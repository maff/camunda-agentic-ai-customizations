package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationSessionHandler;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStore;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.connector.agenticai.aiagent.model.AgentResponse;
import io.camunda.connector.agenticai.aiagent.model.request.AgentRequest;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.example.aiagentruntime.memory.conversation.entity.MyConversationRepository;
import io.camunda.example.aiagentruntime.memory.conversation.entity.ProcessContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MyConversationStore implements ConversationStore {

  private final MyConversationRepository repository;

  public MyConversationStore(MyConversationRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public AgentResponse executeInSession(
      OutboundConnectorContext outboundConnectorContext,
      AgentRequest agentRequest,
      AgentContext agentContext,
      ConversationSessionHandler sessionHandler) {
    final var jobContext = outboundConnectorContext.getJobContext();
    final var processContext =
        new ProcessContext(
            String.valueOf(jobContext.getProcessDefinitionKey()),
            String.valueOf(jobContext.getProcessInstanceKey()),
            jobContext.getElementId(),
            String.valueOf(jobContext.getElementInstanceKey()));

    final var session = new MyConversationSession(repository, processContext);

    return sessionHandler.handleSession(session);
  }
}
