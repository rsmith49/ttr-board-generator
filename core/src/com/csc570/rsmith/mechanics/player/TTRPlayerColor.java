package com.csc570.rsmith.mechanics.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.csc570.rsmith.graphics.GraphicsUtils;

/**
 * Created by rsmith on 2/25/17.
 */
public enum TTRPlayerColor {
    RED (Color.RED, "map_images/FilledTrackRed.png"),
    GREEN (Color.GREEN, "map_images/FilledTrackGreen.png"),
    YELLOW (Color.YELLOW, "map_images/FilledTrackYellow.png"),
    BLUE (Color.BLUE, "map_images/FilledTrackBlue.png"),
    BLACK (Color.BLACK, "map_images/FilledTrackBlack.png");

    private Color graphicsColor;
    private String spriteFilePath;

    TTRPlayerColor(Color color, String filepath) {
        this.graphicsColor = color;
        this.spriteFilePath = filepath;
    }

    public Color getGraphicsColor() {
        return graphicsColor;
    }

    public Sprite getFilledSprite() {
        return GraphicsUtils.getFilledSprite(spriteFilePath);
    }
}
