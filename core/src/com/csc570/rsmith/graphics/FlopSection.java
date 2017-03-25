package com.csc570.rsmith.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.cards.TrainCardManager;
import com.csc570.rsmith.mechanics.cards.TrainCardType;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnState;
import com.csc570.rsmith.mechanics.gamestate.TurnType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsmith on 3/7/17.
 */
public class FlopSection extends ScreenSection {

    private GameManager manager;
    private BitmapFont font;
    private Texture deckText;

    private List<FlopDisplay> displays = new ArrayList<>();

    public FlopSection(GameManager manager, BitmapFont font) {
        super(GraphicsUtils.WINDOW_WIDTH - GraphicsUtils.RIGHT_TAB_WIDTH,
                GraphicsUtils.BOTTOM_TAB_HEIGHT,
                GraphicsUtils.WINDOW_WIDTH,
                GraphicsUtils.WINDOW_HEIGHT);
        this.manager = manager;
        this.font = font;

        deckText = new Texture("question_mark.jpg");
    }

    private boolean selecting = false;

    @Override
    public void update(float delta) {

        if (!selecting) {
            displays = new ArrayList<>();

            for (int ndx = 0; ndx <= TrainCardManager.FLOP_SIZE; ++ndx) {
                FlopDisplay disp;

                if (ndx >= manager.getTrainCardManager().viewFlop().size()) {
                    disp = new FlopDisplay(null, ndx);
                } else {
                    disp = new FlopDisplay(
                            manager.getTrainCardManager().viewFlop().get(ndx), ndx);
                }

                disp.setLocation(x0 + 30, y1 - 170 - 85 * ndx);

                displays.add(disp);
            }
        }

        if (manager.getTurnType() == TurnType.GET_TRAIN_CARDS &&
                manager.getState() == TurnState.SELECT_TRAIN_CARD) {
            if (selecting) {

                manager.selector().update(delta);
                if (manager.selector().hasResponse()) {
                    selecting = false;

                }
            }
            else {
                manager.setMessage("Select a Train Card");
                selecting = true;
                manager.selector().setSelectables(displays,
                        true, false, true);
            }

        }

    }

    @Override
    public void draw(Batch batch) {

        for (FlopDisplay disp : displays) {
            font.setColor(disp.cardType.getColor().getGraphicsColor());

            disp.draw(batch);
        }

        if (selecting) {
            manager.selector().draw(batch);
        }
    }

    @Override
    public void dispose() {
        deckText.dispose();
    }

    private class FlopDisplay implements Selectable, Drawable {
        static final int MESSAGE_X_OFFSET = 25;
        static final int MESSAGE_Y_OFFSET = 15;

        public Sprite sprite;
        public Message indexMessage;
        public TrainCardType cardType;
        public int index;

        public FlopDisplay(TrainCard card, int index) {
            if (card == null) {
                sprite = new Sprite(deckText);
                sprite.setBounds(0, 0,
                        GraphicsUtils.TRAIN_CARD_WIDTH,
                        GraphicsUtils.TRAIN_CARD_HEIGHT);
                this.cardType = TrainCardType.LOCOMOTIVE;
            }
            else {
                sprite = card.getType().getCardImage();
                this.cardType = card.getType();
            }
            this.index = index;
            this.indexMessage = new Message(font, "" + index);
        }

        public void setLocation(int x, int y) {
            sprite.setBounds(x, y, sprite.getWidth(), sprite.getHeight());
            indexMessage.setCords(x + sprite.getWidth() + MESSAGE_X_OFFSET, y + MESSAGE_Y_OFFSET);
        }

        public void draw(Batch batch) {
            sprite.draw(batch);
            indexMessage.draw(batch);
        }


        @Override
        public int getX() {
            return (int) sprite.getX();
        }

        @Override
        public int getY() {
            return (int) sprite.getY();
        }

        @Override
        public int getWidth() {
            return (int) sprite.getWidth() + MESSAGE_X_OFFSET;
        }

        @Override
        public int getHeight() {
            return (int) sprite.getHeight();
        }

        @Override
        public float getAngle() {
            return 0;
        }

        @Override
        public Object getInfo() {
            return index;
        }
    }
}
