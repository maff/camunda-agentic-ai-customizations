package io.camunda.example.aiagentruntime.memory.conversation.entity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyConversationRepository extends JpaRepository<MyConversation, UUID> {}
