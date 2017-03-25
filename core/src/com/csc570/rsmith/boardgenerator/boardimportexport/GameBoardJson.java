package com.csc570.rsmith.boardgenerator.boardimportexport;

import java.util.ArrayList;

/**
 * Created by rsmith on 2/25/17.
 */
public class GameBoardJson {

    private ArrayList<DestinationJson> destinations;
    private ArrayList<DestinationTicketCardJson> ticketCards;

    public ArrayList<DestinationJson> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<DestinationJson> destinations) {
        this.destinations = destinations;
    }

    public ArrayList<DestinationTicketCardJson> getTicketCards() {
        return ticketCards;
    }

    public void setTicketCards(ArrayList<DestinationTicketCardJson> ticketCards) {
        this.ticketCards = ticketCards;
    }
}
