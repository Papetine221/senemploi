package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CandidatCriteriaTest {

    @Test
    void newCandidatCriteriaHasAllFiltersNullTest() {
        var candidatCriteria = new CandidatCriteria();
        assertThat(candidatCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void candidatCriteriaFluentMethodsCreatesFiltersTest() {
        var candidatCriteria = new CandidatCriteria();

        setAllFilters(candidatCriteria);

        assertThat(candidatCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void candidatCriteriaCopyCreatesNullFilterTest() {
        var candidatCriteria = new CandidatCriteria();
        var copy = candidatCriteria.copy();

        assertThat(candidatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(candidatCriteria)
        );
    }

    @Test
    void candidatCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var candidatCriteria = new CandidatCriteria();
        setAllFilters(candidatCriteria);

        var copy = candidatCriteria.copy();

        assertThat(candidatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(candidatCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var candidatCriteria = new CandidatCriteria();

        assertThat(candidatCriteria).hasToString("CandidatCriteria{}");
    }

    private static void setAllFilters(CandidatCriteria candidatCriteria) {
        candidatCriteria.id();
        candidatCriteria.telephone();
        candidatCriteria.adresse();
        candidatCriteria.userId();
        candidatCriteria.distinct();
    }

    private static Condition<CandidatCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTelephone()) &&
                condition.apply(criteria.getAdresse()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CandidatCriteria> copyFiltersAre(CandidatCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTelephone(), copy.getTelephone()) &&
                condition.apply(criteria.getAdresse(), copy.getAdresse()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
