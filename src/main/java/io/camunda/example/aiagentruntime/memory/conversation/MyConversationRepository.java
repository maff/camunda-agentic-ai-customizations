package io.camunda.example.aiagentruntime.memory.conversation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyConversationRepository extends JpaRepository<MyConversation, UUID> {

  @Query("SELECT c FROM MyConversation c ORDER BY c.updatedAt DESC")
  List<MyConversation> findAllOrderByUpdatedAtDesc();
}
