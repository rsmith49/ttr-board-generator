package com.csc570.rsmith.mechanics.cards;

import com.csc570.rsmith.mechanics.exceptions.InfiniteLoopException;
import com.csc570.rsmith.mechanics.exceptions.OutOfCardsException;

import java.util.*;

/**
 * Created by rsmith on 2/22/17.
 */
public abstract class CardManager<T> {

    Queue<T> deck;
    List<T> discard;

    Random random  = new Random();

    public CardManager() {
        init();
    }

    public abstract void init();

    // DECK METHODS

    /**
     * Draw a card from the deck, throws an OutOfCardsException if
     * players are hoarding all the available cards
     * @return The TrainCard object drawn
     */
    public T draw() {
        T ans = deck.poll();
        if (ans == null) {
            if (discard.size() == 0) {
                throw new OutOfCardsException();
            }

            Collections.shuffle(discard);
            deck.addAll(discard);
            discard.clear();

            ans = deck.remove();
        }

        return ans;
    }

    public Collection<T> draw(int num) {
        Collection<T> ans = new ArrayList<>();

        for (int ndx = 0; ndx < num; ++ndx) {
            ans.add(draw());
        }

        return ans;
    }

    // DISCARD METHODS

    /**
     * Discards a card to the discard pile
     * @param card Card to be discarded
     */
    public void discard(T card) {
        discard.add(card);
    }

    /**
     * Discards a collection of cards to the discard pile
     * @param cards Cards to be discarded
     */
    public void discardAll(Collection<T> cards) {
        for (T card : cards) {
            discard(card);
        }
    }

    /**
     * Gets the current size of the deck
     * @return The size of the deck
     */
    public int getDeckSize() {
        if (deck.size() <= 1) {

            Collections.shuffle(discard);
            deck.addAll(discard);
            discard.clear();
        }

        return deck.size();
    }
}
