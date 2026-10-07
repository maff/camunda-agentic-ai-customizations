package io.camunda.example.aiagentruntime.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.document.DocumentFactory;
import io.camunda.connector.document.jackson.JacksonModuleDocumentDeserializer;
import io.camunda.connector.document.jackson.JacksonModuleDocumentSerializer;
import io.camunda.connector.runtime.core.intrinsic.DisabledIntrinsicFunctionExecutor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

  @Bean
  public HibernatePropertiesCustomizer jsonFormatMapperCustomizer(DocumentFactory documentFactory) {
    return (properties) ->
        properties.put(
            AvailableSettings.JSON_FORMAT_MAPPER,
            new JacksonJsonFormatMapper(createMessageObjectMapper(documentFactory)));
  }

  /**
   * Creates the ObjectMapper used to (de-)serialize the stored messages as JSON.
   *
   * <p>Messages can contain documents (e.g. documents added to the user prompt or returned by
   * tools). Documents are stored as references and need the connector runtime's document modules to
   * be (de-)serialized. Intrinsic function execution is disabled, as stored messages are data and
   * not expressions.
   */
  public static ObjectMapper createMessageObjectMapper(DocumentFactory documentFactory) {
    // the connectors SDK includes the Jackson Scala module which uses scala
    // types when deserializing maps - register a custom ObjectMapper explicitely
    // ignoring the Scala module to avoid issues with deserializing the serialized
    // message content
    final ObjectMapper objectMapper = new ObjectMapper();
    ObjectMapper.findModules().stream()
        .filter(module -> !module.getModuleName().equals("DefaultScalaModule"))
        .forEach(objectMapper::registerModule);

    objectMapper.registerModules(
        new JacksonModuleDocumentSerializer(),
        new JacksonModuleDocumentDeserializer(
            documentFactory, new DisabledIntrinsicFunctionExecutor()));
    return objectMapper;
  }
}
