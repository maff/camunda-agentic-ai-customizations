package io.camunda.example.aiagentruntime.api;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

  private final ConversationService conversationService;

  @Autowired
  public ConversationController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }

  @GetMapping
  public List<ConversationListDto> getAllConversations() {
    return conversationService.getAllNonArchivedConversations();
  }

  @GetMapping("/{conversationId}")
  public ResponseEntity<ConversationDto> getConversation(@PathVariable UUID conversationId) {
    return conversationService
        .getConversationById(conversationId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
