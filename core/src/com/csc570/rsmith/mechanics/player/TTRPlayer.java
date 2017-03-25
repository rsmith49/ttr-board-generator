package com.csc570.rsmith.mechanics.player;

import com.csc570.rsmith.mechanics.board.Destination;
import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.cards.TrainCardType;
import com.csc570.rsmith.mechanics.exceptions.IncompatibleVarsException;
import com.csc570.rsmith.mechanics.gamestate.TurnType;

import java.util.*;

/**
 * Created by rsmith on 2/25/17.
 */
public abstract class TTRPlayer {

    private static final Map<Integer, Integer> TRAIN_POINTS_MAP = new HashMap<>();
    static {
        TRAIN_POINTS_MAP.put(1, 1);
        TRAIN_POINTS_MAP.put(2, 2);
        TRAIN_POINTS_MAP.put(3, 4);
        TRAIN_POINTS_MAP.put(4, 7);
        TRAIN_POINTS_MAP.put(5, 10);
        TRAIN_POINTS_MAP.put(6, 15);
    }

    private static final int NUM_TRAINS = 45;

    private TTRPlayerColor color;
    Map<TrainCardType, Integer> cardFreqs = new HashMap<>();
    List<DestinationTicketCard> ticketCards = new ArrayList<>();

    private int points = 0;
    private int trainsLeft = NUM_TRAINS;

    public TTRPlayer(TTRPlayerColor color) {
        this.color = color;

        for (TrainCardType trainCardType : TrainCardType.values()) {
            cardFreqs.put(trainCardType, 0);
        }
    }

    public TTRPlayerColor getColor() {
        return color;
    }

    public Map<TrainCardType, Integer> getCardFreqs() {
        return cardFreqs;
    }

    public List<DestinationTicketCard> getTicketCards() {
        return ticketCards;
    }

    public int getPoints() {
        return points;
    }

    public int getTrainsLeft() {
        return trainsLeft;
    }

    // DRAW TRAIN CARDS

    /**
     * Pick up or draw a train card
     * @param trainCard The train card picked up
     */
    public void addTrainCard(TrainCard trainCard) {
        cardFreqs.put(trainCard.getType(), cardFreqs.get(trainCard.getType()) + 1);
    }

    /**
     * Pick up or draw multiple train cards
     * @param trainCards cards picked up
     */
    public void addTrainCards(Collection<TrainCard> trainCards) {
        for (TrainCard trainCard : trainCards) {
            addTrainCard(trainCard);
        }
    }

    /**
     * Removes a train card from the players hand -- for use when selecting
     * train cards to play from the gui
     * @param card Card to be removed
     */
    public void removeTrainCard(TrainCard card) {
        cardFreqs.put(card.getType(), cardFreqs.get(card.getType()) - 1);
    }

    /**
     * Returns the indexMessage of the desired card in the flop. If a non-ANY card is
     * selected, it is to be called twice in a turn.
     * @param flop The list of TrainCards in the flop
     * @return The indexMessage of the selected card, -1 for the deck
     */
    public abstract int selectTrainCard(List<TrainCard> flop);

    // BUILD TRAIN

    /**
     * Selects the route on which to build a track
     * @return the Route object on which to build
     */
    public abstract Route selectRoute(GameBoard board);

    /**
     * Select the train color for the route that was filled this turn.
     * @return The TrainColor of the track to fill
     * @param board The game board
     * @param route The route selected
     */
    public abstract TrainColor selectTrackColor(GameBoard board, Route route);

    /**
     * Plays the train cards required to build a track on the selected route
     * @return The cards played to build on the selected route
     * @param board the game board
     * @param route the route selected
     * @param trainColor the train color chosen
     */
    public Collection<TrainCard> playTrainCards(GameBoard board,
                                                         Route route,
                                                         TrainColor trainColor) {
        Collection<TrainCard> cardsChosen = chooseTrainCards(board, route, trainColor);

        route.fillTrack(cardsChosen, trainColor, this.color);

        for (TrainCard card : cardsChosen) {
            cardFreqs.put(card.getType(), cardFreqs.get(card.getType()) - 1);
        }
        trainsLeft -= cardsChosen.size();

        this.points += TRAIN_POINTS_MAP.get(cardsChosen.size());

        for (DestinationTicketCard ticketCard : this.ticketCards) {
            if (!ticketCard.isCompleted() && isCompleted(ticketCard)) {
                this.points += 2 * ticketCard.getDistance();
                ticketCard.complete();
            }
        }

        return cardsChosen;
    }

