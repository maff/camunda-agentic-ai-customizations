package io.camunda.example.aiagentruntime.memory.conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MyConversationTurnRepository extends JpaRepository<MyConversationTurn, UUID> {

  /**
   * Walks the parent chain starting from {@code headId} back to the root turn and returns the rows
   * in chronological (root → head) order. Uses a recursive CTE to do the walk in a single
   * round-trip.
   */
  @Query(
      value =
          """
          WITH RECURSIVE chain AS (
              SELECT * FROM conversation_turns WHERE id = :headId
              UNION ALL
              SELECT p.* FROM conversation_turns p
              JOIN chain c ON p.id = c.parent_id
          )
          SELECT * FROM chain ORDER BY created_at ASC
          """,
      nativeQuery = true)
  List<MyConversationTurn> findChainByHeadId(@Param("headId") UUID headId);

  @Query(
      """
      SELECT t FROM MyConversationTurn t
      WHERE t.conversationId = :conversationId AND t.parentId IS NULL
      ORDER BY t.createdAt ASC
      LIMIT 1
      """)
  Optional<MyConversationTurn> findRootTurn(@Param("conversationId") UUID conversationId);
}
