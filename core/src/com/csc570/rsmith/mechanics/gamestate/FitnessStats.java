package com.csc570.rsmith.mechanics.gamestate;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.csc570.rsmith.boardgenerator.genetic.GEBoardGenerator;
import com.csc570.rsmith.mechanics.board.Destination;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.player.TTRPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsmith on 3/22/17.
 */
public class FitnessStats {

    static final double CLUTTER_RADIUS = 300;

    static final double AVG_SCORE = 70.8636662556512;
    static final double SCORE_VAR = 366.09495273325143;
    static final double NUM_DESTS = 36;
    static final double AVG_ROUTES = 4.333333333333497;
    static final double NUM_TURNS = 195.42046855733662;
    static final double TRAIN_TURNS = 0.6427626071067598;
    static final double BUILD_TURNS = 0.3442839854498003;
    static final double DEST_TURNS = 0.012953407443431843;
    static final double PATH_CHANGES = 11.241368680641191;
    static final double CLUTTER = 12.444444444441876;
    static final double ROUTE_INTS = 3.641025641026155;
    static final double CITIES_COVERED = 0.01185931379978608;

    static Map<Double, Double> weightsMap = new HashMap<>();
    static {
        weightsMap.put(AVG_SCORE, 1.5);
        weightsMap.put(SCORE_VAR, 0.8);
        weightsMap.put(NUM_DESTS, 0.6);
        weightsMap.put(AVG_ROUTES, 0.8);
        weightsMap.put(NUM_TURNS, 1.0);
        weightsMap.put(TRAIN_TURNS, 0.8);
        weightsMap.put(BUILD_TURNS, 0.0);
        weightsMap.put(DEST_TURNS, 0.0);
        weightsMap.put(PATH_CHANGES, 0.4);
        weightsMap.put(CLUTTER, 1.5);
        weightsMap.put(ROUTE_INTS, 0.6);
        weightsMap.put(CITIES_COVERED, 0.7);
    }

    private double avgScore = 0;
    private double scoreVariance = 0;

    private int numDests = 0;
    private double routesPerDest = 0;

    private int numTurns = 0;
    private double avgTrainCardsTurns = 0;
    private double avgBuildTurns = 0;
    private double avgDestCardsTurns = 0;

    private double avgPathChanges = 0;

    private double clutter = 0;
    private double routeOverRoutes = 0;
    private double citiesCovered = 0;

    public void takeTurn(TurnType type) {
        switch (type) {
            case BUILD:
                ++avgBuildTurns;
                break;
            case GET_TRAIN_CARDS:
                ++avgTrainCardsTurns;
                break;
            case GET_DEST_CARDS:
                ++avgDestCardsTurns;
                break;
        }
    }

    public void switchPath() {
        ++avgPathChanges;
    }

    public void endGameStats(GameManager manager) {
        List<TTRPlayer> players = manager.getPlayers();

        numTurns = manager.getTurnNdx();
        for (TTRPlayer player : players) {
            avgScore += player.getPoints();
        }
        avgScore /= players.size();

        for (TTRPlayer player : players) {
            scoreVariance += Math.pow(player.getPoints() - avgScore, 2);
        }
        scoreVariance /= players.size();

        avgDestCardsTurns /= numTurns;
        avgTrainCardsTurns /= numTurns;
        avgBuildTurns /= numTurns;

        for (Destination dest : manager.getBoard().getDestinations()) {
            routesPerDest += dest.getRoutes().size();
            ++numDests;
        }
        routesPerDest /= numDests;

        avgPathChanges /= players.size();

        List<Destination> destinations = manager.getBoard().getDestinations();
        List<Route> routes = manager.getBoard().getRoutes();

        for (Destination dest : destinations) {
            int numClose = 0;

            for (Destination other : destinations) {
                if (!dest.equals(other)) {
                    double dist = Math.sqrt(Math.pow(dest.getxCord() - other.getxCord(), 2) +
                            Math.pow(dest.getyCord() - other.getyCord(), 2));
                    if (dist < CLUTTER_RADIUS) {
                        numClose++;
                    }
                }
            }

            clutter += numClose;
        }

        clutter /= 1.0 * destinations.size();

        for (Route route : routes) {
            for (Route other : routes) {
                if (!route.equals(other)) {
                    routeOverRoutes += routeIntersection(route, other);
                }
            }

            for (Destination dest : destinations) {
                if (!route.getStart().equals(dest) && !route.getEnd().equals(dest)) {
                    citiesCovered += cityCoverage(route, dest);
                }
            }
        }

        citiesCovered /= routes.size();
        routeOverRoutes /= routes.size();
    }

