package com.csc570.rsmith.boardgenerator.genetic;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.csc570.rsmith.boardgenerator.BoardGenerator;
import com.csc570.rsmith.mechanics.board.Destination;
import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCardManager;

import java.util.*;

/**
 * Created by rsmith on 3/21/17.
 */
public class GEBoardGenerator implements BoardGenerator {

    public static final float DEST_RADIUS = 45f;
    public static final double DEST_BUFFER = 70;

    public static final int TRACK_LENGTH_MIN = 1;
    public static final int TRACK_LENGTH_MAX = 6;

    public static final int MIN_NUM_ROUTES = 2;
    public static final int MAX_NUM_ROUTES = 7;

    public static final double GRAY_TRACK_PERC = .33;

    private static final double EPSILON = .000001;

    static Map<Integer, Interval> trackLengthDistanceMap;
    static {
        trackLengthDistanceMap = new HashMap<>();
        trackLengthDistanceMap.put(1, new Interval(DEST_BUFFER, DEST_BUFFER + 25));
        trackLengthDistanceMap.put(2, new Interval(DEST_BUFFER + 25, DEST_BUFFER + 50));
        trackLengthDistanceMap.put(3, new Interval(DEST_BUFFER + 50, DEST_BUFFER + 75));
        trackLengthDistanceMap.put(4, new Interval(DEST_BUFFER + 75, DEST_BUFFER + 100));
        trackLengthDistanceMap.put(5, new Interval(DEST_BUFFER + 100, DEST_BUFFER + 120));
        trackLengthDistanceMap.put(6, new Interval(DEST_BUFFER + 110, DEST_BUFFER + 150));
    }


    private Genome genome;

    private Map<Integer, Route> allRoutes = new HashMap<>();
    private List<Destination> allDestList = new ArrayList<>();

    public GEBoardGenerator(Genome genome) {
        this.genome = genome;
    }

    @Override
    public GameBoard createBoard() {

        long startTime = System.currentTimeMillis();

        List<Pair<Integer>> ticketCardSeed = new ArrayList<>();
        for (int ndx = 0; ndx < DestinationTicketCardManager.DEFAULT_NUM_TICKET_CARDS; ++ndx) {
            int x = genome.next();
            int y = genome.next();
            ticketCardSeed.add(new Pair<>(x, y));
        }

        Queue<Destination> destQueue = new ArrayDeque<>();
        Set<Destination> visisted = new HashSet<>();

        Destination firstDest = new Destination(nextDestName());
        float firstX = (float) (genome.nextPerc() *
                (Destination.TOTAL_WIDTH - 2 * DEST_RADIUS) + DEST_RADIUS);
        float firstY = (float) (genome.nextPerc() *
                (Destination.TOTAL_HEIGHT - 2 * DEST_RADIUS) + DEST_RADIUS);
        firstDest.setCords(firstX, firstY);

        destQueue.add(firstDest);
        visisted.add(firstDest);
        allDestList.add(firstDest);
        Destination currDest = firstDest;

        while (destQueue.size() > 0) {
            currDest = destQueue.poll();

            int numRoutes = genome.next() % (MAX_NUM_ROUTES - MIN_NUM_ROUTES) + MIN_NUM_ROUTES;

            for (int ndx = currDest.getRoutes().size(); ndx < numRoutes; ++ndx) {
                Destination newDest = createRoute(currDest);
                if (newDest != null && !visisted.contains(newDest)) {
                    destQueue.add(newDest);
                    visisted.add(newDest);
                }
            }
        }

        List<DestinationTicketCard> ticketCards = new ArrayList<>();
        for (Pair<Integer> nextPair : ticketCardSeed) {
            Destination start = allDestList.get(nextPair.x % allDestList.size());
            List<Destination> tmp = new ArrayList<>(allDestList);
            tmp.remove(start);
            Destination end = tmp.get(nextPair.y % tmp.size());
            ticketCards.add(new DestinationTicketCard(start, end));
        }

        //System.out.println("Generation took: " +
        //        (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
        //System.out.println("Genome Length: " + genome.getUsedSeed().length);

        return new GameBoard(allRoutes.values(), ticketCards);
    }

