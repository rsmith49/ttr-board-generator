package com.csc570.rsmith.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

/**
 * Created by rsmith on 3/2/17.
 */
public class Message {

    private BitmapFont font;
    private String message;
    private GlyphLayout layout;

    private float x, y;

    public Message(BitmapFont font) {
        this.font = font;
    }

    public Message(BitmapFont font, String message) {
        this(font);
        this.message = message;
    }

    public Message(BitmapFont font, float x, float y) {
        this(font);
        this.x = x;
        this.y = y;
    }

    public Message(BitmapFont font, String message, float x, float y) {
        this(font, x, y);
        this.message = message;


    }

    public void setMessage(String newMessage) {
        this.message = newMessage;
    }

    public void setCords(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public BitmapFont getFont() {
        return font;
    }

    public String getMessage() {
        return message;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void draw(Batch batch) {
        font.draw(batch, message, x, y);
    }
}
