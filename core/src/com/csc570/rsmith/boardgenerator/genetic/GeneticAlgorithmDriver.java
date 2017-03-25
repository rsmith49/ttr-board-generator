package com.csc570.rsmith.boardgenerator.genetic;

import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.exceptions.InfiniteLoopException;
import com.csc570.rsmith.mechanics.exceptions.NoPossibleMovesException;
import com.csc570.rsmith.mechanics.gamestate.FitnessStats;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.player.TTRPlayer;

import java.util.*;

/**
 * Created by rsmith on 3/22/17.
 */
public class GeneticAlgorithmDriver {

    public static int GEN_POP = 400;
    public static final int NUM_GENS = 40;
    public static final double FITNESS_CUTOFF = 25;

    public static final double GENE_MUT_PROB = 0.05;
    public static final double NDX_MUT_PROB = .002;
    public static final double MUT_VAR_CONST = 1000;

    public static final int NUM_FITNESS_GAMES = 100;

    public static final int BEST_TO_KEEP = 5;



    private Random random;

    public GeneticAlgorithmDriver() {
        random = new Random();
    }

    public GeneticAlgorithmDriver(Random random) {
        this.random = random;
    }

    public List<GameBoard> generateBoards(int numBoards) {
        double avgBestFit = 0;
        Generation currGen = new Generation();

        for (int genNdx = 0; genNdx < NUM_GENS; ++genNdx) {
            currGen = new Generation(currGen);

            avgBestFit = 0;
            for (int ndx = 0; ndx < BEST_TO_KEEP; ++ndx) {
                avgBestFit += currGen.organisms.get(ndx).fitness;
            }
            avgBestFit /= BEST_TO_KEEP;

            System.out.println("Generation " + (genNdx + 1) +
                    " generated, Avg Best Fitness: " + avgBestFit);

            // If we have reached a certain threshold
            if (avgBestFit >= FITNESS_CUTOFF) {
                System.out.println("Generation " + (genNdx + 1) +
                        " generated, Avg Best Fitness: " + avgBestFit);
                break;
            }
        }

        /* if (avgBestFit < FITNESS_CUTOFF) {
            System.out.println("Generation " + NUM_GENS +
                    " generated, Avg Best Fitness: " + avgBestFit);
        } */

        List<GameBoard> bestBoards = new ArrayList<>();
        List<Seed> bestOrgs = new ArrayList<>(currGen.organisms);
        Collections.sort(bestOrgs);
        for (int ndx = 0; ndx < numBoards; ++ndx) {
            GameBoard board = bestOrgs.get(ndx).createBoard();
            board.setSeed(bestOrgs.get(ndx).seed);
            board.setFitness(bestOrgs.get(ndx).fitness);

            bestBoards.add(board);
        }

        return bestBoards;
    }


    public int[] mutate(int[] seed, double fitness) {
        int[] ans = new int[seed.length];

        for (int ndx = 0; ndx < seed.length; ++ndx) {
            if (random.nextDouble() <= NDX_MUT_PROB) {
                double gauss = random.nextGaussian();
                ans[ndx] = (int) (seed[ndx] + mutVariance(fitness) * gauss);
                if (ans[ndx] < 0) {
                    ans[ndx] = 0;
                }
                else if (ans[ndx] > Genome.SEED_MAX) {
                    ans[ndx] = Genome.SEED_MAX;
                }
            }
            else {
                ans[ndx] = seed[ndx];
            }
        }

        return ans;
    }

    /**
     * Seeds must have the same length
     * @param seed1
     * @param seed2
     * @return
     */
    public int[] crossover(int[] seed1, int[] seed2) {
        int[] ans = new int[seed1.length];
        int[] firstSeed, secondSeed;
        if (random.nextDouble() > 0.5) {
            firstSeed = seed1;
            secondSeed = seed2;
        }
        else {
            firstSeed = seed2;
            secondSeed = seed1;
        }

        int crossOverNdx = random.nextInt(ans.length);

        System.arraycopy(firstSeed, 0, ans, 0, crossOverNdx);
        System.arraycopy(secondSeed, crossOverNdx, ans, crossOverNdx, ans.length - crossOverNdx);

        return ans;
    }

