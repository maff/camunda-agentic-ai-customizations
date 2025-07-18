package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationSession;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationUtil;
import io.camunda.connector.agenticai.aiagent.memory.runtime.RuntimeMemory;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.connector.agenticai.aiagent.model.AgentJobContext;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversation.MyConversationJobContext;
import java.util.UUID;

public class MyConversationSession implements ConversationSession {

  private final MyConversationRepository repository;
  private final AgentJobContext jobContext;

  private MyConversationContext previousConversationContext;
  private MyConversation previousConversation;

  public MyConversationSession(MyConversationRepository repository, AgentJobContext jobContext) {
    this.repository = repository;
    this.jobContext = jobContext;
  }

  @Override
  public void loadIntoRuntimeMemory(AgentContext agentContext, RuntimeMemory runtimeMemory) {
    previousConversationContext =
        ConversationUtil.loadConversationContext(agentContext, MyConversationContext.class);
    if (previousConversationContext == null) {
      return;
    }

    previousConversation =
        repository
            .findById(previousConversationContext.recordId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Failed to find previous conversation with ID: "
                            + previousConversationContext.recordId()));

    previousConversation.getMessages().forEach(runtimeMemory::addMessage);
  }

  @Override
  public AgentContext storeFromRuntimeMemory(
      AgentContext agentContext, RuntimeMemory runtimeMemory) {
    MyConversationJobContext conversationJobContext = MyConversationJobContext.from(jobContext);

    MyConversation conversation;
    if (previousConversation != null) {
      previousConversation.setArchived(true);
      repository.save(previousConversation);

      conversation =
          new MyConversation(
              previousConversation.getConversationId(),
              conversationJobContext,
              previousConversation);
    } else {
      conversation = new MyConversation(UUID.randomUUID(), conversationJobContext);
    }

    conversation.setMessages(runtimeMemory.allMessages());
    repository.save(conversation);

    if (previousConversation != null) {
      repository.deleteAllPreviousEntries(
          previousConversation.getConversationId(), previousConversation);
    }

    repository.flush();

    return agentContext.withConversation(
        new MyConversationContext(
            conversation.getConversationId().toString(), conversation.getId()));
  }
}
