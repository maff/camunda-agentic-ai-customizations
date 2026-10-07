package io.camunda.example.aiagentruntime.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.runtime.annotation.ConnectorsObjectMapper;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

  /**
   * Uses the connector runtime's ObjectMapper to (de-)serialize the stored messages as JSON.
   *
   * <p>Messages can contain connector types such as document references, which need the runtime's
   * Jackson modules to be (de-)serialized.
   */
  @Bean
  public HibernatePropertiesCustomizer jsonFormatMapperCustomizer(
      @ConnectorsObjectMapper ObjectMapper connectorObjectMapper) {
    return (properties) ->
        properties.put(
            AvailableSettings.JSON_FORMAT_MAPPER,
            new JacksonJsonFormatMapper(connectorObjectMapper));
  }
}
