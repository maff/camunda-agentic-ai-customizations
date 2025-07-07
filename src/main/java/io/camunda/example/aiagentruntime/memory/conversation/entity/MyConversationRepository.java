package io.camunda.example.aiagentruntime.memory.conversation.entity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MyConversationRepository extends JpaRepository<MyConversation, UUID> {

  @Modifying
  @Query(
      "DELETE FROM MyConversation c WHERE c.conversationId = :conversationId AND c != :previous AND c.archived = true")
  void deleteAllPreviousEntries(UUID conversationId, MyConversation previous);
}
