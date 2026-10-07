package io.camunda.example.aiagentruntime.chatmodel;

import io.camunda.connector.agenticai.aiagent.chatmodel.ChatModel;
import io.camunda.connector.agenticai.aiagent.chatmodel.ChatModelConfiguration;
import io.camunda.connector.agenticai.aiagent.chatmodel.ChatModelFactory;
import io.camunda.connector.agenticai.aiagent.chatmodel.provider.openai.OpenAiChatModelFactory;
import io.camunda.connector.agenticai.aiagent.model.request.v2.CustomProviderConfiguration;
import io.camunda.connector.agenticai.aiagent.model.request.v2.OpenAiChatModelConfiguration;
import io.camunda.connector.agenticai.aiagent.model.request.v2.OpenAiChatModelConfiguration.OpenAiApi.OpenAiResponsesApi;
import io.camunda.connector.agenticai.aiagent.model.request.v2.OpenAiChatModelConfiguration.OpenAiBackend.OpenAiApiBackend;
import io.camunda.connector.agenticai.aiagent.model.request.v2.OpenAiChatModelConfiguration.OpenAiBackend.OpenAiApiBackend.OpenAiApiConnection;
import io.camunda.connector.agenticai.aiagent.model.request.v2.OpenAiChatModelConfiguration.OpenAiConnection;
import io.camunda.connector.agenticai.aiagent.model.request.v2.OpenAiChatModelConfiguration.OpenAiModel;
import io.camunda.connector.api.error.ConnectorInputException;
import org.springframework.stereotype.Component;

/**
 * Example of a custom chat model provider. It handles the {@code custom} provider type with the
 * provider type {@code uppercase}: the model call is delegated to the native OpenAI provider and
 * the assistant's text is upper-cased by {@link UppercaseChatModel}.
 *
 * <p>Usage: select "Custom Implementation" as the provider in the agent configuration and set
 *
 * <ul>
 *   <li>Provider type: {@code uppercase}
 *   <li>Model: an OpenAI model ID, e.g. {@code gpt-5.4-mini}
 *   <li>Provider parameters: {@code = {apiKey: "{{secrets.OPENAI_API_KEY}}"}}
 * </ul>
 *
 * <p>The delegate is the native {@link OpenAiChatModelFactory} bean, called directly. Going through
 * the {@code ChatModelRegistry} would not work here: the registry fails if more than one factory
 * supports a configuration, and it would also resolve this factory again.
 */
@Component
public class UppercaseChatModelFactory implements ChatModelFactory {

  public static final String PROVIDER_TYPE = "uppercase";

  private final OpenAiChatModelFactory openAiChatModelFactory;

  public UppercaseChatModelFactory(OpenAiChatModelFactory openAiChatModelFactory) {
    this.openAiChatModelFactory = openAiChatModelFactory;
  }

  @Override
  public boolean supports(ChatModelConfiguration configuration) {
    return configuration instanceof CustomProviderConfiguration custom
        && PROVIDER_TYPE.equals(custom.providerType());
  }

  @Override
  public ChatModel create(ChatModelConfiguration configuration) {
    final var custom = (CustomProviderConfiguration) configuration;
    final var apiKey = custom.parameters() == null ? null : custom.parameters().get("apiKey");
    if (!(apiKey instanceof String key) || key.isBlank()) {
      throw new ConnectorInputException(
          "Provider parameter 'apiKey' is required for custom provider type '%s'"
              .formatted(PROVIDER_TYPE));
    }

    final var openAiConfiguration =
        new OpenAiChatModelConfiguration(
            new OpenAiConnection(
                new OpenAiResponsesApi(null),
                new OpenAiApiBackend(
                    new OpenAiApiConnection(key, null, null, null, null, null, null)),
                new OpenAiModel(custom.model()),
                null));

    return new UppercaseChatModel(openAiChatModelFactory.create(openAiConfiguration));
  }
}
