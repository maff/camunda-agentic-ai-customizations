package io.camunda.example.aiagentruntime;

import io.camunda.connector.agenticai.adhoctoolsschema.resolver.AdHocToolsSchemaResolver;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializationResult;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializer;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializerImpl;
import io.camunda.connector.agenticai.aiagent.model.request.AgentRequest;
import io.camunda.connector.agenticai.aiagent.tool.GatewayToolHandlerRegistry;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyCustomAiAgentInitializer implements AgentInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomAiAgentInitializer.class);

  private final AgentInitializer delegate;

  public MyCustomAiAgentInitializer(
      AdHocToolsSchemaResolver schemaResolver, GatewayToolHandlerRegistry gatewayToolHandlers) {
    this.delegate = new AgentInitializerImpl(schemaResolver, gatewayToolHandlers);
  }

  @Override
  public AgentInitializationResult initializeAgent(
      OutboundConnectorContext outboundConnectorContext, AgentRequest agentRequest) {
    LOGGER.info(
        ">>> Initializing agent. Context: {}, Request: {}", outboundConnectorContext, agentRequest);

    final var result = delegate.initializeAgent(outboundConnectorContext, agentRequest);

    LOGGER.info("<<< Agent initialized. Result: {}", result);

    return result;
  }
}
