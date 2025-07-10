package io.camunda.example.aiagentruntime;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class ConversationContextSubTypesBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof ObjectMapper objectMapper) {
      // register custom subtype for our conversation context class
      objectMapper.registerSubtypes(MyConversationContext.class);
    }

    return bean;
  }
}
