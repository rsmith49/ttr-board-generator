package com.csc570.rsmith.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.TrainCard;
import com.csc570.rsmith.mechanics.cards.TrainCardType;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnState;
import com.csc570.rsmith.mechanics.player.TTRHumanPlayer;
import com.csc570.rsmith.mechanics.player.TTRPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsmith on 3/7/17.
 */
public class TrainCardHandSection extends ScreenSection {

    private GameManager manager;
    private BitmapFont font;

    private List<CardAmountDisplay> cardNums = new ArrayList<>();
    private List<CardAmountDisplay> availableCardNums;

    private ScoreDisplay scoreDisplay;
    private ScoreDisplay trainCountDisplay;

    public TrainCardHandSection(GameManager manager, BitmapFont font) {
        super(0, 0,
                GraphicsUtils.BOTTOM_TAB_WIDTH / 2,
                GraphicsUtils.BOTTOM_TAB_HEIGHT);
        this.manager = manager;
        this.font = font;

        for (TrainCardType trainCardType : TrainCardType.values()) {
            Sprite sprite = trainCardType.getCardImage();
            Message message = new Message(
                    font, "x " + manager.getCurrentPlayer().getCardFreqs().get(trainCardType),
                    sprite.getX(), sprite.getY() - GraphicsUtils.TRAINCARD_NUM_Y_OFFSET
            );

            cardNums.add(new CardAmountDisplay(message, sprite, trainCardType));
        }

        scoreDisplay = new ScoreDisplay(font);
        scoreDisplay.setLocation(
                GraphicsUtils.TRAIN_X_OFFSET + GraphicsUtils.TRAIN_X_INC *
                        (TrainCardType.values().length / 2),
                GraphicsUtils.TRAIN_Y_OFFSET);

        trainCountDisplay = new ScoreDisplay(font, "Trains:");
        trainCountDisplay.setLocation(
                GraphicsUtils.TRAIN_X_OFFSET + GraphicsUtils.TRAIN_X_INC *
                        (TrainCardType.values().length / 2) + GraphicsUtils.TRAIN_COUNT_OFFSET,
                GraphicsUtils.TRAIN_Y_OFFSET);
    }

    private boolean selecting = false;

    @Override
    public void update(float delta) {
        for (CardAmountDisplay cardDisp : cardNums) {
            cardDisp.setNumber(manager.getCurrentPlayer().getCardFreqs().get(cardDisp.type));
        }

        scoreDisplay.update(manager.getCurrentPlayer());
        trainCountDisplay.updateTrainCount(manager.getCurrentPlayer());

        if (manager.getState() == TurnState.SELECT_TRAIN_CARDS) {
            if (selecting) {
                manager.selector().update(delta);
                if (manager.selector().hasResponse()) {
                    selecting = false;
                }
            }
            else {
                selecting = true;
                setAvailableCardNums();

                manager.selector().setSelectables(availableCardNums,
                        true, false, false);
            }
        }
        else {
            selecting = false;
        }

    }

    @Override
    public void draw(Batch batch) {
        if (manager.getState() != TurnState.TURN_START) {
            font.setColor(Color.BLACK);

            for (CardAmountDisplay cardDisp : cardNums) {
                cardDisp.draw(batch);
            }

            scoreDisplay.draw(batch);
            trainCountDisplay.draw(batch);

            if (selecting) {
                manager.selector().draw(batch);
            }
        }

    }

    public void setAvailableCardNums() {
        availableCardNums = new ArrayList<>();
        for (CardAmountDisplay disp : cardNums) {
            if (disp.number > 0) {
                availableCardNums.add(disp);
            }
        }
    }

    private static class CardAmountDisplay implements Drawable, Selectable {

        Message message;
        Sprite sprite;
        TrainCardType type;
        int number;

        CardAmountDisplay(Message message, Sprite sprite, TrainCardType type) {
            this.message = message;
            this.sprite = sprite;
            this.type = type;
        }

        public void setNumber(int num) {
            number = num;

            message.setMessage("x " + number);
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
            return (int) sprite.getWidth();
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
            return type;
        }

        @Override
        public void draw(Batch batch) {
            sprite.draw(batch);
            message.draw(batch);
        }
    }

    public static class ScoreDisplay implements Drawable {

        private static final int Y_OFFSET = 20;

        private Message scoreText;
        private Message scoreAmount;

        public ScoreDisplay(BitmapFont font) {
            scoreText = new Message(font, "Score:");
            scoreAmount = new Message(font, "0");
        }

        public ScoreDisplay(BitmapFont font, String topMessage) {
            this(font);
            scoreText.setMessage(topMessage);
        }

        public void setLocation(int x, int y) {
            scoreText.setCords(x, y + Y_OFFSET);
            scoreAmount.setCords(x, y);
        }

        public void update(TTRPlayer player) {
            scoreAmount.setMessage("" + player.getPoints());
        }

        public void updateTrainCount(TTRPlayer player) {
            scoreAmount.setMessage("" + player.getTrainsLeft());
        }

        @Override
        public void draw(Batch batch) {
            scoreText.draw(batch);
            scoreAmount.draw(batch);
        }
    }

    @Override
    public void dispose() {

    }
}
