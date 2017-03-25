package com.csc570.rsmith.playerai;

import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnType;
import com.csc570.rsmith.mechanics.player.TTRComputerPlayer;

import java.util.Collection;
import java.util.List;

/**
 * Created by rsmith on 2/28/17.
 */
public abstract class TTRPlayerAI {

    TTRComputerPlayer player;
    GameBoard board;
    PlayerStatCounter stats;

    public TTRPlayerAI(GameBoard board) {
        this.board = board;
        this.stats = new PlayerStatCounter();
    }

    public void setPlayer(TTRComputerPlayer player) {
        this.player = player;
        this.stats.setPlayer(player);
    }

    public abstract TTRPlayerAI getInstance(GameManager manager);

    public abstract int selectTrainCard(List<TrainCard> flop);

    public abstract Route selectRoute(GameBoard board);

    public abstract Collection<TrainCard> playTrainCards();

    public abstract TrainColor selectTrackColor();

    public abstract Collection<DestinationTicketCard> chooseToKeep(
            Collection<DestinationTicketCard> ticketCards, int toKeep);

    public abstract TurnType selectTurnType();

    public PlayerStatCounter getStats() {
        return stats;
    }
}
