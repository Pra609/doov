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
package io.doov.core.dsl.impl;

import static io.doov.core.dsl.impl.DefaultFunction.valueModel;
import static io.doov.core.dsl.meta.predicate.UnaryPredicateMetadata.notNullMetadata;
import static io.doov.core.dsl.meta.predicate.UnaryPredicateMetadata.nullMetadata;

import java.util.function.*;

import io.doov.core.FieldModel;
import io.doov.core.Try;
import io.doov.core.dsl.field.BaseFieldInfo;
import io.doov.core.dsl.lang.Context;
import io.doov.core.dsl.meta.predicate.PredicateMetadata;

public class LeafStepCondition<N> extends DefaultStepCondition {

    private LeafStepCondition(PredicateMetadata metadata, BiFunction<FieldModel, Context, Try<N>> value,
            Function<N, Boolean> predicate) {
        super(metadata, (model, context) ->
                value.apply(model, context)
                        .map(predicate)
                        .onErrorReturn(false)
                        .value());
    }

    private LeafStepCondition(PredicateMetadata metadata, BiFunction<FieldModel, Context, Try<N>> left,
            BiFunction<FieldModel, Context, Try<N>> right, BiFunction<N, N, Boolean> predicate) {
        super(metadata, (model, context) -> left.apply(model, context)
                .flatMap(l -> right.apply(model, context).map(r -> predicate.apply(l, r)))
                .onErrorReturn(false)
                .value());
    }

    /**
     * Returns a step condition checking if the node value is null.
     *
     * @param <N> the type of the node value
     * @param condition the node value to check
     * @return the step condition
     */
    public static <N> LeafStepCondition<Try<N>> isNull(DefaultCondition<N> condition) {
        return new LeafStepCondition<>(nullMetadata(condition.metadata()),
                (model, context) -> Try.success(condition.value(model, context)), Try::isNull);
    }

    /**
     * Returns a step condition checking if the node value is not null.
     *
     * @param <N> the type of the node value
     * @param condition the node value to check
     * @return the step condition
     */
    public static <N> LeafStepCondition<Try<N>> isNotNull(DefaultCondition<N> condition) {
        return new LeafStepCondition<>(notNullMetadata(condition.metadata()),
                (model, context) -> Try.success(condition.value(model, context)), Try::isNotNull);
    }

    public static <N> LeafStepCondition<N> stepCondition(PredicateMetadata metadata,
            BiFunction<FieldModel, Context, Try<N>> left, Function<N, Boolean> predicate) {
        return new LeafStepCondition<>(metadata, left, predicate);
    }

    public static <N> LeafStepCondition<N> stepCondition(PredicateMetadata metadata,
            BiFunction<FieldModel, Context, Try<N>> left, BaseFieldInfo<N> right,
            BiFunction<N, N, Boolean> predicate) {
        return new LeafStepCondition<>(metadata, left, (model, context) -> valueModel(model, right), predicate);
    }

    public static <N> LeafStepCondition<N> stepCondition(PredicateMetadata metadata,
            BiFunction<FieldModel, Context, Try<N>> left, N right, BiFunction<N, N, Boolean> predicate) {
        return new LeafStepCondition<>(metadata, left, (model, context) -> Try.success(right), predicate);
    }

    public static <N> LeafStepCondition<N> stepCondition(PredicateMetadata metadata,
            BiFunction<FieldModel, Context, Try<N>> left, Supplier<N> right,
            BiFunction<N, N, Boolean> predicate) {
        return new LeafStepCondition<>(metadata, left, (model, context) -> Try.supplier(right), predicate);
    }

    public static <N> LeafStepCondition<N> stepCondition(PredicateMetadata metadata,
        BiFunction<FieldModel, Context, Try<N>> left, BiFunction<FieldModel, Context, Try<N>> right,
        BiFunction<N, N, Boolean> predicate) {
        return new LeafStepCondition<>(metadata, left, right, predicate);
    }

}