    /**
     * Creates a route, using the genome instance variable and GE methodology
     * @param start The destination the route is starting from
     * @return a new destination to be added to the "search path" if one is
     *         created, otherwise null
     */
    private Destination createRoute(Destination start) {
        double x = start.getxCord();
        double y = start.getyCord();

        int trackLength = genome.next() % TRACK_LENGTH_MAX + TRACK_LENGTH_MIN;

        List<Interval> angleInts = getPossibleAngleIntervals(x, y, trackLength);
        double angleIntLength = 0;
        for (Interval interval : angleInts) {
            angleIntLength += interval.getLength();
        }
        double angleTotal = genome.nextPerc() * angleIntLength;
        double angle = -1;
        for (Interval interval : angleInts) {
            if (interval.getLength() + EPSILON < angleTotal) {
                angleTotal -= interval.getLength();
            }
            else {
                angle = interval.start + angleTotal;
            }
        }

        if (angle == -1) {
            System.out.println("Angle initialization wrong");
        }

        double trackDistance =
                genome.nextPerc() * trackLengthDistanceMap.get(trackLength).getLength() +
                trackLengthDistanceMap.get(trackLength).start;

        double newX = x + trackDistance * Math.cos(angle);
        double newY = y + trackDistance * Math.sin(angle);

        // DEBUGGING
        if (newX < 0 || newY < 0 || newX > Destination.TOTAL_WIDTH || newY > Destination.TOTAL_HEIGHT) {
            System.out.println("This is wrong, have (" + newX + ", " + newY + ")");
        }

        Destination newDest = null;
        Destination endDest = null;
        for (Destination dest : allDestList) {
            if (!dest.equals(start) && withinBuffer(start, dest, newX, newY)) {
                endDest = dest;
                if (endDest.getNeighbors().contains(start)) {
                    return null;
                }
                trackDistance = Math.sqrt(Math.pow(endDest.getxCord() - start.getxCord(), 2) +
                        Math.pow(endDest.getyCord() - start.getyCord(), 2));

                if (!trackLengthDistanceMap.get(trackLength).inInterval(trackDistance)) {
                    boolean distanceFound = false;
                    for (Integer length : trackLengthDistanceMap.keySet()) {
                        if (trackLengthDistanceMap.get(length).inInterval(trackDistance)
                                && !distanceFound) {
                            trackLength = length;
                            distanceFound = true;

                        }
                    }
                    if (!distanceFound) {
                        trackLength = TRACK_LENGTH_MAX;
                    }
                }

                break;
            }
        }
        if (endDest == null) {
            newDest = new Destination(nextDestName());
            newDest.setCords((float) newX, (float) newY);
            allDestList.add(newDest);
            endDest = newDest;
        }

        int numTrainColors = genome.next() % 2 + 1;
        List<TrainColor> colorsAllowed = new ArrayList<>();

        List<TrainColor> toChooseFrom = new ArrayList<>();
        int numGray = 1;
        for (TrainColor color : TrainColor.values()) {
            toChooseFrom.add(color);
        }
        while (numGray * 1.0 / toChooseFrom.size() < GRAY_TRACK_PERC) {
            ++numGray;
            toChooseFrom.add(TrainColor.ANY);
        }

        for (int ndx = 0; ndx < numTrainColors; ++ndx) {
            TrainColor color = toChooseFrom.get(genome.next() % toChooseFrom.size());
            colorsAllowed.add(color);
        }

        Route route = new Route(start, endDest, trackLength, colorsAllowed);
        start.addRoute(route);
        endDest.addRoute(route);
        allRoutes.put(route.hashCode(), route);

        return newDest;
    }

