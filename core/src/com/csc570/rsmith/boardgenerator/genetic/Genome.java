package com.csc570.rsmith.boardgenerator.genetic;

import com.csc570.rsmith.mechanics.exceptions.OutOfSeedException;

import java.util.Random;

/**
 * Created by rsmith on 3/21/17.
 */
public class Genome {

    public static final int SEED_SIZE = 1200;
    public static final int SEED_MAX = Integer.MAX_VALUE;

    private int[] seed;
    private Random rand = new Random();
    private int genomeNdx = 0;

    public Genome() {
        seed = new int[SEED_SIZE];

        for (int ndx = 0; ndx < SEED_SIZE; ++ndx) {
            seed[ndx] = rand.nextInt(SEED_MAX);
        }
    }

    public Genome(int[] seed) {
        this.seed = seed;
    }

    public int next() {
        if (genomeNdx < seed.length) {
            return seed[genomeNdx++];
        }

        throw new OutOfSeedException();
    }

    public double nextPerc() {
        int next = next();
        return next * 1.0 / SEED_MAX;
    }

    public int[] getSeed() {
        return seed;
    }

    public int[] getUsedSeed() {
        int[] ans = new int[genomeNdx];
        System.arraycopy(seed, 0, ans, 0, genomeNdx);

        return ans;
    }


}
