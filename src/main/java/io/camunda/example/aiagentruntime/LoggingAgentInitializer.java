package io.camunda.example.aiagentruntime;

import io.camunda.connector.agenticai.aiagent.agent.AgentInitializationResult;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializer;
import io.camunda.connector.agenticai.aiagent.model.AgentExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorates the connector's {@link AgentInitializer} with logging. It only depends on the {@link
 * AgentInitializer} interface and delegates to whatever implementation the connector provides, see
 * {@link AgentInitializerDecoratingBeanPostProcessor}.
 */
public class LoggingAgentInitializer implements AgentInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAgentInitializer.class);

  private final AgentInitializer delegate;

  public LoggingAgentInitializer(AgentInitializer delegate) {
    this.delegate = delegate;
  }

  @Override
  public AgentInitializationResult initializeAgent(AgentExecutionContext executionContext) {
    LOGGER.info(">>> Initializing agent");

    final var result = delegate.initializeAgent(executionContext);

    // log the result type only: the result contains the agent context and tool call results
    LOGGER.info("<<< Agent initialized. Result: {}", result.getClass().getSimpleName());

    return result;
  }
}
