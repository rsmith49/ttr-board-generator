package com.csc570.rsmith.mechanics.cards;

import com.csc570.rsmith.mechanics.board.TrainColor;

/**
 * Created by rsmith on 2/22/17.
 */
public class TrainCard {

    private TrainCardType type;

    public TrainCard(TrainCardType type) {
        this.type = type;
    }

    public TrainCardType getType() {
        return type;
    }

    public TrainColor getColor() {
        return type.getColor();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TrainCard)) {
            return false;
        }

        return ((TrainCard) other).getType().equals(type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
