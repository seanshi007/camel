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

import java.util.Comparator;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Registry;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the file sorter ref option
 */
public class FileSorterRefTest extends ContextTestSupport {

    @Override
    protected Registry createCamelRegistry() throws Exception {
        Registry jndi = super.createCamelRegistry();
        jndi.bind("mySorter", new MyFileSorter<>());
        return jndi;
    }

    @Test
    public void testSortFiles() throws Exception {
        template.sendBodyAndHeader(fileUri(), "Hello Paris", Exchange.FILE_NAME, "paris.txt");

        template.sendBodyAndHeader(fileUri(), "Hello London", Exchange.FILE_NAME, "london.txt");

        template.sendBodyAndHeader(fileUri(), "Hello Copenhagen", Exchange.FILE_NAME, "copenhagen.txt");

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from(fileUri("?initialDelay=0&delay=10&sorter=#mySorter")).convertBodyTo(String.class).to("mock:result");
            }
        });

        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello Copenhagen", "Hello London", "Hello Paris");
        assertMockEndpointsSatisfied();
    }

    // START SNIPPET: e1
    public static class MyFileSorter<T> implements Comparator<GenericFile<T>> {
        @Override
        public int compare(GenericFile<T> o1, GenericFile<T> o2) {
            return o1.getFileName().compareToIgnoreCase(o2.getFileName());
        }
    }
    // END SNIPPET: e1

}
