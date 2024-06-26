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
package org.apache.camel.component.jsonvalidator;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonValidationExceptionTest {
    private int errorId = 0;

    @Test
    void testErrorsInfoInMessage() {
        Set<ValidationMessage> errors = new LinkedHashSet<>();
        errors.add(createError("name: is missing but it is required"));
        errors.add(createError("id: string found, integer expected"));
        final JsonValidationException jsonValidationException = new JsonValidationException(null, null, errors);

        assertEquals(
                "JSON validation error with 2 errors:\nname: is missing but it is required\nid: string found, integer expected",
                jsonValidationException.getMessage());
    }

    @Test
    void testErrorsEmpty() {
        assertEquals(0, new JsonValidationException(null, null, new Exception()).getNumberOfErrors());
    }

    private ValidationMessage createError(String msg) {
        return new ValidationMessage.Builder()
                .messageKey(String.valueOf(errorId++))
                .format(new MessageFormat(msg)).build();
    }
}
