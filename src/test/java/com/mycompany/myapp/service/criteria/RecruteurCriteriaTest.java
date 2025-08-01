package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RecruteurCriteriaTest {

    @Test
    void newRecruteurCriteriaHasAllFiltersNullTest() {
        var recruteurCriteria = new RecruteurCriteria();
        assertThat(recruteurCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void recruteurCriteriaFluentMethodsCreatesFiltersTest() {
        var recruteurCriteria = new RecruteurCriteria();

        setAllFilters(recruteurCriteria);

        assertThat(recruteurCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void recruteurCriteriaCopyCreatesNullFilterTest() {
        var recruteurCriteria = new RecruteurCriteria();
        var copy = recruteurCriteria.copy();

        assertThat(recruteurCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(recruteurCriteria)
        );
    }

    @Test
    void recruteurCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var recruteurCriteria = new RecruteurCriteria();
        setAllFilters(recruteurCriteria);

        var copy = recruteurCriteria.copy();

        assertThat(recruteurCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(recruteurCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var recruteurCriteria = new RecruteurCriteria();

        assertThat(recruteurCriteria).hasToString("RecruteurCriteria{}");
    }

    private static void setAllFilters(RecruteurCriteria recruteurCriteria) {
        recruteurCriteria.id();
        recruteurCriteria.nomEntreprise();
        recruteurCriteria.secteur();
        recruteurCriteria.userId();
        recruteurCriteria.distinct();
    }

    private static Condition<RecruteurCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNomEntreprise()) &&
                condition.apply(criteria.getSecteur()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RecruteurCriteria> copyFiltersAre(RecruteurCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNomEntreprise(), copy.getNomEntreprise()) &&
                condition.apply(criteria.getSecteur(), copy.getSecteur()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
