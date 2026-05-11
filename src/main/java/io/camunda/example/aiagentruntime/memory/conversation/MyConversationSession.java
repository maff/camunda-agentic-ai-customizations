package io.camunda.example.aiagentruntime.memory.conversation;

import static io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationUtil.loadConversationContext;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationContext;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationLoadResult;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationSession;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStoreRequest;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.connector.agenticai.aiagent.model.AgentExecutionContext;
import io.camunda.connector.agenticai.model.message.Message;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

/**
 * Per-turn session for the JPA-backed conversation store.
 *
 * <p>{@link #loadMessages} walks the turn chain from the head pointer in the incoming {@link
 * MyConversationContext} back to the root and concatenates the per-turn message deltas.
 *
 * <p>{@link #storeMessages} appends a single new {@link MyConversationTurn} row containing only the
 * messages added during this turn (delta), parented to the previous head. The previous turn is
 * never mutated, so a failed job completion leaves the new row as an invisible orphan — cleaned up
 * by {@link MyConversationStore#onJobCompletionFailed} on a best-effort basis.
 */
public class MyConversationSession implements ConversationSession {

  private final MyConversationRepository conversationRepository;
  private final MyConversationTurnRepository turnRepository;
  private final AgentExecutionContext executionContext;

  private MyConversationContext previousContext;
  private int loadedMessageCount;

  public MyConversationSession(
      MyConversationRepository conversationRepository,
      MyConversationTurnRepository turnRepository,
      AgentExecutionContext executionContext) {
    this.conversationRepository = conversationRepository;
    this.turnRepository = turnRepository;
    this.executionContext = executionContext;
  }

  @Override
  public ConversationLoadResult loadMessages(AgentContext agentContext) {
    previousContext = loadConversationContext(agentContext, MyConversationContext.class);
    if (previousContext == null) {
      loadedMessageCount = 0;
      return ConversationLoadResult.empty();
    }

    final var chain = turnRepository.findChainByHeadId(previousContext.recordId());
    final List<Message> messages = new ArrayList<>();
    for (final var turn : chain) {
      messages.addAll(turn.getMessages());
    }
    loadedMessageCount = messages.size();
    return ConversationLoadResult.of(messages);
  }

  @Override
  @Transactional
  public ConversationContext storeMessages(
      AgentContext agentContext, ConversationStoreRequest request) {
    final var requestMessages = request.messages();
    // Runtime persists allMessages() (the full history). The delta for this turn is everything
    // appended after what we loaded.
    final var deltaStart = Math.min(loadedMessageCount, requestMessages.size());
    final var deltaMessages =
        new ArrayList<>(requestMessages.subList(deltaStart, requestMessages.size()));

    final UUID conversationId;
    final UUID parentTurnId;
    if (previousContext == null) {
      // First turn: create the parent conversation row. Its UUIDv7 id becomes the conversationId.
      final var conversation =
          conversationRepository.save(
              new MyConversation(
                  MyConversation.MyConversationJobContext.from(executionContext.jobContext())));
      conversationId = conversation.getId();
      parentTurnId = null;
    } else {
      conversationId = UUID.fromString(previousContext.conversationId());
      parentTurnId = previousContext.recordId();
    }

    final var turn =
        new MyConversationTurn(
            conversationId,
            parentTurnId,
            executionContext.jobContext().getElementInstanceKey(),
            ZonedDateTime.now(),
            deltaMessages);
    final var savedTurn = turnRepository.save(turn);

    return new MyConversationContext(conversationId.toString(), savedTurn.getId());
  }
}
