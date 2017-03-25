package com.csc570.rsmith.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by rsmith on 3/7/17.
 */
public abstract class ScreenSection {

    // These are the coordinates corresponding to the rectangle the section
    // contains: bottom left (x0, y0), top right (x1, y1)
    int x0, y0, x1, y1;

    public ScreenSection(int x0, int y0, int x1, int y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public abstract void update(float delta);

    public abstract void draw(Batch batch);

    public abstract void dispose();
}
