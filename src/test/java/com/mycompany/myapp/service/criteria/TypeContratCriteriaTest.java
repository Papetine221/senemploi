package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TypeContratCriteriaTest {

    @Test
    void newTypeContratCriteriaHasAllFiltersNullTest() {
        var typeContratCriteria = new TypeContratCriteria();
        assertThat(typeContratCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void typeContratCriteriaFluentMethodsCreatesFiltersTest() {
        var typeContratCriteria = new TypeContratCriteria();

        setAllFilters(typeContratCriteria);

        assertThat(typeContratCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void typeContratCriteriaCopyCreatesNullFilterTest() {
        var typeContratCriteria = new TypeContratCriteria();
        var copy = typeContratCriteria.copy();

        assertThat(typeContratCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(typeContratCriteria)
        );
    }

    @Test
    void typeContratCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var typeContratCriteria = new TypeContratCriteria();
        setAllFilters(typeContratCriteria);

        var copy = typeContratCriteria.copy();

        assertThat(typeContratCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(typeContratCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var typeContratCriteria = new TypeContratCriteria();

        assertThat(typeContratCriteria).hasToString("TypeContratCriteria{}");
    }

    private static void setAllFilters(TypeContratCriteria typeContratCriteria) {
        typeContratCriteria.id();
        typeContratCriteria.nom();
        typeContratCriteria.distinct();
    }

    private static Condition<TypeContratCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria -> condition.apply(criteria.getId()) && condition.apply(criteria.getNom()) && condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TypeContratCriteria> copyFiltersAre(TypeContratCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
