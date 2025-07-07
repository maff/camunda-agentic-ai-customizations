package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.memory.conversation.BaseConversationStore;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStoreSession;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStoreSessionHandler;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.connector.agenticai.aiagent.model.AgentResponse;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.example.aiagentruntime.memory.conversation.entity.MyConversationRepository;
import io.camunda.example.aiagentruntime.memory.conversation.entity.ProcessContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MyConversationStore extends BaseConversationStore<MyConversationContext> {

  private final MyConversationRepository repository;

  public MyConversationStore(MyConversationRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<MyConversationContext> conversationContextClass() {
    return MyConversationContext.class;
  }

  @Override
  @Transactional
  public AgentResponse executeInSession(
      OutboundConnectorContext context,
      AgentContext agentContext,
      ConversationStoreSessionHandler<MyConversationContext> conversationStoreSessionHandler) {
    return super.executeInSession(context, agentContext, conversationStoreSessionHandler);
  }

  @Override
  protected ConversationStoreSession<MyConversationContext> createSession(
      OutboundConnectorContext outboundConnectorContext,
      AgentContext agentContext,
      MyConversationContext previousConversationContext) {
    final var jobContext = outboundConnectorContext.getJobContext();
    final var processContext =
        new ProcessContext(
            String.valueOf(jobContext.getProcessDefinitionKey()),
            String.valueOf(jobContext.getProcessInstanceKey()),
            jobContext.getElementId(),
            String.valueOf(jobContext.getElementInstanceKey()));

    return new MyConversationStoreSession(repository, processContext, previousConversationContext);
  }
}
