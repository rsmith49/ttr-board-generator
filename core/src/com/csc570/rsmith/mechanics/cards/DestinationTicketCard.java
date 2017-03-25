package com.csc570.rsmith.mechanics.cards;

import com.csc570.rsmith.mechanics.board.Destination;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.exceptions.IncompatibleVarsException;

import java.util.*;

/**
 * Created by rsmith on 2/22/17.
 */
public class DestinationTicketCard {

    private Destination start;
    private Destination end;

    private int distance;
    private boolean completed = false;

    /**
     *  Both Destinations must have routes initialized, and game
     *  board must be established when calling this method
     * @param start Destination 1
     * @param end Destination 2
     */
    public DestinationTicketCard(Destination start, Destination end) {
        this.start = start;
        this.end = end;

        setDistance();
    }

    public DestinationTicketCard(Destination start, Destination end,
                                 int pathLength) {
        this.start = start;
        this.end = end;
        this.distance = pathLength;
    }

    /**
     * Uses shortest route uninformed search to find the shortest path
     * between the Destinations
     */
    private void setDistance() {
        PriorityQueue<DestNode> queue = new PriorityQueue<>();
        Set<Destination> visited = new HashSet<>();

        queue.add(new DestNode(start, 0));
        DestNode currNode = new DestNode(null, 0);

        while (queue.size() > 0 && !end.equals(currNode.dest)) {
            currNode = queue.poll();
            visited.add(currNode.dest);

            Collection<Route> routes = currNode.dest.getRoutes();
            for (Route route : routes) {
                if (!visited.contains(route.getOther(currNode.dest))) {
                    queue.add(new DestNode(route.getOther(currNode.dest),
                            currNode.pathLength + route.getLength()));
                }
            }
        }

        if (!end.equals(currNode.dest)) {
            throw new IncompatibleVarsException();
        }

        this.distance = currNode.pathLength;
    }

    private static class DestNode implements Comparable<DestNode> {

        Destination dest;
        int pathLength;

        DestNode(Destination dest, int pathLength) {
            this.dest = dest;
            this.pathLength = pathLength;
        }

        @Override
        public int compareTo(DestNode d2) {
            return pathLength - d2.pathLength;
        }
    }

    public Destination getStart() {
        return start;
    }

    public Destination getEnd() {
        return end;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        completed = true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof DestinationTicketCard)) {
            return false;
        }
        DestinationTicketCard otherCard = (DestinationTicketCard) other;

        return (otherCard.start.equals(start) && otherCard.end.equals(end)) ||
                (otherCard.start.equals(end) && otherCard.end.equals(start));
    }

    @Override
    public int hashCode() {
        List<String> destNames = new ArrayList<>();
        destNames.add(start.getLocationName());
        destNames.add(end.getLocationName());
        Collections.sort(destNames);

        String combinedName = destNames.get(0) + destNames.get(1);

        return combinedName.hashCode();
    }

    @Override
    public String toString() {
        return start.getLocationName() + " to " + end.getLocationName() + ": " + distance;
    }
}
