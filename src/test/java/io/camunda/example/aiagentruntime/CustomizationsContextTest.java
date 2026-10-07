package io.camunda.example.aiagentruntime;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializer;
import io.camunda.connector.agenticai.aiagent.chatmodel.ChatModelRegistry;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationContext;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationStore;
import io.camunda.connector.agenticai.aiagent.model.request.v2.CustomProviderConfiguration;
import io.camunda.example.aiagentruntime.chatmodel.UppercaseChatModel;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversationContext;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversationStore;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Verifies that the customizations are wired into the AI Agent connector's Spring context: the
 * custom initializer replaces the default, the custom store and chat model factory are discovered
 * by their registries, and the custom conversation context survives a JSON round trip on the
 * connector runtime's object mappers.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class CustomizationsContextTest {

  @Container @ServiceConnection
  static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18");

  @Autowired ApplicationContext context;
  @Autowired AgentInitializer agentInitializer;
  @Autowired List<ConversationStore> conversationStores;
  @Autowired ChatModelRegistry chatModelRegistry;

  @Test
  void customInitializerReplacesDefault() {
    assertThat(agentInitializer).isInstanceOf(MyCustomAgentInitializer.class);
  }

  @Test
  void customStoreIsRegistered() {
    assertThat(conversationStores)
        .filteredOn(store -> MyConversationStore.TYPE.equals(store.type()))
        .singleElement()
        .isInstanceOf(MyConversationStore.class);
  }

  @Test
  void customChatModelIsResolvedForCustomProvider() {
    final var configuration =
        new CustomProviderConfiguration(
            "uppercase", "gpt-5.4-mini", Map.of("apiKey", "not-a-real-key"));

    try (final var chatModel = chatModelRegistry.resolve(configuration)) {
      assertThat(chatModel).isInstanceOf(UppercaseChatModel.class);
    }
  }

  @Test
  void customConversationContextRoundTripsOnAllRuntimeMappers() throws Exception {
    // the mappers the connector runtime binds job variables and agent context with
    final var mappers =
        List.of(
            context.getBean("connectorObjectMapper", ObjectMapper.class),
            context.getBean("outboundConnectorObjectMapper", ObjectMapper.class));

    final ConversationContext original =
        new MyConversationContext(UUID.randomUUID().toString(), UUID.randomUUID());
    for (final var mapper : mappers) {
      final var json = mapper.writeValueAsString(original);
      assertThat(mapper.readValue(json, ConversationContext.class)).isEqualTo(original);
    }
  }
}
