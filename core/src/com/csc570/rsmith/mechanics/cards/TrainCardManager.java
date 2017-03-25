package com.csc570.rsmith.mechanics.cards;

import com.csc570.rsmith.mechanics.exceptions.InfiniteLoopException;

import java.util.*;

/**
 * Created by rsmith on 2/22/17.
 */
public class TrainCardManager extends CardManager<TrainCard> {

    public static final int FLOP_SIZE = 5;
    public static final int MAX_FLOP_LOCS = 3;
    public static final int DEFAULT_CARD_NUM = 12;
    public static final int LOCO_CARD_NUM = 14;

    private List<TrainCard> flop;

    @Override
    public void init() {

        List<TrainCard> tmpList = new ArrayList<>();
        // Add all cards to deck to start
        for (TrainCardType cardType : TrainCardType.values()) {
            int toAdd;
            if (cardType == TrainCardType.LOCOMOTIVE) {
                toAdd = LOCO_CARD_NUM;
            }
            else {
                toAdd = DEFAULT_CARD_NUM;
            }

            for (int ndx = 0; ndx < toAdd; ++ndx) {
                tmpList.add(new TrainCard(cardType));
            }
        }

        Collections.shuffle(tmpList, random);

        deck = new ArrayDeque<>(tmpList);
        flop = new ArrayList<>(FLOP_SIZE);
        discard = new ArrayList<>();

        reDealFlop();
        checkFlop();
    }

    // FLOP METHODS

    /**
     * Gets a view of the flop so a player can make a decision on if they
     * want any of the cards in the flop
     * @return A list view of the flop
     */
    public List<TrainCard> viewFlop() {
        return new ArrayList<>(flop);
    }

    /**
     * Takes a specific card from the flop (-1 for from deck)
     * Should only be called when the flop size is 5
     * @param index Index of the flop to take the card from
     * @return The card selected
     */
    public TrainCard takeFromFlop(int index) {
        if (index == -1) {
            return draw();
        }

        TrainCard ans = flop.get(index);
        flop.set(index, draw());
        checkFlop();

        return ans;
    }

    /**
     * Redeal the flop, discarding any cards that were there previously
     */
    private void reDealFlop() {
        for (int ndx = 0; ndx < FLOP_SIZE; ++ndx) {
            if (flop.size() > ndx) {
                discard.add(flop.get(ndx));
                flop.set(ndx, draw());
            }
            else {
                flop.add(draw());
            }
        }
    }

    // For being able to throw the InfiniteLoopException
    private static final int MAX_ITS = 200;

    /**
     * Checks to make sure the appropriate number of LOCO's are in the flop,
     * otherwise reDeal's it
     */
    private void checkFlop() {
        int numLocs = 0, numIts = 0;
        for (TrainCard card : flop) {
            if (card.getType() == TrainCardType.LOCOMOTIVE) {
                ++numLocs;
            }
        }

        while (numLocs >= MAX_FLOP_LOCS && numIts < MAX_ITS) {
            reDealFlop();

            numLocs = 0;
            for (TrainCard card : flop) {
                if (card.getType() == TrainCardType.LOCOMOTIVE) {
                    ++numLocs;
                }
            }
            ++numIts;
        }

        if (numIts >= MAX_ITS) {
            String errMsg = "";
            for (TrainCard card : deck) {
                errMsg += ", " + card.getType().getColor();
            }
            for (TrainCard card : discard) {
                errMsg += ", " + card.getType().getColor();
            }
            for (TrainCard card : flop) {
                errMsg += ", " + card.getType().getColor();
            }
            //System.err.println("Cards: " + errMsg);

            throw new InfiniteLoopException();
        }
    }
}
