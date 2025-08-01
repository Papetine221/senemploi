package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CandidatureCriteriaTest {

    @Test
    void newCandidatureCriteriaHasAllFiltersNullTest() {
        var candidatureCriteria = new CandidatureCriteria();
        assertThat(candidatureCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void candidatureCriteriaFluentMethodsCreatesFiltersTest() {
        var candidatureCriteria = new CandidatureCriteria();

        setAllFilters(candidatureCriteria);

        assertThat(candidatureCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void candidatureCriteriaCopyCreatesNullFilterTest() {
        var candidatureCriteria = new CandidatureCriteria();
        var copy = candidatureCriteria.copy();

        assertThat(candidatureCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(candidatureCriteria)
        );
    }

    @Test
    void candidatureCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var candidatureCriteria = new CandidatureCriteria();
        setAllFilters(candidatureCriteria);

        var copy = candidatureCriteria.copy();

        assertThat(candidatureCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(candidatureCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var candidatureCriteria = new CandidatureCriteria();

        assertThat(candidatureCriteria).hasToString("CandidatureCriteria{}");
    }

    private static void setAllFilters(CandidatureCriteria candidatureCriteria) {
        candidatureCriteria.id();
        candidatureCriteria.datePostulation();
        candidatureCriteria.statut();
        candidatureCriteria.candidatId();
        candidatureCriteria.offreId();
        candidatureCriteria.distinct();
    }

    private static Condition<CandidatureCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDatePostulation()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getCandidatId()) &&
                condition.apply(criteria.getOffreId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CandidatureCriteria> copyFiltersAre(CandidatureCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDatePostulation(), copy.getDatePostulation()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getCandidatId(), copy.getCandidatId()) &&
                condition.apply(criteria.getOffreId(), copy.getOffreId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
