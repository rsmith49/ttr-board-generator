package com.csc570.rsmith.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

/**
 * Created by rsmith on 3/11/17.
 */
public class LayoutMessage extends Message {

    private GlyphLayout layout;
    private Color color;
    private float targetLength;
    private int align;
    private boolean wrap = false;

    public LayoutMessage(BitmapFont font, String message, float targetLength) {
        super(font, message);
        this.targetLength = targetLength;
        this.align = Align.center;
        this.color = Color.BLACK;
        layout = new GlyphLayout();
        setLayout();
    }

    public void setColor(Color color) {
        this.color = color;
        setLayout();
    }

    public void setTargetLength(float targetLength) {
        this.targetLength = targetLength;
        setLayout();
    }

    public void setAlign(int align) {
        this.align = align;
        setLayout();
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
        setLayout();
    }

    private void setLayout() {
        layout.setText(getFont(), getMessage(), color, targetLength, align, wrap);
    }

    public GlyphLayout getLayout() {
        return layout;
    }

    public Color getColor() {
        return color;
    }

    public float getTargetLength() {
        return targetLength;
    }

    public int getAlign() {
        return align;
    }

    public boolean isWrap() {
        return wrap;
    }

    @Override
    public void draw(Batch batch) {
        getFont().draw(batch, layout, getX(), getY());
    }
}
