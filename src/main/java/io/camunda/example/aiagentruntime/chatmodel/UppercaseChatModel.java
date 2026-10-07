package io.camunda.example.aiagentruntime.chatmodel;

import io.camunda.connector.agenticai.aiagent.chatmodel.ChatModel;
import io.camunda.connector.agenticai.aiagent.chatmodel.ChatRequest;
import io.camunda.connector.agenticai.aiagent.chatmodel.ChatResult;
import io.camunda.connector.agenticai.aiagent.model.message.AssistantMessage;
import io.camunda.connector.agenticai.aiagent.model.message.content.Content;
import io.camunda.connector.agenticai.aiagent.model.message.content.TextContent;
import java.util.List;

/** Decorates another {@link ChatModel} and upper-cases the text of the assistant response. */
public class UppercaseChatModel implements ChatModel {

  private final ChatModel delegate;

  public UppercaseChatModel(ChatModel delegate) {
    this.delegate = delegate;
  }

  @Override
  public ChatResult execute(ChatRequest request) {
    return switch (delegate.execute(request)) {
      case ChatResult.Completed(var message, var metrics) ->
          new ChatResult.Completed(uppercase(message), metrics);
      case ChatResult.Continuation(var message, var metrics) ->
          new ChatResult.Continuation(uppercase(message), metrics);
    };
  }

  private static AssistantMessage uppercase(AssistantMessage message) {
    final List<Content> content =
        message.content().stream()
            .map(
                c ->
                    c instanceof TextContent text
                        ? (Content) new TextContent(text.text().toUpperCase(), text.metadata())
                        : c)
            .toList();
    return message.withContent(content);
  }

  @Override
  public void close() {
    delegate.close();
  }
}
