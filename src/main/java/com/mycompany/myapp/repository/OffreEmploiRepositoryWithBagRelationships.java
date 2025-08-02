package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OffreEmploi;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface OffreEmploiRepositoryWithBagRelationships {
    Optional<OffreEmploi> fetchBagRelationships(Optional<OffreEmploi> offreEmploi);

    List<OffreEmploi> fetchBagRelationships(List<OffreEmploi> offreEmplois);

    Page<OffreEmploi> fetchBagRelationships(Page<OffreEmploi> offreEmplois);
}
