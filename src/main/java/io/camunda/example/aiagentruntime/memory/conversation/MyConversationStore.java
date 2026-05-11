package io.camunda.example.aiagentruntime.memory.conversation;

import static io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationUtil.loadConversationContext;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationSession;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStore;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.connector.agenticai.aiagent.model.AgentExecutionContext;
import io.camunda.connector.api.outbound.JobCompletionFailure;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom conversation store that persists chat history in a Postgres database via JPA.
 *
 * <p>Usage: select "Custom Implementation" as the memory storage type and specify {@code
 * my-conversation} as the implementation type in the agent configuration properties.
 *
 * <h3>Storage shape</h3>
 *
 * Two tables:
 *
 * <ul>
 *   <li>{@code conversations}: one row per logical conversation. Holds process metadata and a
 *       {@code last_known_head_id} projection updated post-commit (see below).
 *   <li>{@code conversation_turns}: append-only chain. Each turn stores only the messages added
 *       during that turn (delta) and a {@code parent_id} pointer to its predecessor.
 * </ul>
 *
 * <h3>Contract compliance</h3>
 *
 * Follows the write-ahead with pointer-based visibility contract:
 *
 * <ul>
 *   <li>{@code storeMessages} only inserts a new {@code conversation_turns} row; it never mutates
 *       the previous turn or anything else that the incoming {@code ConversationContext} points at.
 *   <li>If Zeebe rejects the job completion, the newly written turn becomes an orphan — invisible
 *       because nothing references it. {@link #onJobCompletionFailed} best-effort deletes it.
 *   <li>{@link #onJobCompleted} updates {@code conversations.last_known_head_id} to the committed
 *       turn id. This is the UI's source of truth for "the live head of this conversation".
 * </ul>
 *
 * <p><b>Note on the UI projection:</b> {@code last_known_head_id} is updated via {@link
 * #onJobCompleted}, which is best-effort per the SPI. In the rare case a callback does not fire
 * (e.g., worker crash between Zeebe ack and callback dispatch), the projection will lag — the UI
 * will show the previous turn's content until the next successful turn updates it. Production
 * systems that need stronger live-freshness can additionally query the {@code agentContext} process
 * variable via {@code CamundaClient.newVariableSearchRequest()} and prefer that value over the
 * projection.
 */
@Component
public class MyConversationStore implements ConversationStore {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyConversationStore.class);

  public static final String TYPE = "my-conversation";

  private final MyConversationRepository conversationRepository;
  private final MyConversationTurnRepository turnRepository;

  public MyConversationStore(
      MyConversationRepository conversationRepository,
      MyConversationTurnRepository turnRepository) {
    this.conversationRepository = conversationRepository;
    this.turnRepository = turnRepository;
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public ConversationSession createSession(
      AgentExecutionContext executionContext, AgentContext agentContext) {
    return new MyConversationSession(conversationRepository, turnRepository, executionContext);
  }

  @Override
  @Transactional
  public void onJobCompleted(
      AgentExecutionContext executionContext, AgentContext committedContext) {
    final var ctx = loadConversationContext(committedContext, MyConversationContext.class);
    if (ctx == null) {
      return;
    }

    conversationRepository
        .findById(UUID.fromString(ctx.conversationId()))
        .ifPresent(
            conversation -> {
              conversation.setLastKnownHeadId(ctx.recordId());
              conversationRepository.save(conversation);
            });
  }

  @Override
  @Transactional
  public void onJobCompletionFailed(
      AgentExecutionContext executionContext,
      AgentContext failedContext,
      JobCompletionFailure failure) {
    final var ctx = loadConversationContext(failedContext, MyConversationContext.class);
    if (ctx == null) {
      return;
    }

    // The turn referenced by `ctx.recordId()` was written by storeMessages during this job but
    // never became part of a committed chain because Zeebe rejected the job completion. Delete
    // it so the orphan does not accumulate.
    try {
      turnRepository.deleteById(ctx.recordId());
    } catch (Exception e) {
      LOGGER.warn("Failed to delete orphaned conversation turn {}", ctx.recordId(), e);
    }
  }
}
