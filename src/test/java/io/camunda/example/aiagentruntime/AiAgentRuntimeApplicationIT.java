package io.camunda.example.aiagentruntime;

import io.camunda.process.test.api.CamundaSpringProcessTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@CamundaSpringProcessTest
@Import(TestcontainersConfiguration.class)
class AiAgentRuntimeApplicationIT {

  @Test
  void contextLoads() {}
}
