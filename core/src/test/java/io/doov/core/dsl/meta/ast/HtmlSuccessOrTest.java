/*
 * Copyright 2018 Courtanet
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
package io.doov.core.dsl.meta.ast;

import static io.doov.core.dsl.DOOV.alwaysFalse;
import static io.doov.core.dsl.DOOV.alwaysTrue;
import static io.doov.core.dsl.DOOV.when;
import static io.doov.core.dsl.meta.ast.HtmlAnyMatchTest.documentOf;
import static io.doov.core.dsl.meta.ast.HtmlAnyMatchTest.format;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.doov.core.dsl.lang.Result;
import io.doov.core.dsl.lang.StepCondition;

public class HtmlSuccessOrTest {
    private StepCondition A, B, C;
    private Result result;
    private Document doc;

    @Test
    void or_false_false_success() {
        A = alwaysFalse("A");
        B = alwaysFalse("B");
        result = when(A.or(B)).validate().withShortCircuit(false).execute();
        doc = documentOf(result);
        
        assertFalse(result.value());
    }

    @Test
    void or_true_false_success() {
        A = alwaysTrue("A");
        B = alwaysFalse("B");
        result = when(A.or(B)).validate().withShortCircuit(false).execute();
        doc = documentOf(result);
        
        assertTrue(result.value());
    }

    @Test
    void or_true_false_complex_success() {
        A = alwaysTrue("A");
        B = alwaysFalse("B");
        C = alwaysTrue("C");
        result = when(A.or(B.or(C))).validate().withShortCircuit(false).execute();
        doc = documentOf(result);
        
        assertTrue(result.value());
    }

    @Test
    void or_false_true_success() {
        A = alwaysFalse("A");
        B = alwaysTrue("B");
        result = when(A.or(B)).validate().withShortCircuit(false).execute();
        doc = documentOf(result);
        
        assertTrue(result.value());
    }

    @Test
    void or_false_true_complex_success() {
        A = alwaysFalse("A");
        B = alwaysTrue("B");
        C = alwaysTrue("C");
        result = when(A.or(B.and(C))).validate().withShortCircuit(false).execute();
        doc = documentOf(result);
        
        assertTrue(result.value());
    }

    @Test
    void or_true_true_success() {
        A = alwaysTrue("A");
        B = alwaysTrue("B");
        result = when(A.or(B)).validate().withShortCircuit(false).execute();
        doc = documentOf(result);
        
        assertTrue(result.value());
    }
    

    @AfterEach
    void afterEach() {
        System.out.println(format(result, doc));
    }
}
