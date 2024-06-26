/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jetty;

import java.util.Map;

import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpFilterCamelHeadersTest extends BaseJettyTest {

    @BindToRegistry("foo")
    private MyFooBean bean = new MyFooBean();

    @Test
    public void testFilterCamelHeaders() {
        Exchange out = template.send("http://localhost:{{port}}/test/filter", new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody("Claus");
                exchange.getIn().setHeader("bar", 123);
            }
        });

        assertNotNull(out);
        assertEquals("Hi Claus", out.getMessage().getBody(String.class));

        // there should be no internal Camel headers
        // except for the response code
        Map<String, Object> headers = out.getMessage().getHeaders();
        for (String key : headers.keySet()) {
            boolean valid
                    = key.equalsIgnoreCase(Exchange.HTTP_RESPONSE_CODE) || key.equalsIgnoreCase(Exchange.HTTP_RESPONSE_TEXT);
            if (!valid) {
                assertFalse(key.toLowerCase().startsWith("camel"), "Should not contain any Camel internal headers");
            } else {
                assertEquals(200, headers.get(Exchange.HTTP_RESPONSE_CODE));
            }
        }
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("jetty:http://localhost:{{port}}/test/filter").bean("foo");
            }
        };
    }

    public static class MyFooBean {

        public String hello(String name) {
            return "Hi " + name;
        }
    }
}
