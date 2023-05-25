# Spring ChatGPT Sample

This sample shows how to build a ChatGPT like application in Spring and run on Azure Spring Apps.
It enables ChatGPT to use your private data to answer the questions.

## Getting Started

### Prerequisites

- JDK 17
- Maven
- Azure OpenAI Service

### Quickstart

1. Create the model deployments for `text-embedding-ada-002` and `gpt-35-turbo` in your Azure OpenAI service.
1. Run `git clone https://github.com/Azure-Samples/spring-chatgpt-sample.git`
1. Run `cd spring-chatgpt-sample`.
1. Run `cp env.sh.sample env.sh` and substitute the placeholders.
1. Build with `mvn clean package`.

## Demo

A demo app is included to show how to use the project.

To run the demo, follow these steps:

1. `source env.sh`
1. Load your documents into the local vector store:
   ```shell
   java -jar spring-chatgpt-sample-cli/target/spring-chatgpt-sample-cli-0.0.1-SNAPSHOT.jar --from=/<path>/<to>/<your>/<documents> --to=doc_store.json
   ```
1. Launch the web app
   ```shell
   java -jar spring-chatgpt-sample-webapi/target/spring-chatgpt-sample-webapi-0.0.1-SNAPSHOT.jar
   ```
1. Open `http://localhost:8080` in your browser.

## Resources

- Link to supporting information
- Link to similar sample
- ...
