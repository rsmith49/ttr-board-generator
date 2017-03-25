package com.csc570.rsmith.mechanics.cards;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by rsmith on 2/23/17.
 */
public class DestinationTicketCardManager extends
        CardManager<DestinationTicketCard> {

    public static int DEFAULT_NUM_TICKET_CARDS = 30;

    public DestinationTicketCardManager(Collection<DestinationTicketCard> cards) {
        ArrayList<DestinationTicketCard> tmp = new ArrayList<>(cards);
        Collections.shuffle(tmp);

        deck = new ArrayDeque<>(tmp);
        discard = new ArrayList<>();
    }

    @Override
    public void init() {
        deck = new ArrayDeque<>();
        discard = new ArrayList<>();
    }
}
