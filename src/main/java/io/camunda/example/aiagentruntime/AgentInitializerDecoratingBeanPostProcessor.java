package io.camunda.example.aiagentruntime;

import io.camunda.connector.agenticai.aiagent.agent.AgentInitializer;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Wraps the connector's default {@link AgentInitializer} bean in a {@link LoggingAgentInitializer}.
 *
 * <p>Decorating the existing bean avoids constructing the connector's implementation class
 * ourselves, whose constructor can change between releases.
 */
@Component
public class AgentInitializerDecoratingBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (bean instanceof AgentInitializer agentInitializer
        && !(bean instanceof LoggingAgentInitializer)) {
      return new LoggingAgentInitializer(agentInitializer);
    }

    return bean;
  }
}
