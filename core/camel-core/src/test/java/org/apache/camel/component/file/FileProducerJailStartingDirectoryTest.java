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
package org.apache.camel.component.file;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileProducerJailStartingDirectoryTest extends ContextTestSupport {

    @Test
    public void testWriteOutsideStartingDirectory() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);

        Exception e = assertThrows(Exception.class,
                () -> template.sendBodyAndHeader("direct:start", "Hello World", Exchange.FILE_NAME, "hello.txt"),
                "Should have thrown exception");

        IllegalArgumentException iae = assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
        assertTrue(iae.getMessage().contains("as the filename is jailed to the starting directory"));

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testWriteInsideStartingDirectory() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.sendBodyAndHeader("direct:start", "Bye World", Exchange.FILE_NAME, "outbox/bye.txt");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start").setHeader(Exchange.FILE_NAME, simple("../${file:name}"))
                        .to(fileUri("outbox"))
                        .to("mock:result");
            }
        };
    }
}
