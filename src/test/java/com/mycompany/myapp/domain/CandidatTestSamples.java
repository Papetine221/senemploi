package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CandidatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Candidat getCandidatSample1() {
        return new Candidat().id(1L).telephone("telephone1").adresse("adresse1");
    }

    public static Candidat getCandidatSample2() {
        return new Candidat().id(2L).telephone("telephone2").adresse("adresse2");
    }

    public static Candidat getCandidatRandomSampleGenerator() {
        return new Candidat().id(longCount.incrementAndGet()).telephone(UUID.randomUUID().toString()).adresse(UUID.randomUUID().toString());
    }
}
