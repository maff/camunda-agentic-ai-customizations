[![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
![Compatible with: Camunda 8](https://img.shields.io/badge/Compatible%20with-Camunda%208.8-0072Ce)
[![](https://img.shields.io/badge/Lifecycle-Proof%20of%20Concept-blueviolet)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#proof-of-concept-)

# Camunda AI Agent customizations

> [!WARNING]
> This is a playground project to demonstrate how to customize the Camunda AI Agent connector. It is by no means a production-ready solution.

Test project to demonstrate how to customize the [Camunda AI Agent](https://docs.camunda.io/docs/next/components/connectors/out-of-the-box-connectors/agentic-ai-aiagent/) connector.

## What's included?

- An example of how to override specific parts of the AI Agent connector implementation. See [MyCustomAgentInitializer.java](src/main/java/io/camunda/example/aiagentruntime/MyCustomAgentInitializer.java)
- A custom storage implementation for the AI Agent connector that stores conversations in a Postgres database. See [MyConversation.java](src/main/java/io/camunda/example/aiagentruntime/memory/conversation/MyConversation.java)
    - This comes paired with a minimal UI to browse and follow conversations stored in the custom storage implementation.
- An example MCP client configuration in the `dev-mcp-client` Spring Boot profile

## Prerequisites

> [!IMPORTANT]
> Custom storage support for the AI Agent connector is available starting from Camunda 8.8.0-alpha7. For this example you need
> to use a current SNAPSHOT build or a released version of 8.8.0-alpha7 or higher.

- A Camunda 8.8.0 cluster (for example,
  using [Camunda 8 Run](https://docs.camunda.io/docs/next/self-managed/quickstart/developer-quickstart/c8run/))
- Java 24 or higher
- A running Docker environment
- The hybrid AI Agent connector element template:
    - [AI Agent Process connector](https://raw.githubusercontent.com/camunda/connectors/refs/heads/main/connectors/agentic-ai/element-templates/hybrid/agenticai-aiagent-job-worker-hybrid.json)
    - [AI Agent Task connector](https://raw.githubusercontent.com/camunda/connectors/refs/heads/main/connectors/agentic-ai/element-templates/hybrid/agenticai-aiagent-outbound-connector-hybrid.json)

## Running the example

The `dev` Spring Boot profile is configured to connect to a locally running Camunda 8 Cluster. Adapt the config files in
`src/main/resources` to your needs if necessary.

When starting the example application via Maven, it will automatically start a Postgres database container through
Spring Boot's `docker compose` support.

```bash
# build the project
mvn clean package

# set a custom AI Agent Process connector type
export CONNECTOR_AI_AGENT_JOB_WORKER_TYPE=io.camunda.agenticai:aiagent-job-worker:hybrid1

# set a custom AI Agent Task connector type
export CONNECTOR_AI_AGENT_TYPE=io.camunda.agenticai:aiagent:hybrid1

# export any env variables which should be available as secrets to the AI Agent connector
export OPENAI_API_KEY=your_openai_api_key

# run the example in the dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

After starting the application, you can access the conversation UI at [http://localhost:8181](http://localhost:8181).

### Configuring your process to use your custom runtime

Configuration depends on whether you want to use the **AI Agent Process** (applied to an ad-hoc sub-process) or the **AI Agent Task** (applied to a service task) implementation.

#### AI Agent Process

In a process using
the [AI Agent Process connector](https://docs.camunda.io/docs/8.8/components/connectors/out-of-the-box-connectors/agentic-ai-aiagent-process/),
apply the following changes. You can start from one of
the [chat agent examples](https://github.com/camunda/connectors/tree/main/connectors/agentic-ai/examples/ai-agent/ad-hoc-sub-process/ai-agent-chat-with-tools)
provided with the AI Agent connector implementation.

- Apply the `Hybrid AI Agent Process` element template to your AI Agent ad-hoc sub-process to override the task definition type.
- Set Task definition type to `io.camunda.agenticai:aiagent-job-worker:hybrid1` (or the value of the `CONNECTOR_AI_AGENT_JOB_WORKER_TYPE` environment variable in case you used another value).

#### AI Agent Task

In a process using
the [AI Agent Task connector](https://docs.camunda.io/docs/8.8/components/connectors/out-of-the-box-connectors/agentic-ai-aiagent-task/),
apply the following changes. You can start from one of
the [chat agent examples](https://github.com/camunda/connectors/tree/main/connectors/agentic-ai/examples/ai-agent/service-task/ai-agent-chat-with-tools)
provided with the AI Agent connector implementation.

- Apply the `Hybrid AI Agent Process` element template to your AI Agent task to override the task definition type.
- Set Task definition type to `io.camunda.agenticai:aiagent:hybrid1` (or the value of the `CONNECTOR_AI_AGENT_TYPE` environment variable in case you used another value).

### Configuring the AI Agent to use the custom storage

The following applies to both AI Agent implementations:

- In the `Memory` group, set the `Memory storage type` to `Custom Implementation (Hybrid/Self-Managed only)`.
- Configure the `Implementation type` to the type exposed by the custom store implementation: `my-conversation`

After applying these changes, you should be able to start a process and follow the conversation on the UI exposed by this project.

## Frontend Development Setup

```bash
# Start backend server
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# In another terminal, start frontend dev server
cd src/main/frontend
npm run dev
```
