package com.csc570.rsmith.mechanics.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.csc570.rsmith.graphics.GraphicsUtils;
import com.csc570.rsmith.mechanics.cards.TrainCardType;

/**
 * Created by rsmith on 2/22/17.
 */
public enum TrainColor {

    PINK (Color.PINK, "map_images/TrackPink.png"),
    WHITE (Color.WHITE, "map_images/TrackWhite.png"),
    BLUE (Color.BLUE, "map_images/TrackBlue.png"),
    YELLOW (Color.YELLOW, "map_images/TrackYellow.png"),
    ORANGE (Color.ORANGE, "map_images/TrackOrange.png"),
    BLACK (Color.BLACK, "map_images/TrackBlack.png"),
    RED (Color.RED, "map_images/TrackRed.png"),
    GREEN (Color.GREEN, "map_images/TrackGreen.png"),
    ANY (Color.GRAY, "map_images/TrackGray.png");

    private Color graphicsColor;
    private String fileName;

    TrainColor(Color color, String fileName) {
        this.graphicsColor = color;
        this.fileName = fileName;
    }

    public Color getGraphicsColor() {
        return graphicsColor;
    }

    public Sprite getTrackSprite() {
        return GraphicsUtils.getTrackSprite(fileName);
    }
}