    // Clutter stuff
    private static double routeIntersection(Route r1, Route r2) {
        Vector2 p1 = new Vector2(r1.getStart().getxCord(), r1.getStart().getyCord());
        Vector2 p2 = new Vector2(r1.getEnd().getxCord(), r1.getEnd().getyCord());
        Vector2 p3 = new Vector2(r2.getStart().getxCord(), r2.getStart().getyCord());
        Vector2 p4 = new Vector2(r2.getEnd().getxCord(), r2.getEnd().getyCord());
        Vector2 intersection = new Vector2();

        if (!Intersector.intersectSegments(p1, p2, p3, p4, intersection)) {
            return 0;
        }

        double ans;
        if (p2.cpy().sub(p1).angle() <= 45) {

            ans = (intersection.x - p1.x) / (p2.x - p1.x);
        }
        else {
            ans = (intersection.y - p1.y) / (p2.y - p1.y);
        }

        if (ans > 1 || ans < 0) {
            System.out.println("Uh Oh in intersections");
        }

        return ans;
    }

    private static double cityCoverage(Route route, Destination dest) {
        Vector2 A = new Vector2(route.getStart().getxCord(), route.getStart().getyCord());
        Vector2 B = new Vector2(route.getEnd().getxCord(), route.getEnd().getyCord());
        Vector2 C = new Vector2(dest.getxCord(), dest.getyCord());

        float distance = Intersector.distanceSegmentPoint(A, B, C);

        if (distance >= GEBoardGenerator.DEST_RADIUS) {
            return 0;
        }

        return (GEBoardGenerator.DEST_RADIUS - distance) / GEBoardGenerator.DEST_RADIUS;
    }

    public double getFitness() {
        double distance = 0;

        distance += weightsMap.get(AVG_SCORE) * percError(avgScore, AVG_SCORE);
        distance += weightsMap.get(SCORE_VAR) * percError(scoreVariance, SCORE_VAR);

        distance += weightsMap.get(NUM_DESTS) * percError(numDests, NUM_DESTS);
        distance += weightsMap.get(AVG_ROUTES) * percError(routesPerDest, AVG_ROUTES);

        distance += weightsMap.get(NUM_TURNS) * percError(numTurns, NUM_TURNS);
        distance += weightsMap.get(TRAIN_TURNS) * percError(avgTrainCardsTurns, TRAIN_TURNS);
        distance += weightsMap.get(BUILD_TURNS) * percError(avgBuildTurns, BUILD_TURNS);
        distance += weightsMap.get(DEST_TURNS) * percError(avgDestCardsTurns, DEST_TURNS);

        distance += weightsMap.get(PATH_CHANGES) * percError(avgPathChanges, PATH_CHANGES);

        distance += weightsMap.get(CLUTTER) * percError(clutter, CLUTTER);
        distance += weightsMap.get(ROUTE_INTS) * (routeOverRoutes - ROUTE_INTS) / ROUTE_INTS;
        distance += weightsMap.get(CITIES_COVERED) * (citiesCovered - CITIES_COVERED) / CITIES_COVERED;

        double totalWeight = 0;
        for (Double weight : weightsMap.values()) {
            totalWeight += weight;
        }

        distance /= totalWeight;

        return 1 / distance;
    }

    public static double percError(double value, double original) {
        return Math.abs(original - value) / original;
    }



    public double getAvgScore() {
        return avgScore;
    }

    public double getScoreVariance() {
        return scoreVariance;
    }

    public int getNumDests() {
        return numDests;
    }

    public double getRoutesPerDest() {
        return routesPerDest;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public double getAvgTrainCardsTurns() {
        return avgTrainCardsTurns;
    }

    public double getAvgBuildTurns() {
        return avgBuildTurns;
    }

    public double getAvgDestCardsTurns() {
        return avgDestCardsTurns;
    }

    public double getAvgPathChanges() {
        return avgPathChanges;
    }

    public double getClutter() {
        return clutter;
    }

    public double getRouteOverRoutes() {
        return routeOverRoutes;
    }

    public double getCitiesCovered() {
        return citiesCovered;
    }
}
