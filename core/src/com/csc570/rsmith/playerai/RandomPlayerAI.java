package com.csc570.rsmith.playerai;

import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by rsmith on 3/7/17.
 *
 * TODO: Should only conduct turns that are possible (i.e. check for deck size before getting train cards)
 */
public class RandomPlayerAI extends TTRPlayerAI {

    private Random random = new Random();


    public RandomPlayerAI(GameBoard board) {
        super(board);
    }

    @Override
    public TTRPlayerAI getInstance(GameManager manager) {
        return new RandomPlayerAI(board);
    }

    // AI methods

    @Override
    public int selectTrainCard(List<TrainCard> flop) {
        return random.nextInt(flop.size() + 1) - 1;
    }

    @Override
    public Route selectRoute(GameBoard board) {
        return null;
    }

    @Override
    public Collection<TrainCard> playTrainCards() {
        return null;
    }

    @Override
    public TrainColor selectTrackColor() {
        return null;
    }

    @Override
    public Collection<DestinationTicketCard> chooseToKeep(
            Collection<DestinationTicketCard> ticketCards, int toKeep) {
        ArrayList<DestinationTicketCard> ans = new ArrayList<>();
        int ndx = 0;
        for (DestinationTicketCard card : ticketCards) {
            if (ndx++ < toKeep) {
                ans.add(card);
            }
        }

        return ans;
    }

    @Override
    public TurnType selectTurnType() {
        return TurnType.values()[random.nextInt(2) + 1];
    }
}
