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
package org.apache.camel.processor;

import java.util.Comparator;
import java.util.List;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Traceable;
import org.apache.camel.spi.IdAware;
import org.apache.camel.spi.RouteIdAware;
import org.apache.camel.support.AsyncProcessorSupport;

/**
 * A processor that sorts the expression using a comparator
 */
public class SortProcessor<T> extends AsyncProcessorSupport implements IdAware, RouteIdAware, Traceable {

    private String id;
    private String routeId;
    private final Expression expression;
    private final Comparator<? super T> comparator;

    public SortProcessor(Expression expression, Comparator<? super T> comparator) {
        this.expression = expression;
        this.comparator = comparator;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            @SuppressWarnings("unchecked")
            List<T> list = expression.evaluate(exchange, List.class);
            list.sort(comparator);

            exchange.getMessage().setBody(list);
        } catch (Exception e) {
            exchange.setException(e);
        }

        callback.done(true);
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        return "sort[" + expression + "]";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getRouteId() {
        return routeId;
    }

    @Override
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Expression getExpression() {
        return expression;
    }

    public Comparator<? super T> getComparator() {
        return comparator;
    }

}
