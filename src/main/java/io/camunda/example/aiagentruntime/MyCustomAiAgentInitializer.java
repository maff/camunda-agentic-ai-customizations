package io.camunda.example.aiagentruntime;

import io.camunda.connector.agenticai.adhoctoolsschema.resolver.AdHocToolsSchemaResolver;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializationResult;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializer;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializerImpl;
import io.camunda.connector.agenticai.aiagent.model.AgentExecutionContext;
import io.camunda.connector.agenticai.aiagent.tool.GatewayToolHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Example of a custom agent initializer. */
@Component
public class MyCustomAiAgentInitializer implements AgentInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomAiAgentInitializer.class);

  private final AgentInitializer delegate;

  public MyCustomAiAgentInitializer(
      AdHocToolsSchemaResolver schemaResolver, GatewayToolHandlerRegistry gatewayToolHandlers) {
    this.delegate = new AgentInitializerImpl(schemaResolver, gatewayToolHandlers);
  }

  @Override
  public AgentInitializationResult initializeAgent(AgentExecutionContext executionContext) {
    LOGGER.info(
        ">>> Initializing agent. Context: {}, Request: {}",
        executionContext.jobContext(),
        executionContext.request());

    final var result = delegate.initializeAgent(executionContext);

    LOGGER.info("<<< Agent initialized. Result: {}", result);

    return result;
  }
}
