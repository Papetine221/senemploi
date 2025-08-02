package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OffreEmploi;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class OffreEmploiRepositoryWithBagRelationshipsImpl implements OffreEmploiRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String OFFREEMPLOIS_PARAMETER = "offreEmplois";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<OffreEmploi> fetchBagRelationships(Optional<OffreEmploi> offreEmploi) {
        return offreEmploi.map(this::fetchCompetences);
    }

    @Override
    public Page<OffreEmploi> fetchBagRelationships(Page<OffreEmploi> offreEmplois) {
        return new PageImpl<>(
            fetchBagRelationships(offreEmplois.getContent()),
            offreEmplois.getPageable(),
            offreEmplois.getTotalElements()
        );
    }

    @Override
    public List<OffreEmploi> fetchBagRelationships(List<OffreEmploi> offreEmplois) {
        return Optional.of(offreEmplois).map(this::fetchCompetences).orElse(Collections.emptyList());
    }

    OffreEmploi fetchCompetences(OffreEmploi result) {
        return entityManager
            .createQuery(
                "select offreEmploi from OffreEmploi offreEmploi left join fetch offreEmploi.competences where offreEmploi.id = :id",
                OffreEmploi.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<OffreEmploi> fetchCompetences(List<OffreEmploi> offreEmplois) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, offreEmplois.size()).forEach(index -> order.put(offreEmplois.get(index).getId(), index));
        List<OffreEmploi> result = entityManager
            .createQuery(
                "select offreEmploi from OffreEmploi offreEmploi left join fetch offreEmploi.competences where offreEmploi in :offreEmplois",
                OffreEmploi.class
            )
            .setParameter(OFFREEMPLOIS_PARAMETER, offreEmplois)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
