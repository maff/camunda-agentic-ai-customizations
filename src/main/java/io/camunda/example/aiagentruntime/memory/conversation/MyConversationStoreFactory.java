package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationContext;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStore;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStoreFactory;
import io.camunda.connector.agenticai.aiagent.model.request.AgentRequest;
import org.springframework.stereotype.Component;

@Component
public class MyConversationStoreFactory implements ConversationStoreFactory {

  private final MyConversationStore store;

  public MyConversationStoreFactory(MyConversationStore store) {
    this.store = store;
  }

  @Override
  public <C extends ConversationContext> ConversationStore<C> createConversationStore(
      AgentRequest agentRequest) {
    return (ConversationStore<C>) store;
  }
}
