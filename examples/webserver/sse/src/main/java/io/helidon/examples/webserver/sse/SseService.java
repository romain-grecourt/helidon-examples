/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.examples.webserver.sse;

import io.helidon.http.sse.SseEvent;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.sse.SseSink;

import jakarta.json.Json;
import jakarta.json.JsonObject;

class SseService implements HttpService {

    @Override
    public void routing(HttpRules httpRules) {
        httpRules.get("/sse_text", this::sseText)
                .get("/sse_json", this::sseJson);
    }

    void sseText(ServerRequest req, ServerResponse res) {
        try (SseSink sseSink = res.sink(SseSink.TYPE)) {
            sseSink.emit(SseEvent.builder()
                                 .comment("first line")
                                 .name("first")
                                 .data("hello")
                                 .build())
                    .emit(SseEvent.builder()
                                  .name("second")
                                  .data("world")
                                  .build());
        }
    }

    void sseJson(ServerRequest req, ServerResponse res) {
        JsonObject json = Json.createObjectBuilder()
                .add("hello", "world")
                .build();
        try (SseSink sseSink = res.sink(SseSink.TYPE)) {
            sseSink.emit(SseEvent.create(json));
        }
    }
}