    public static double mutVariance(double fitness) {
        return MUT_VAR_CONST / fitness;
    }

    class Seed implements Comparable<Seed> {
        int[] seed;
        double fitness;

        public Seed() {
            seed = new int[Genome.SEED_SIZE];
            for (int ndx = 0; ndx < Genome.SEED_SIZE; ++ndx) {
                seed[ndx] = random.nextInt(Genome.SEED_MAX);
            }
            init();
        }

        public Seed(int[] seed) {
            this.seed = seed;
            init();
        }

        private void init() {
            GameBoard board = createBoard();
            fitness = fitness(board);
        }

        public GameBoard createBoard() {
            Genome genome = new Genome(seed);
            GEBoardGenerator generator = new GEBoardGenerator(genome);
            return generator.createBoard();
        }

        @Override
        public int compareTo(Seed other) {
            double result = other.fitness - this.fitness;
            if (result > 0) {
                return 1;
            }
            if (result < 0) {
                return -1;
            }
            return 0;
        }
    }

    class Generation {
        List<Seed> organisms = new ArrayList<>();

        public Generation() {
            for (int ndx = 0; ndx < GEN_POP; ++ndx) {
                Seed seed = new Seed();
                organisms.add(seed);
            }
        }

        public Generation(Generation previous) {
            double totalFitness = 0;
            for (Seed seed : previous.organisms) {
                totalFitness += seed.fitness;
            }

            // Keep the N best organisms from the last generation
            if (BEST_TO_KEEP > 0) {
                List<Seed> sortedOrganisms = new ArrayList<>(previous.organisms);
                Collections.sort(sortedOrganisms);

                for (int ndx = 0; ndx < BEST_TO_KEEP; ++ndx) {
                    if (random.nextDouble() <= GENE_MUT_PROB) {
                        int[] newSeed = mutate(sortedOrganisms.get(ndx).seed,
                                sortedOrganisms.get(ndx).fitness);
                        organisms.add(new Seed(newSeed));
                    }
                    else {
                        organisms.add(sortedOrganisms.get(ndx));
                    }
                }
            }

            for (int ndx = BEST_TO_KEEP; ndx < previous.size(); ++ndx) {
                Seed parent1 = previous.selectByFitness(random.nextDouble() * totalFitness);
                Seed parent2 = previous.selectByFitness(random.nextDouble() * totalFitness);
                Seed childOrg = new Seed(crossover(parent1.seed, parent2.seed));

                if (random.nextDouble() <= GENE_MUT_PROB) {
                    int[] newSeed = mutate(childOrg.seed, childOrg.fitness);
                    organisms.add(new Seed(newSeed));
                }
                else {
                    organisms.add(childOrg);
                }
            }


        }

        public Seed selectByFitness(double num) {
            for (Seed organism : organisms) {
                num -= organism.fitness;
                if (num <= 0) {
                    return organism;
                }
            }

            System.out.println("Uh Oh, have a null");
            return null;
        }

        public int size() {
            return organisms.size();
        }

    }


    // This is all of the fitness

    public static double fitness(GameBoard board) {

        double totalFitness = 0;
        int toCount = NUM_FITNESS_GAMES;

        for (int ndx = 0; ndx < NUM_FITNESS_GAMES; ++ndx) {
            try {
                totalFitness += singleGameFitness(board);
            } catch (NoPossibleMovesException | InfiniteLoopException e) {
                totalFitness += 0;
                //--toCount;
                //System.out.println("No possible move");
            }
        }

        if (toCount == 0) {
            return 0;
        }

        totalFitness /= toCount;

        return totalFitness;
    }

    public static double singleGameFitness(GameBoard board) {
        GameManager manager = new GameManager(board);
        manager.runAutomatedGame();

        FitnessStats stats = manager.getStats();

        return stats.getFitness();
    }
}
