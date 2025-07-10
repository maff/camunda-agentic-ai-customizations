package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationSessionHandler;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStore;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.connector.agenticai.aiagent.model.AgentExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom conversation store implementation that uses a repository to manage conversation data.
 *
 * <p>Usage: select "Custom Implementation" as memory storage type and specify "my-conversation" as
 * implementation type in the agent configuration properties.
 */
@Component
public class MyConversationStore implements ConversationStore {

  public static final String TYPE = "my-conversation";

  private final MyConversationRepository repository;

  public MyConversationStore(MyConversationRepository repository) {
    this.repository = repository;
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  @Transactional
  public <T> T executeInSession(
      AgentExecutionContext executionContext,
      AgentContext agentContext,
      ConversationSessionHandler<T> sessionHandler) {
    final var session = new MyConversationSession(repository, executionContext.jobContext());
    return sessionHandler.handleSession(session);
  }
}
