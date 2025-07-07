package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStoreSession;
import io.camunda.connector.agenticai.aiagent.memory.runtime.RuntimeMemory;
import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.example.aiagentruntime.memory.conversation.entity.MyConversation;
import io.camunda.example.aiagentruntime.memory.conversation.entity.MyConversationRepository;

import java.util.UUID;

public class MyConversationStoreSession implements ConversationStoreSession<MyConversationContext> {

  private final MyConversationRepository repository;
  private final MyConversationContext previousConversationContext;
  private MyConversation previousConversation;

  public MyConversationStoreSession(
      MyConversationRepository repository, MyConversationContext previousConversationContext) {
    this.repository = repository;
    this.previousConversationContext = previousConversationContext;
  }

  @Override
  public void loadIntoRuntimeMemory(RuntimeMemory runtimeMemory) {
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

    final var conversation = new MyConversation();
    if (previousConversation != null) {
      conversation.setParent(previousConversation);
      conversation.setConversationId(previousConversation.getConversationId());
    } else {
      conversation.setConversationId(UUID.randomUUID());
    }

    conversation.setMessages(runtimeMemory.allMessages());
    repository.saveAndFlush(conversation);

    final var conversationContext =
        new MyConversationContext(
            conversation.getConversationId().toString(), conversation.getId());

    return agentContext.withConversation(conversationContext);
  }
}
