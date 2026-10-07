package io.camunda.example.aiagentruntime;

import io.camunda.connector.agenticai.aiagent.model.AgentContext;
import io.camunda.connector.agenticai.aiagent.model.AgentExecutionContext;
import io.camunda.connector.agenticai.aiagent.systemprompt.SystemPromptContributor;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Example of a system prompt contributor. Contributors are discovered as Spring beans and appended
 * to the agent's configured system prompt, ordered by {@link #getOrder()}.
 *
 * <p>This one tells the model the current date (LLMs have no clock) and which process it is running
 * in, which is taken from the job of the current execution.
 */
@Component
public class MyCustomSystemPromptContributor implements SystemPromptContributor {

  private final Clock clock = Clock.systemDefaultZone();

  @Override
  public String contribute(AgentExecutionContext executionContext, AgentContext agentContext) {
    return "Today's date is %s. You are running as part of the process '%s'."
        .formatted(LocalDate.now(clock), executionContext.jobContext().getBpmnProcessId());
  }
}
