package com.csc570.rsmith.mechanics.board;

import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.exceptions.IncompatibleCardsException;
import com.csc570.rsmith.mechanics.exceptions.IncompatibleVarsException;
import com.csc570.rsmith.mechanics.player.TTRPlayer;
import com.csc570.rsmith.mechanics.player.TTRPlayerColor;

import java.util.*;

/**
 * Created by rsmith on 2/22/17.
 */
public class Route {

    public static final float TRACK_TO_MAP_MULTIPIER = 27.62f;

    private Destination start;
    private Destination end;

    private int length;
    private List<TrainColor> colorsAllowed;
    private Set<TTRPlayerColor> playerColorsFilled = new HashSet<>();

    private int tracksFilled = 0;
    private int tracksAvailable;

    private int totalTracks;

    public Route(Destination start, Destination end, int length,
                 Collection<TrainColor> colorsAllowed) {
        this.start = start;
        this.end = end;
        this.length = length;
        this.tracksAvailable = colorsAllowed.size();
        this.totalTracks = tracksAvailable;
        this.colorsAllowed = new ArrayList<>(colorsAllowed);
    }

    /**
     * Used mainly for imports
     * @param start
     * @param end
     * @param length
     * @param colorStringsAllowed
     * @param other
     */
    public Route(Destination start, Destination end, int length,
                 Collection<String> colorStringsAllowed, boolean other) {
        this.start = start;
        this.end = end;
        this.length = length;
        this.tracksAvailable = colorStringsAllowed.size();
        this.totalTracks = tracksAvailable;

        this.colorsAllowed = new ArrayList<>();
        for (String colorString : colorStringsAllowed) {
            TrainColor color = TrainColor.valueOf(colorString);
            colorsAllowed.add(color);
        }
    }

    public Destination getStart() {
        return start;
    }

    public Destination getEnd() {
        return end;
    }

    public int getLength() {
        return length;
    }

    public int numTracks() {
        return colorsAllowed.size();
    }

    public List<TrainColor> getColorsAllowed() {
        return new ArrayList<>(colorsAllowed);
    }

    // VALIDITY CHECKS

    /**
     * Checks if the provided destination is in this route
     * @param destination Destination to check
     * @return If the destination is present or not
     */
    public boolean hasDestination(Destination destination) {
        return start.equals(destination) || end.equals(destination);
    }

    /**
     * Checks if the provided color train is acceptable for the tracks
     * @param color The color to check
     * @return If the color is allowed
     */
    private boolean colorAllowed(TrainColor color, TrainColor trackColor) {
        return color.equals(TrainColor.ANY) ||
                trackColor.equals(color);
    }

    // INTERACTION METHODS

    /**
     * Fills the tracks with the cards (if possible). Otherwise,
     * throws an IncompatibleCardsException
     * @param cards Cards to use to fill the track
     */
    public void fillTrack(Collection<TrainCard> cards, TrainColor colorSelected,
                          TTRPlayerColor playerColor) {
        if (cards.size() != length) {
            throw new IncompatibleCardsException();
        }

        if (playerColorsFilled.contains(playerColor)) {
            throw new IncompatibleVarsException();
        }

        if (colorSelected == TrainColor.ANY) {
            TrainColor mainColor = null;

            for (TrainCard card : cards) {
                if (!card.getColor().equals(TrainColor.ANY) && mainColor == null) {
                    mainColor = card.getColor();
                }
                if (!card.getColor().equals(TrainColor.ANY)) {
                    if (!card.getColor().equals(mainColor)) {
                        throw new IncompatibleCardsException();
                    }
                }
            }
        }
        else {
            for (TrainCard card : cards) {
                if (!colorAllowed(card.getColor(), colorSelected)) {
                    throw new IncompatibleCardsException();
                }
            }
        }

        if (!colorsAllowed.contains(colorSelected)) {
            throw new IncompatibleCardsException();
        }

        --tracksAvailable;
        ++tracksFilled;
        colorsAllowed.remove(colorSelected);
        playerColorsFilled.add(playerColor);
    }

    public List<TTRPlayerColor> getColorsFilled() {
        return new ArrayList<>(playerColorsFilled);
    }

    public int getTracksFilled() {
        return tracksFilled;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    /**
     * A method to check if the requesting player has access to the route
     * @param player The player in question
     * @return If the player has built on the route or not
     */
    public boolean hasAccess(TTRPlayer player) {
        return playerColorsFilled.contains(player.getColor());
    }

    // MISC

    /**
     * Gets the destination that is not the one passed to
     * the function
     * @param one The destination not to return
     * @return The destination on the other side from the destination
     */
    public Destination getOther(Destination one) {
        if (start.equals(one)) {
            return end;
        }
        if (end.equals(one)) {
            return start;
        }

        throw new IncompatibleVarsException();
    }


    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Route)) {
            return false;
        }
        Route otherRoute = (Route) other;
        return (otherRoute.end.equals(end) && otherRoute.start.equals(start) ||
                otherRoute.start.equals(end) && otherRoute.end.equals(start));

        /*return otherRoute.tracksFilled == tracksFilled &&
                (otherRoute.end.equals(end) && otherRoute.start.equals(start) ||
                otherRoute.start.equals(end) && otherRoute.end.equals(start)) &&
                otherRoute.length == length &&
                otherRoute.tracksAvailable == tracksAvailable; */
    }

    @Override
    public int hashCode() {
        List<String> destNames = new ArrayList<>();
        destNames.add(start.getLocationName());
        destNames.add(end.getLocationName());
        Collections.sort(destNames);

        String combinedName = destNames.get(0) + destNames.get(1);

        return combinedName.hashCode();
    }

    @Override
    public String toString() {
        return start.getLocationName() + ", " + end.getLocationName();
    }
}
