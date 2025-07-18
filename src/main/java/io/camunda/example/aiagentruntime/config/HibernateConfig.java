package io.camunda.example.aiagentruntime.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

  @Bean
  public HibernatePropertiesCustomizer jsonFormatMapperCustomizer() {
    return (properties) -> {
      // the connectors SDK includes the Jackson Scala module which uses scala
      // types when deserializing maps - register a custom ObjectMapper explicitely
      // ignoring the Scala module to avoid issues with deserializing the serialized
      // message content
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectMapper.findModules().stream()
          .filter(module -> !module.getModuleName().equals("DefaultScalaModule"))
          .forEach(objectMapper::registerModule);

      properties.put(
          AvailableSettings.JSON_FORMAT_MAPPER, new JacksonJsonFormatMapper(objectMapper));
    };
  }
}
