package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStore;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStoreFactory;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.connector.agenticai.aiagent.model.request.AgentRequest;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import org.springframework.stereotype.Component;

@Component
public class MyConversationStoreFactory implements ConversationStoreFactory {

  private final MyConversationStore store;

  public MyConversationStoreFactory(MyConversationStore store) {
    this.store = store;
  }

  @Override
  public ConversationStore createConversationStore(
      OutboundConnectorContext outboundConnectorContext,
      AgentRequest agentRequest,
      AgentContext agentContext) {
    return store;
  }
}