    /**
     * Select the train cards that you desire to play, should be implemented
     * @param board the game board
     * @param route the route selected
     * @param trainColor the color of track selected to play
     * @return the cards chosen to play
     */
    public abstract Collection<TrainCard> chooseTrainCards(GameBoard board,
                                                           Route route,
                                                           TrainColor trainColor);

    // DRAW TICKET CARDS

    /**
     * Get ticket cards, which you are then required to keep some of. This
     * method requires a decision to implement, and therefore is abstract so
     * that either a human player or an AI can implement it.
     * @param ticketCards The cards drawn
     * @param toKeep The number of cards required to keep: can be greater than
     *               or equal to this number
     * @return The cards not kept by the player
     */
    public Collection<DestinationTicketCard> addTicketCards(
            Collection<DestinationTicketCard> ticketCards, int toKeep) {
        Collection<DestinationTicketCard> cardsKept =
                chooseToKeep(ticketCards, toKeep);
        Collection<DestinationTicketCard> ans = new ArrayList<>();

        for (DestinationTicketCard ticketCard : ticketCards) {
            if (cardsKept.contains(ticketCard)) {
                this.ticketCards.add(ticketCard);

                if (isCompleted(ticketCard)) {
                    ticketCard.complete();
                    this.points += ticketCard.getDistance();
                }
                else {
                    this.points -= ticketCard.getDistance();
                }
            }
            else {
                ans.add(ticketCard);
            }
        }

        return ans;
    }

    public abstract Collection<DestinationTicketCard> chooseToKeep(
            Collection<DestinationTicketCard> ticketCards, int toKeep);

    /**
     * The first method to be called upon entering a turn. It determines the
     * type of turn the player would like to take.
     * @return The TurnType for the turn selected by the player.
     */
    public abstract TurnType selectTurnType();

    /**
     * Checks to see if the player has any cards left
     * @return Whether or not the player has any cards
     */
    public boolean hasCards() {
        for (TrainCardType cardType : cardFreqs.keySet()) {
            if (cardFreqs.get(cardType) > 0) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean equals(Object other) {
        return (other instanceof TTRPlayer) &&
                ((TTRPlayer) other).color.equals(color);
    }

    @Override
    public int hashCode() {
        return color.hashCode();
    }

    // Graph Search Methods

    private boolean isCompleted(DestinationTicketCard ticketCard) {
        Destination start = ticketCard.getStart();
        Destination end = ticketCard.getEnd();

        Queue<Destination> queue = new ArrayDeque<>();
        Set<Destination> visited = new HashSet<>();

        queue.add(start);
        Destination currDest = null;

        while (queue.size() > 0 && !end.equals(currDest)) {
            currDest = queue.poll();
            visited.add(currDest);

            Collection<Route> routes = currDest.getRoutes();
            for (Route route : routes) {
                if (route.hasAccess(this) && !visited.contains(route.getOther(currDest))) {
                    queue.add(route.getOther(currDest));
                }
            }
        }

        return end.equals(currDest);
    }

    // TODO: Depth First Search, resulting search tree takes longest 2 paths from base and combines them
    public int getLongestRouteLength(GameBoard board) {
        int longestLength = 0;
        Set<Route> unusedRoutes = new HashSet<>();
        Set<Route> traveledRoutes = new HashSet<>();

        for (Route route : board.getRoutes()) {
            if (route.hasAccess(this)) {
                unusedRoutes.add(route);
            }
        }

        while (unusedRoutes.size() > 0) {
            Iterator<Route> routeIt = unusedRoutes.iterator();
            Route currRoute = routeIt.next();

            Deque<Route> pathList = new ArrayDeque<>();

            pathList.add(currRoute);
            traveledRoutes.add(currRoute);
            unusedRoutes.remove(currRoute);

            while (pathList.size() > 0) {
                List<Route> connections = new ArrayList<>();

                for (Route route : currRoute.getStart().getRoutes()) {
                    if (route.hasAccess(this) &&
                            !traveledRoutes.contains(route) &&
                            !route.equals(currRoute)) {
                        pathList.push(route);
                        traveledRoutes.add(route);
                        unusedRoutes.remove(currRoute);
                    }
                }

                for (Route route : currRoute.getEnd().getRoutes()) {
                    if (route.hasAccess(this) &&
                            !traveledRoutes.contains(route) &&
                            !route.equals(currRoute)) {
                        pathList.push(route);
                        traveledRoutes.add(route);
                        unusedRoutes.remove(currRoute);
                    }
                }

                currRoute = pathList.pop();
            }


        }




        return 0;
    }

    private static class RoutePath {

        Route startRoute;
        Set<Route> routes;

    }
}
