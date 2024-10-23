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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.helidon.http.sse.SseEvent;
import io.helidon.webclient.http1.Http1Client;
import io.helidon.webclient.http1.Http1ClientResponse;
import io.helidon.webclient.sse.SseSource;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.testing.junit5.ServerTest;
import io.helidon.webserver.testing.junit5.SetUpRoute;

import jakarta.json.JsonObject;
import org.junit.jupiter.api.Test;

import static io.helidon.http.HeaderValues.ACCEPT_EVENT_STREAM;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ServerTest
class SseServiceTest {

    private final Http1Client client;

    SseServiceTest(Http1Client client) {
        this.client = client;
    }

    @SetUpRoute
    static void routing(HttpRules rules) {
        rules.register(new SseService());
    }

    @Test
    void testSseText() throws InterruptedException {
        try (Http1ClientResponse r = client.get("/sse_text").header(ACCEPT_EVENT_STREAM).request()) {
            CountDownLatch latch = new CountDownLatch(1);
            r.source(SseSource.TYPE, new SseSource() {
                private int state = 0;

                @Override
                public void onEvent(SseEvent event) {
                    switch (state) {
                    case 0 -> {
                        assertThat(event.comment().isPresent(), is(true));
                        assertThat(event.comment().get(), is("first line"));
                        assertThat(event.name().isPresent(), is(true));
                        assertThat(event.name().get(), is("first"));
                        assertThat(event.data(), is("hello"));
                    }
                    case 1 -> {
                        assertThat(event.name().isPresent(), is(true));
                        assertThat(event.name().get(), is("second"));
                        assertThat(event.data(), is("world"));
                    }
                    }
                    state++;
                }

                @Override
                public void onClose() {
                    latch.countDown();
                }
            });
            assertThat(latch.await(5, TimeUnit.SECONDS), is(true));
        }
    }

    @Test
    void testSseJson() throws InterruptedException {
        try (Http1ClientResponse r = client.get("/sse_json").header(ACCEPT_EVENT_STREAM).request()) {
            CountDownLatch latch = new CountDownLatch(1);
            r.source(SseSource.TYPE, event -> {
                JsonObject json = event.data(JsonObject.class);
                assertThat(json, is(notNullValue()));
                assertThat(json.getString("hello"), is("world"));
                latch.countDown();
            });
            assertThat(latch.await(5, TimeUnit.SECONDS), is(true));
        }
    }
}
