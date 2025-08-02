package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LocalisationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Localisation getLocalisationSample1() {
        return new Localisation().id(1L).region("region1").departement("departement1").ville("ville1");
    }

    public static Localisation getLocalisationSample2() {
        return new Localisation().id(2L).region("region2").departement("departement2").ville("ville2");
    }

    public static Localisation getLocalisationRandomSampleGenerator() {
        return new Localisation()
            .id(longCount.incrementAndGet())
            .region(UUID.randomUUID().toString())
            .departement(UUID.randomUUID().toString())
            .ville(UUID.randomUUID().toString());
    }
}
