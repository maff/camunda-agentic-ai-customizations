package io.camunda.example.aiagentruntime.ui;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SpaController {
  @GetMapping("/conversation/{uuid}")
  public String conversation(@PathVariable UUID uuid) {
    return "forward:/index.html";
  }
}
