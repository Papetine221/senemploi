package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LocalisationCriteriaTest {

    @Test
    void newLocalisationCriteriaHasAllFiltersNullTest() {
        var localisationCriteria = new LocalisationCriteria();
        assertThat(localisationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void localisationCriteriaFluentMethodsCreatesFiltersTest() {
        var localisationCriteria = new LocalisationCriteria();

        setAllFilters(localisationCriteria);

        assertThat(localisationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void localisationCriteriaCopyCreatesNullFilterTest() {
        var localisationCriteria = new LocalisationCriteria();
        var copy = localisationCriteria.copy();

        assertThat(localisationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(localisationCriteria)
        );
    }

    @Test
    void localisationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var localisationCriteria = new LocalisationCriteria();
        setAllFilters(localisationCriteria);

        var copy = localisationCriteria.copy();

        assertThat(localisationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(localisationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var localisationCriteria = new LocalisationCriteria();

        assertThat(localisationCriteria).hasToString("LocalisationCriteria{}");
    }

    private static void setAllFilters(LocalisationCriteria localisationCriteria) {
        localisationCriteria.id();
        localisationCriteria.region();
        localisationCriteria.departement();
        localisationCriteria.ville();
        localisationCriteria.distinct();
    }

    private static Condition<LocalisationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRegion()) &&
                condition.apply(criteria.getDepartement()) &&
                condition.apply(criteria.getVille()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LocalisationCriteria> copyFiltersAre(
        LocalisationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRegion(), copy.getRegion()) &&
                condition.apply(criteria.getDepartement(), copy.getDepartement()) &&
                condition.apply(criteria.getVille(), copy.getVille()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
