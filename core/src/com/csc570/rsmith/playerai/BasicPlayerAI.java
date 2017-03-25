package com.csc570.rsmith.playerai;

import com.csc570.rsmith.mechanics.board.Destination;
import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.cards.TrainCardType;
import com.csc570.rsmith.mechanics.exceptions.NoPossibleMovesException;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnType;

import java.util.*;

/**
 * Created by rsmith on 3/18/17.
 */
public class BasicPlayerAI extends TTRPlayerAI {

    // TODO: Get some kind of metric collector

    private GameManager manager;
    private Route selectedRoute = null;
    private Collection<TrainCard> selectedTrainCards;
    private List<TrainCardType> cardPriorities = new LinkedList<>();
    private TrainColor selectedTrackColor = null;

    private Map<DestinationTicketCard, List<Route>> paths = new HashMap<>();

    public BasicPlayerAI(GameBoard board) {
        super(board);

        cardPriorities.add(TrainCardType.LOCOMOTIVE);
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }


    @Override
    public TTRPlayerAI getInstance(GameManager manager) {
        BasicPlayerAI ans = new BasicPlayerAI(board);
        ans.setManager(manager);
        return ans;
    }

    @Override
    public int selectTrainCard(List<TrainCard> flop) {
        //System.out.println();
        //System.out.println("Player: " + player.getColor() + " GET TRAIN CARD");

        for (TrainCardType type : cardPriorities) {
            TrainCard card = new TrainCard(type);

            if (flop.contains(card)) {

                //System.out.println("    Chose " + card.getColor().toString());
                return flop.indexOf(card);
            }
        }

        //System.out.println("    Chose draw from deck");
        return -1;
    }

    @Override
    public Route selectRoute(GameBoard board) {
        return selectedRoute;
    }

    @Override
    public Collection<TrainCard> playTrainCards() {
        //System.out.println();
        //System.out.println("Player: " + player.getColor() + " BUILD");
        //System.out.println("    Route: " + selectedRoute.toString());

        for (List<Route> path : paths.values()) {
            path.remove(selectedRoute);
        }

        Collection<TrainCard> ans = selectedTrainCards;
        selectedRoute = null;
        selectedTrainCards = null;
        selectedTrackColor = null;

        cardPriorities = new LinkedList<>();

        return ans;
    }

    @Override
    public TrainColor selectTrackColor() {
        return selectedTrackColor;
    }

    @Override
    public Collection<DestinationTicketCard> chooseToKeep(
            Collection<DestinationTicketCard> ticketCards, int toKeep) {
        List<Pair> orderToKeep = new ArrayList<>();
        List<DestinationTicketCard> originalList = new ArrayList<>();

        int ticketCardOrder = 0;
        for (DestinationTicketCard ticketCard : ticketCards) {
            originalList.add(ticketCard);
            List<Route> path = uniformPathSearch(ticketCard.getStart(),
                    ticketCard.getEnd());
            int pathLength = 0;
            for (Route route : path) {
                pathLength += route.getLength();
            }

            boolean shouldAdd = true;
            for (int ndx = 0; ndx < orderToKeep.size(); ++ndx) {
                Pair pair = orderToKeep.get(ndx);
                if (pair.pathLength > pathLength && shouldAdd) {
                    orderToKeep.add(ndx, new Pair(ticketCardOrder, pathLength));
                    shouldAdd = false;
                }
            }
            if (shouldAdd) {
                orderToKeep.add(new Pair(ticketCardOrder, pathLength));
            }

            ++ticketCardOrder;
        }

        List<DestinationTicketCard> ans = new ArrayList<>();
        int totalPathLength = 0;

        for (int ndx = 0; (totalPathLength <= player.getTrainsLeft() || ans.size() < toKeep) &&
                ndx < ticketCards.size(); ++ndx) {
            ans.add(originalList.get((orderToKeep.get(ndx).orderNum)));
            totalPathLength += orderToKeep.get(ndx).pathLength;
        }

        if (totalPathLength > player.getTrainsLeft() && ans.size() > toKeep) {
            ans.remove(ans.size() - 1);
        }

        //System.out.println();
        //System.out.println("Player: " + player.getColor() + " GET TICKET CARDS");

        return ans;
    }

