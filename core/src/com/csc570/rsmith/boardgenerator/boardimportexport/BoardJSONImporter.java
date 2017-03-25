package com.csc570.rsmith.boardgenerator.boardimportexport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.csc570.rsmith.mechanics.board.Destination;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;

import java.io.File;
import java.util.*;

/**
 * Created by rsmith on 2/25/17.
 */
public class BoardJSONImporter {

    public static GameBoard importBoard(String filepath) {
        Json json = new Json();
        FileHandle fileIn = Gdx.files.internal(filepath);

        GameBoardJson tmpBoard = json.fromJson(GameBoardJson.class, fileIn);

        Set<Route> routes = new HashSet<>();
        Map<String, Destination> destinations = new HashMap<>();

        for (DestinationJson dest : tmpBoard.getDestinations()) {
            destinations.put(dest.getName(), new Destination(dest.getName()));
        }

        for (int ndx = 0; ndx < destinations.size(); ++ndx) {
            DestinationJson jsonDest = tmpBoard.getDestinations().get(ndx);
            Destination realDest = destinations.get(jsonDest.getName());
            realDest.setCords(jsonDest.getXCord(), jsonDest.getYCord());

            for (RouteJson jsonRoute : jsonDest.getRoutes()) {
                Route realRoute = new Route(
                        destinations.get(jsonRoute.getStart()),
                        destinations.get(jsonRoute.getEnd()),
                        jsonRoute.getLength(),
                        jsonRoute.getColorsAllowed(),
                        true
                );

                if (routes.contains(realRoute)) {
                    for (Route route : routes) {
                        if (route.equals(realRoute)) {
                            realDest.addRoute(route);
                        }
                    }
                }
                else {
                    realDest.addRoute(realRoute);
                    routes.add(realRoute);
                }
            }
        }

        Set<DestinationTicketCard> ticketCards = new HashSet<>();

        for (DestinationTicketCardJson jsonCard : tmpBoard.getTicketCards()) {
            DestinationTicketCard ticketCard = new DestinationTicketCard(
                    destinations.get(jsonCard.getStart()),
                    destinations.get(jsonCard.getEnd())
            );

            ticketCards.add(ticketCard);
        }



        return new GameBoard(routes, ticketCards);
    }
}
