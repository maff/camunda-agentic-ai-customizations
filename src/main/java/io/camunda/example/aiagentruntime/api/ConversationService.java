package io.camunda.example.aiagentruntime.api;

import io.camunda.example.aiagentruntime.memory.conversation.MyConversation;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversationRepository;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversationTurn;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversationTurnRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST-facing read service for conversations.
 *
 * <p>The UI's view of "the live head of this conversation" is sourced from the {@code
 * MyConversation.lastKnownHeadId} projection, which is updated post-commit in {@link
 * io.camunda.example.aiagentruntime.memory.conversation.MyConversationStore#onJobCompleted}. Since
 * the projection only advances after Zeebe accepts a job, the UI never displays uncommitted turn
 * data — at worst it's stale (showing the previously committed head) until the next successful
 * turn's callback fires.
 *
 * <p>Production systems that need stronger live-freshness can additionally query the {@code
 * agentContext} process variable from Camunda (e.g., {@code
 * CamundaClient.newVariableSearchRequest()}) and prefer that value over the projection when
 * present.
 */
@Service
@Transactional(readOnly = true)
public class ConversationService {

  private final MyConversationRepository conversationRepository;
  private final MyConversationTurnRepository turnRepository;
  private final ConversationMapper conversationMapper;

  public ConversationService(
      MyConversationRepository conversationRepository,
      MyConversationTurnRepository turnRepository,
      ConversationMapper conversationMapper) {
    this.conversationRepository = conversationRepository;
    this.turnRepository = turnRepository;
    this.conversationMapper = conversationMapper;
  }

  public List<ConversationListDto> listConversations() {
    return conversationRepository.findAllOrderByUpdatedAtDesc().stream()
        .filter(c -> c.getLastKnownHeadId() != null)
        .map(this::toListDto)
        .toList();
  }

  public Optional<ConversationDto> getConversationById(UUID conversationId) {
    return conversationRepository
        .findById(conversationId)
        .filter(c -> c.getLastKnownHeadId() != null)
        .map(this::toDto);
  }

  private ConversationListDto toListDto(MyConversation conversation) {
    final var rootTurn = turnRepository.findRootTurn(conversation.getId()).orElse(null);
    return conversationMapper.toListDto(conversation, rootTurn);
  }

  private ConversationDto toDto(MyConversation conversation) {
    final List<MyConversationTurn> chain =
        turnRepository.findChainByHeadId(conversation.getLastKnownHeadId());
    return conversationMapper.toDto(conversation, chain);
  }
}