    private static class Pair {
        int orderNum;
        int pathLength;

        public Pair(int orderNum, int pathLength) {
            this.orderNum = orderNum;
            this.pathLength = pathLength;
        }
    }

    @Override
    public TurnType selectTurnType() {
        update();

        if (selectedTrainCards != null) {
            return TurnType.BUILD;
        }

        if (manager.isLastTurn()) {
            if (buildOnAllPossibleRoutes()) {
                return TurnType.BUILD;
            }
        }

        boolean drawTicketCards = false;
        if (manager.getTicketCardManager().getDeckSize() > 2) {
            drawTicketCards = true;
            for (DestinationTicketCard ticketCard : player.getTicketCards()) {
                if (!ticketCard.isCompleted()) {
                    drawTicketCards = false;
                }
            }
        }

        if (drawTicketCards) {
            return TurnType.GET_DEST_CARDS;
        }

        if (manager.getTrainCardManager().getDeckSize() > 1) {
            return TurnType.GET_TRAIN_CARDS;
        }

        if (buildOnAllPossibleRoutes()) {
            return TurnType.BUILD;
        }

        throw new NoPossibleMovesException();
    }

    private boolean buildOnAllPossibleRoutes() {
        List<Route> allRoutes = new ArrayList<>();
        for (Route route : board.getRoutes()) {
            if (!route.hasAccess(player) && route.numTracks() > 0) {
                allRoutes.add(route);
            }
        }

        allRoutes.sort(new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o2.getLength() - o1.getLength();
            }
        });

        for (Route route : allRoutes) {
            if (hasNecessaryCards(route)) {
                return true;
            }
        }

        return false;
    }

    private void update() {

        // Only add not completed ticket cards
        List<DestinationTicketCard> ticketCards = new ArrayList<>();
        for (DestinationTicketCard ticketCard : player.getTicketCards()) {
            if (!ticketCard.isCompleted()) {
                ticketCards.add(ticketCard);
            }
        }

        // sort by points (ascending)
        ticketCards.sort(new Comparator<DestinationTicketCard>() {
            @Override
            public int compare(DestinationTicketCard o1, DestinationTicketCard o2) {
                return o1.getDistance() - o2.getDistance();
            }
        });

        for (DestinationTicketCard ticketCard : ticketCards) {
            // Create new path if none there
            if (paths.get(ticketCard) == null || paths.get(ticketCard).size() == 0) {
                if (paths.get(ticketCard) != null) {
                    manager.getStats().switchPath();
                }

                paths.put(ticketCard, uniformPathSearch(ticketCard.getStart(), ticketCard.getEnd()));
            }

            for (Route route : paths.get(ticketCard)) {

                // If path no longer accessible, create new path
                if (route.numTracks() == 0) {
                    paths.put(ticketCard, uniformPathSearch(ticketCard.getStart(), ticketCard.getEnd()));
                }

            }

            List<Route> path = new ArrayList<>(paths.get(ticketCard));
            path.sort(new Comparator<Route>() {
                @Override
                public int compare(Route o1, Route o2) {
                    return o1.getLength() - o2.getLength();
                }
            });

            for (Route route : path) {
                if (!hasNecessaryCards(route)) {
                    setDesiredCards(route);
                }
            }

        }

    }

    // Desired cards

    private void setDesiredCards(Route route) {
        for (TrainColor color : route.getColorsAllowed()) {
            if (!color.equals(TrainColor.ANY)) {
                if (cardPriorities.contains(TrainCardType.getTypeOf(color))) {
                    cardPriorities.remove(TrainCardType.getTypeOf(color));
                }

                cardPriorities.add(0, TrainCardType.getTypeOf(color));
            }


        }
    }

    // Route accessibility

    private boolean hasNecessaryCards(Route route) {
        int numLocomotives = player.numCards(TrainCardType.LOCOMOTIVE);

        for (TrainColor color : route.getColorsAllowed()) {
            if (color.equals(TrainColor.ANY)) {
                List<TrainCardType> cardTypes = new ArrayList<>(cardPriorities);
                for (TrainCardType type : TrainCardType.values()) {
                    if (!cardTypes.contains(type)) {
                        cardTypes.add(type);
                    }
                }

                for (TrainCardType type : cardTypes) {
                    if (player.numCards(type) +
                            numLocomotives > route.getLength()) {

                        selectedRoute = route;
                        selectedTrackColor = TrainColor.ANY;
                        selectedTrainCards = new ArrayList<>();
                        for (int ndx = 0; ndx < player.numCards(type) &&
                                ndx < route.getLength(); ++ndx) {
                            selectedTrainCards.add(new TrainCard(type));
                        }
                        for (int ndx = selectedTrainCards.size(); ndx < route.getLength(); ++ndx) {
                            selectedTrainCards.add(new TrainCard(TrainCardType.LOCOMOTIVE));
                        }

                        return true;
                    }
                }
            }
            else if (player.numCards(TrainCardType.getTypeOf(color)) +
                    numLocomotives > route.getLength()) {

                selectedRoute = route;
                selectedTrackColor = color;
                selectedTrainCards = new ArrayList<>();
                for (int ndx = 0; ndx < player.numCards(TrainCardType.getTypeOf(color)) &&
                        ndx < route.getLength(); ++ndx) {
                    selectedTrainCards.add(new TrainCard(TrainCardType.getTypeOf(color)));
                }
                for (int ndx = selectedTrainCards.size(); ndx < route.getLength(); ++ndx) {
                    selectedTrainCards.add(new TrainCard(TrainCardType.LOCOMOTIVE));
                }

                return true;
            }
        }

        return false;
    }



    // Djikstra (uniform lowest cost) Search for destination ticket card routes

    private List<Route> uniformPathSearch(Destination start, Destination end) {
        List<Route> path = new ArrayList<>();
        Set<Destination> visited = new HashSet<>();
        PriorityQueue<DestNode> queue = new PriorityQueue<>(new Comparator<DestNode>() {
            @Override
            public int compare(DestNode o1, DestNode o2) {
                return o1.getPathLength() - o2.getPathLength();
            }
        });

        DestNode currNode = new DestNode(start, new ArrayList<Route>());
        queue.add(currNode);

        while (queue.size() > 0 && !currNode.dest.equals(end)) {
            currNode = queue.poll();

            if (currNode.dest.equals(end)) {
                break;
            }

            for (Route route : currNode.dest.getRoutes()) {
                if (!visited.contains(route.getOther(currNode.dest)) &&
                        route.numTracks() > 0) {

                    List<Route> currPath = currNode.getCurrPath();
                    if (!route.hasAccess(player)) {
                        currPath.add(route);
                    }

                    DestNode node = new DestNode(route.getOther(currNode.dest), currPath);

                    visited.add(node.dest);
                    queue.add(node);
                }
            }

        }

        if (currNode.dest.equals(end) && currNode.getPathLength() <= player.getTrainsLeft()) {
            return currNode.getCurrPath();
        }

        return new ArrayList<>();
    }

    private static class DestNode {

        List<Route> currPath;
        Destination dest;
        int pathLength;

        public DestNode(Destination currDest, List<Route> currPath) {
            dest = currDest;
            this.currPath = new ArrayList<>(currPath);

            pathLength = 0;
            for (Route route : currPath) {
                pathLength += route.getLength();
            }
        }

        public int getPathLength() {
            return pathLength;
        }

        public List<Route> getCurrPath() {
            return new ArrayList<>(currPath);
        }

    }
}
