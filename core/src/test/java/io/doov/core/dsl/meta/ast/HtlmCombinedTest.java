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
import static io.doov.core.dsl.DOOV.matchAll;
import static io.doov.core.dsl.DOOV.when;
import static io.doov.core.dsl.meta.ast.HtmlAnyMatchTest.documentOf;
import static io.doov.core.dsl.meta.ast.HtmlAnyMatchTest.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.*;

import io.doov.core.dsl.field.types.*;
import io.doov.core.dsl.lang.Result;
import io.doov.core.dsl.lang.StepCondition;
import io.doov.core.dsl.runtime.GenericModel;

public class HtlmCombinedTest {
    private StepCondition A, B, C;
    private Result result;
    private Document doc;
    private GenericModel model;
    private IntegerFieldInfo zeroField;
    private IterableFieldInfo<String, List<String>> iterableField;
    private EnumFieldInfo<?> enumField;

    @BeforeEach
    void beforeEach() {
        this.model = new GenericModel();
        this.zeroField = model.intField(0, "zero");
        this.iterableField = model.iterableField(asList("a", "b"), "list");
        this.enumField = model.enumField(null, "enum");
    }

    @Test
    void reduce_matchAll() {
        A = alwaysTrue("A");
        B = alwaysFalse("B");
        C = alwaysFalse("C");
        result = when(matchAll(A, B, C)).validate().withShortCircuit(false).execute();
        doc = documentOf(result);
        
        assertFalse(result.value());
    }

    @Test
    void reduce_and() {
        A = alwaysTrue("A");
        B = alwaysFalse("B");
        result = when(A.and(B)).validate().withShortCircuit(false).execute();
        doc = documentOf(result);

        assertFalse(result.value());
        assertThat(doc.select("div.percentage-value")).hasSize(2);
        assertThat(doc.select("div.percentage-value").get(0).text()).isEqualTo("100 %");
        assertThat(doc.select("div.percentage-value").get(1).text()).isEqualTo("0 %");
    }

    @Test
    void reduce_zeroInt() {
        result = when(zeroField.notEq(0)).validate().withShortCircuit(false).executeOn(model);
        doc = documentOf(result);
        
        assertFalse(result.value());
    }

    @Test
    void reduce_list() {
        result = when(iterableField.contains("c")).validate().withShortCircuit(false).executeOn(model);
        doc = documentOf(result);
        
        assertFalse(result.value());
    }

    @Test
    void reduce_null() {
        result = when(enumField.isNull()).validate().withShortCircuit(false).executeOn(model);
        doc = documentOf(result);
        
        assertTrue(result.value());
    }

    @AfterEach
    void afterEach() {
        System.out.println(format(result, doc));
    }
}