    private boolean withinBuffer(Destination oldDest, Destination newDest, double x, double y) {
        /*return Math.sqrt(Math.pow(newDest.getxCord() - x, 2) +
                Math.pow(newDest.getyCord() - y, 2))
                < DEST_BUFFER;*/
        Vector2 A = new Vector2(oldDest.getxCord(), oldDest.getyCord());
        Vector2 B = new Vector2((float) x, (float) y);
        Vector2 C = new Vector2(newDest.getxCord(), newDest.getyCord());

        return Intersector.distanceSegmentPoint(A, B, C) <= DEST_BUFFER;

    }

    // In radians
    private List<Interval> getPossibleAngleIntervals(double x, double y, int trackLength) {
        if (x < 0 || y < 0 || x > Destination.TOTAL_WIDTH || y > Destination.TOTAL_HEIGHT) {
            System.out.println("This is wrong, have (" + x + ", " + y + ")");
        }

        double radius = trackLengthDistanceMap.get(trackLength).end;

        List<Interval> ans = new ArrayList<>();
        double endAngle = 2 * Math.PI;

        if (x >= Destination.TOTAL_WIDTH - radius) {
            double intStart = angleCalc(radius,
                    Destination.TOTAL_WIDTH - x);

            endAngle = 2 * Math.PI - intStart;
            ans.add(new Interval(intStart, intStart));

        }
        else {
            ans.add(new Interval(0, 0));
        }

        if (y >= Destination.TOTAL_HEIGHT - radius) {
            double intInc = angleCalc(radius,
                    Destination.TOTAL_HEIGHT - y);
            double intStart = Math.PI / 2 - intInc;
            double intEnd = Math.PI / 2 + intInc;

            ans.get(ans.size() - 1).end = intStart;

            if (ans.get(ans.size() - 1).getLength() < 0) {
                ans.set(ans.size() - 1, new Interval(intEnd, intEnd));
            }
            else {
                ans.add(new Interval(intEnd, intEnd));
            }
        }

        if (x <= radius) {
            double intInc = angleCalc(radius, x);
            double intStart = Math.PI - intInc;
            double intEnd = Math.PI + intInc;

            ans.get(ans.size() - 1).end = intStart;

            if (ans.get(ans.size() - 1).getLength() < 0) {
                ans.set(ans.size() - 1, new Interval(intEnd, intEnd));
            }
            else {
                ans.add(new Interval(intEnd, intEnd));
            }
        }

        if (y <= radius) {
            double intInc = angleCalc(radius, y);
            double intStart = 3 * Math.PI / 2 - intInc;
            double intEnd = 3 * Math.PI / 2 + intInc;

            ans.get(ans.size() - 1).end = intStart;

            if (ans.get(ans.size() - 1).getLength() < 0) {
                ans.set(ans.size() - 1, new Interval(intEnd, intEnd));
            }
            else {
                ans.add(new Interval(intEnd, intEnd));
            }
        }

        ans.get(ans.size() - 1).end = endAngle;

        return ans;
    }

    // In radians
    private static double angleCalc(double rad, double coord) {
        double tanStuff = Math.atan(Math.sqrt(rad * rad - coord * coord) / coord);
        double ans = tanStuff + DEST_BUFFER / rad;
        //return tanStuff + DEST_BUFFER / rad;
        return tanStuff;
    }

    static class Interval {
        double start;
        double end;

        public Interval(double start, double end) {
            this.start = start;
            this.end = end;
        }

        public double getLength() {
            return end - start;
        }

        public boolean inInterval(double num) {
            return (num <= end && num >= start) ||
                    (num >= end && num <= start);
        }
    }

    static class Pair<T> {
        T x;
        T y;

        public Pair(T x, T y) {
            this.x = x;
            this.y = y;
        }
    }

    private int currDestNameNum = 0;
    private String nextDestName() {
        return "City: " + currDestNameNum++;
    }
}
