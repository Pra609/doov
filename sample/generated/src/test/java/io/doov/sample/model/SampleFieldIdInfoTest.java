/*
 * Copyright 2017 Courtanet
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
package io.doov.sample.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import io.doov.core.FieldInfo;
import io.doov.sample.field.SampleFieldId;
import io.doov.sample.field.SampleFieldInfo;

public class SampleFieldIdInfoTest {

    private Optional<FieldInfo> fieldInfo(SampleFieldId id) {
        return SampleFieldInfo.stream().filter(info -> info.id() == id).findFirst();
    }

    @Test
    public void should_have_field_info() {
        SoftAssertions softAssertions = new SoftAssertions();

        Arrays.stream(SampleFieldId.values()).forEach(id -> {
            softAssertions.assertThat(fieldInfo(id)).isPresent();
            softAssertions.assertThat(fieldInfo(id))
                    .isNotEmpty()
                    .hasValueSatisfying(info -> assertThat(info.type()).isNotNull());
        });

        softAssertions.assertAll();
    }

}
