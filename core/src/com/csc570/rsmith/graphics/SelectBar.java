package com.csc570.rsmith.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsmith on 3/7/17.
 */
public class SelectBar implements Drawable {

    static final int X_OFFSET = 15;
    static final int Y_OFFSET = 15;

    private List<? extends Selectable> selectables;
    private Sprite sprite;
    private int currNdx = 0;
    private int selectionsMade = 0;
    private boolean withReplacement = false;
    private boolean noOffset = false;
    private boolean resetNdx = true;

    private Object response;
    private boolean hasResponse = false;

    public SelectBar() {
        sprite = GraphicsUtils.getYellowBlockSprite();
    }

    private void setSelectables(List<? extends Selectable> selectables, boolean withReplacement) {
        this.selectables = new ArrayList<>(selectables);
        if (resetNdx || currNdx >= selectables.size() || currNdx < 0) {
            currNdx = 0;
        }

        if (selectables.size() > 0) {
            setBounds(selectables.get(currNdx));
        }
        selectionsMade = 0;
        this.withReplacement = withReplacement;
    }

    public void setSelectables(List<? extends Selectable> selectables, boolean withReplacement,
                               boolean noOffset, boolean resetNdx) {
        this.noOffset = noOffset;
        this.resetNdx = resetNdx;
        setSelectables(selectables, withReplacement);
    }

    private void setBounds(Selectable object, int xOffset, int yOffset) {
        sprite.setBounds(object.getX() - xOffset, object.getY() - yOffset,
                object.getWidth() + 2 * xOffset, object.getHeight() + 2 * yOffset);

        sprite.setOrigin(0, 0);
        sprite.setRotation(object.getAngle());
    }

    private void setBounds(Selectable object) {
        if (noOffset) {
            setBounds(object, 1, 1);
        }
        else {
            setBounds(object, X_OFFSET, Y_OFFSET);
        }
    }

    private void addNdx(int increment) {
        currNdx += increment;
        if (currNdx >= selectables.size()) {
            currNdx -= selectables.size();
        }
        else if (currNdx < 0) {
            currNdx += selectables.size();
        }
    }

    public void update(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            addNdx(1);
            setBounds(selectables.get(currNdx));
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            addNdx(-1);
            setBounds(selectables.get(currNdx));
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            response = selectables.get(currNdx).getInfo();
            hasResponse = true;

        }

    }

    public Object queryCursor() {
        if (selectables.size() > currNdx) {
            return selectables.get(currNdx).getInfo();
        }

        return null;
    }

    public boolean hasResponse() {
        return hasResponse;
    }

    public Object pollResponse() {
        return response;
    }

    public void removeResponse() {
        hasResponse = false;
    }

    public Object getResponse() {
        if (!withReplacement) {
            selectables.remove(currNdx);
        }

        if (resetNdx || currNdx >= selectables.size()) {
            currNdx = 0;
            if (selectables.size() > 0) {
                setBounds(selectables.get(currNdx));
            }
        }
        else {
            if (!withReplacement) {
                --currNdx;
            }
        }

        hasResponse = false;
        ++selectionsMade;
        return response;
    }

    @Override
    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    public int getSelectionsMade() {
        return selectionsMade;
    }
}
