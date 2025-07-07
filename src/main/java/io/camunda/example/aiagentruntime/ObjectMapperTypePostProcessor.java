package io.camunda.example.aiagentruntime;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperTypePostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof ObjectMapper objectMapper) {
      configureObjectMapper(objectMapper);
    }

    return bean;
  }

  private void configureObjectMapper(ObjectMapper objectMapper) {
    objectMapper.registerSubtypes(MyConversationContext.class);
  }
}
