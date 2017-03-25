package com.csc570.rsmith.mechanics.player;

import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.cards.TrainCardType;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnType;
import com.csc570.rsmith.playerai.TTRPlayerAI;

import java.util.Collection;
import java.util.List;

/**
 * Created by rsmith on 2/28/17.
 */
public class TTRComputerPlayer extends TTRPlayer {

    private TTRPlayerAI ai;

    public TTRComputerPlayer(TTRPlayerColor color, TTRPlayerAI ai, GameManager manager) {
        super(color);
        this.ai = ai.getInstance(manager);
        this.ai.setPlayer(this);
    }

    @Override
    public int selectTrainCard(List<TrainCard> flop) {
        return ai.selectTrainCard(flop);
    }

    @Override
    public Route selectRoute(GameBoard board) {
        return ai.selectRoute(board);
    }

    @Override
    public Collection<TrainCard> chooseTrainCards(GameBoard board, Route route, TrainColor trainColor) {
        return ai.playTrainCards();
    }

    @Override
    public TrainColor selectTrackColor(GameBoard board, Route route) {
        return ai.selectTrackColor();
    }

    @Override
    public Collection<DestinationTicketCard> chooseToKeep(
            Collection<DestinationTicketCard> ticketCards, int toKeep) {
        return ai.chooseToKeep(ticketCards, toKeep);
    }

    @Override
    public TurnType selectTurnType() {
        return ai.selectTurnType();
    }

    public int numCards(TrainCardType type) {
        return this.cardFreqs.get(type);
    }
}
