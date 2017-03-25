package com.csc570.rsmith.mechanics.player;

import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.gamestate.TurnType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rsmith on 2/28/17.
 */
public class TTRHumanPlayer extends TTRPlayer {

    public TTRHumanPlayer(TTRPlayerColor color) {
        super(color);
    }

    // TTRPlayer Methods

    // These methods go together

    private int ndxToSelect = -1;
    public void setNdxToSelect(int ndx) {
        ndxToSelect = ndx;
    }

    @Override
    public int selectTrainCard(List<TrainCard> flop) {
        int ans = ndxToSelect;
        ndxToSelect = -1;
        return ans;
    }

    // These methods go together

    private Route selectedRoute = null;
    public void setSelectedRoute(Route route) {
        selectedRoute = route;
    }

    @Override
    public Route selectRoute(GameBoard board) {
        Route ans = selectedRoute;
        selectedRoute = null;
        return ans;
    }

    private Collection<TrainCard> cardsChosen = null;
    public void setCardsChosen(Collection<TrainCard> cards) {
        cardsChosen = cards;
    }
    @Override
    public Collection<TrainCard> chooseTrainCards(GameBoard board, Route route, TrainColor trainColor) {
        Collection<TrainCard> ans = cardsChosen;
        cardsChosen = null;
        return ans;
    }

    private TrainColor colorPicked = null;
    public void setColorPicked(TrainColor color) {
        colorPicked = color;
    }
    @Override
    public TrainColor selectTrackColor(GameBoard board, Route route) {
        TrainColor ans = colorPicked;
        colorPicked = null;
        return ans;
    }

    // These methods go together

    private Collection<DestinationTicketCard> ticketCardsToKeep = null;
    public void setTicketCardsToKeep(Collection<DestinationTicketCard> ticketCards) {
        ticketCardsToKeep = ticketCards;
    }

    @Override
    public Collection<DestinationTicketCard> chooseToKeep(
            Collection<DestinationTicketCard> ticketCards, int toKeep) {
        Collection<DestinationTicketCard> ans = ticketCardsToKeep;
        ticketCardsToKeep = null;
        return ans;
    }

    // These methods go together

    @Override
    public TurnType selectTurnType() {
        return null;
    }
}
