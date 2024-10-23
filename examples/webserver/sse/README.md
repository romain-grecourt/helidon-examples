# Helidon SE SSE Example

This is a very simple example showing how to use the Helidon SSE API to send events from the
server to a client. It defines a couple of SSE "hello world" endpoints that send either
text or JSON messages. It also uses the SSE extensions to the Helidon WebClient API to test 
these endpoints.

## Build, run and test

Build and start the server:
```shell
mvn package
java -jar target/helidon-examples-webserver-sse.jar
```

See `SseServiceTest` for additional information on the tests that are executed during
the build process.