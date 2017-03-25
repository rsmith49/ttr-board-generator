package com.csc570.rsmith.mechanics.board;

import com.csc570.rsmith.boardgenerator.genetic.GeneticAlgorithmDriver;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by rsmith on 2/22/17.
 */
public class GameBoard {

    private List<Destination> destinations;
    private List<Route> routes;
    private List<DestinationTicketCard> ticketCards;

    public GameBoard() {
        destinations = new ArrayList<>();
        routes = new ArrayList<>();
        ticketCards = new ArrayList<>();
    }

    // GA stuff
    private int[] seed;

    public int[] getSeed() {
        return seed;
    }

    public void setSeed(int[] seed) {
        this.seed = seed;
    }

    private double fitness;

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Creates a GameBoard from a set of routes, used primarily when importing
     * a GameBoard from a json file
     * @param routes The set of routes to be imported
     */
    public GameBoard(Collection<Route> routes, Collection<DestinationTicketCard> ticketCards) {
        this.destinations = new ArrayList<>();
        this.routes = new ArrayList<>(routes);
        for (Route route : routes) {
            if (!destinations.contains(route.getStart())) {
                destinations.add(route.getStart());
            }
            if (!destinations.contains(route.getEnd())) {
                destinations.add(route.getEnd());
            }
            route.getStart().addRoute(route);
            route.getEnd().addRoute(route);
        }

        this.ticketCards = new ArrayList<>(ticketCards);
    }

    public GameBoard(Collection<Destination> destinations,
                     Collection<Route> routes,
                     Collection<DestinationTicketCard> cards) {
        this.destinations = new ArrayList<>(destinations);
        this.routes = new ArrayList<>(routes);
        this.ticketCards = new ArrayList<>(cards);
    }

    public List<Destination> getDestinations() {
        return new ArrayList<>(destinations);
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }

    public List<Route> getAvailableRoutes() {
        List<Route> ans = new ArrayList<>();

        for (Route route : routes) {
            if (route.numTracks() > 0) {
                ans.add(route);
            }
        }

        return ans;
    }

    public List<DestinationTicketCard> getTicketCards() {
        return new ArrayList<>(ticketCards);
    }
}
