package com.csc570.rsmith.mechanics.cards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.csc570.rsmith.graphics.GraphicsUtils;
import com.csc570.rsmith.mechanics.board.TrainColor;

/**
 * Created by rsmith on 2/22/17.
 */
public enum TrainCardType {

    BOX(TrainColor.PINK, 6),
    PASSENGER(TrainColor.WHITE, 0),
    TANKER(TrainColor.BLUE, 2),
    REEFER(TrainColor.YELLOW, 4),
    FREIGHT(TrainColor.ORANGE, 1),
    HOPPER(TrainColor.BLACK, 3),
    COAL(TrainColor.RED, 5),
    CABOOSE(TrainColor.GREEN, 7),
    LOCOMOTIVE(TrainColor.ANY, 8);

    private final TrainColor color;
    private int textureRegionIndex;

    TrainCardType(TrainColor color, int cardImageNdx) {
        this.color = color;
        this.textureRegionIndex = cardImageNdx;
    }

    public TrainColor getColor() {
        return color;
    }

    public Sprite getCardImage() {
        return GraphicsUtils.getTrainCardSprites().get(textureRegionIndex);
    }

    public static TrainCardType getTypeOf(TrainColor color) {
        for (TrainCardType type : TrainCardType.values()) {
            if (type.getColor().equals(color)) {
                return type;
            }
        }
        return null;
    }
}
