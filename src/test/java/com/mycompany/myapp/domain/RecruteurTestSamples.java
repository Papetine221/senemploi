package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RecruteurTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Recruteur getRecruteurSample1() {
        return new Recruteur().id(1L).nomEntreprise("nomEntreprise1").secteur("secteur1");
    }

    public static Recruteur getRecruteurSample2() {
        return new Recruteur().id(2L).nomEntreprise("nomEntreprise2").secteur("secteur2");
    }

    public static Recruteur getRecruteurRandomSampleGenerator() {
        return new Recruteur()
            .id(longCount.incrementAndGet())
            .nomEntreprise(UUID.randomUUID().toString())
            .secteur(UUID.randomUUID().toString());
    }
}
