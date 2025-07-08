package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationSession;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationUtil;
import io.camunda.connector.agenticai.aiagent.memory.runtime.RuntimeMemory;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.example.aiagentruntime.memory.conversation.entity.MyConversation;
import io.camunda.example.aiagentruntime.memory.conversation.entity.MyConversationRepository;
import io.camunda.example.aiagentruntime.memory.conversation.entity.ProcessContext;
import java.util.UUID;

public class MyConversationSession implements ConversationSession {

  private final MyConversationRepository repository;
  private final ProcessContext processContext;

  private MyConversationContext previousConversationContext;
  private MyConversation previousConversation;

  public MyConversationSession(MyConversationRepository repository, ProcessContext processContext) {
    this.repository = repository;
    this.processContext = processContext;
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

    UUID conversationId;
    if (previousConversation != null) {
      conversationId = previousConversation.getConversationId();
      previousConversation.setArchived(true);
      repository.save(previousConversation);
    } else {
      conversationId = UUID.randomUUID();
    }

    final var conversation = new MyConversation(conversationId, processContext);
    conversation.setMessages(runtimeMemory.allMessages());

    repository.save(conversation);
    repository.flush();

    if (previousConversation != null) {
      repository.deleteAllPreviousEntries(
          previousConversation.getConversationId(), previousConversation);
    }

    final var conversationContext =
        new MyConversationContext(
            conversation.getConversationId().toString(), conversation.getId());

    return agentContext.withConversation(conversationContext);
  }
}
