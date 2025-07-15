package io.camunda.example.aiagentruntime.api;

import io.camunda.example.aiagentruntime.memory.conversation.MyConversationRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConversationService {

  private final MyConversationRepository conversationRepository;
  private final ConversationMapper conversationMapper;

  @Autowired
  public ConversationService(
      MyConversationRepository conversationRepository, ConversationMapper conversationMapper) {
    this.conversationRepository = conversationRepository;
    this.conversationMapper = conversationMapper;
  }

  public List<ConversationListDto> getAllNonArchivedConversations() {
    return conversationRepository.findByArchivedFalseOrderByUpdatedAtDesc().stream()
        .map(conversationMapper::toListDto)
        .toList();
  }

  public Optional<ConversationDto> getConversationById(UUID conversationId) {
    return conversationRepository
        .findByConversationIdAndArchivedFalse(conversationId)
        .map(conversationMapper::toDto);
  }
}
