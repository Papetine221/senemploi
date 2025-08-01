package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OffreEmploiCriteriaTest {

    @Test
    void newOffreEmploiCriteriaHasAllFiltersNullTest() {
        var offreEmploiCriteria = new OffreEmploiCriteria();
        assertThat(offreEmploiCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void offreEmploiCriteriaFluentMethodsCreatesFiltersTest() {
        var offreEmploiCriteria = new OffreEmploiCriteria();

        setAllFilters(offreEmploiCriteria);

        assertThat(offreEmploiCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void offreEmploiCriteriaCopyCreatesNullFilterTest() {
        var offreEmploiCriteria = new OffreEmploiCriteria();
        var copy = offreEmploiCriteria.copy();

        assertThat(offreEmploiCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(offreEmploiCriteria)
        );
    }

    @Test
    void offreEmploiCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var offreEmploiCriteria = new OffreEmploiCriteria();
        setAllFilters(offreEmploiCriteria);

        var copy = offreEmploiCriteria.copy();

        assertThat(offreEmploiCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(offreEmploiCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var offreEmploiCriteria = new OffreEmploiCriteria();

        assertThat(offreEmploiCriteria).hasToString("OffreEmploiCriteria{}");
    }

    private static void setAllFilters(OffreEmploiCriteria offreEmploiCriteria) {
        offreEmploiCriteria.id();
        offreEmploiCriteria.titre();
        offreEmploiCriteria.salaire();
        offreEmploiCriteria.datePublication();
        offreEmploiCriteria.dateExpiration();
        offreEmploiCriteria.recruteurId();
        offreEmploiCriteria.distinct();
    }

    private static Condition<OffreEmploiCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitre()) &&
                condition.apply(criteria.getSalaire()) &&
                condition.apply(criteria.getDatePublication()) &&
                condition.apply(criteria.getDateExpiration()) &&
                condition.apply(criteria.getRecruteurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OffreEmploiCriteria> copyFiltersAre(OffreEmploiCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitre(), copy.getTitre()) &&
                condition.apply(criteria.getSalaire(), copy.getSalaire()) &&
                condition.apply(criteria.getDatePublication(), copy.getDatePublication()) &&
                condition.apply(criteria.getDateExpiration(), copy.getDateExpiration()) &&
                condition.apply(criteria.getRecruteurId(), copy.getRecruteurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
