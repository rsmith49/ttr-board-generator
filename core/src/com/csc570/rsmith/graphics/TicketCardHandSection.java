package com.csc570.rsmith.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnState;
import com.csc570.rsmith.mechanics.player.TTRPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsmith on 3/7/17.
 */
public class TicketCardHandSection extends ScreenSection {

    private int NUM_CARDS_FIT_ACCROSS = 6;
    private int Y_ROW_OFFSET = 100;
    private int Y_INIT_OFFSET = 110;
    private int X_COL_OFFSET = 100;
    private int X_INIT_OFFSET = 10;

    private GameManager manager;
    private BitmapFont font;

    private TTRPlayer currPlayer = null;
    private int currTurnNdx;

    private List<TicketCardDisplay> displays = new ArrayList<>();

    public TicketCardHandSection(GameManager manager, BitmapFont font) {
        super(GraphicsUtils.BOTTOM_TAB_WIDTH / 2, 0,
                GraphicsUtils.BOTTOM_TAB_WIDTH, GraphicsUtils.BOTTOM_TAB_HEIGHT);
        this.manager = manager;
        this.font = font;
    }

    @Override
    public void update(float delta) {
        if (currPlayer == null || !manager.getCurrentPlayer().equals(currPlayer) ||
                manager.getTurnNdx() > currTurnNdx) {
            currPlayer = manager.getCurrentPlayer();
            currTurnNdx = manager.getTurnNdx();
            displays = new ArrayList<>();

            int ndx = 0;
            int yOffset = 0;
            for (DestinationTicketCard card : currPlayer.getTicketCards()) {
                TicketCardDisplay disp = new TicketCardDisplay(card, font);
                if (ndx + 1 > NUM_CARDS_FIT_ACCROSS) {
                    ndx = 0;
                    yOffset = Y_ROW_OFFSET;
                }

                disp.setLocation(this.x0 + X_INIT_OFFSET + ndx * X_COL_OFFSET,
                        this.y1 - Y_INIT_OFFSET - yOffset);

                displays.add(disp);
                ++ndx;
            }
        }

    }

    @Override
    public void draw(Batch batch) {
        if (manager.getState() != TurnState.TURN_START) {
            font.setColor(Color.BLACK);
            for (TicketCardDisplay disp : displays) {
                disp.draw(batch);
            }
        }
    }

    @Override
    public void dispose() {

    }

    public static class TicketCardDisplay implements Drawable, Selectable {
        static final int VERTICAL_OFFSET = 15;
        static final int WIDTH = 100;

        public Message startDest;
        public Message endDest;
        public Message points;
        public Message completed;
        public DestinationTicketCard ticketCard;
        BitmapFont font;

        public TicketCardDisplay(DestinationTicketCard card, BitmapFont font) {
            this.font = font;

            if (card == null) {
                this.startDest = new Message(font, "");
                this.endDest = new Message(font, "");
                this.points = new Message(font, "");
                this.completed = new Message(font, "Finish Selecting");
                this.ticketCard = null;

            }
            else {
                this.startDest = new Message(font, card.getStart().getLocationName());
                this.endDest = new Message(font, card.getEnd().getLocationName());
                this.points = new Message(font, "Points: " + card.getDistance());
                this.completed = new Message(font, "Done: " + card.isCompleted());
                this.ticketCard = card;
            }
        }

        public void setLocation(int x, int y) {
            startDest.setCords(x, y + 3 * VERTICAL_OFFSET);
            endDest.setCords(x, y + 2 * VERTICAL_OFFSET);
            points.setCords(x, y + VERTICAL_OFFSET);
            completed.setCords(x, y);
        }

        @Override
        public void draw(Batch batch) {
            font.setColor(Color.BLACK);
            startDest.draw(batch);
            endDest.draw(batch);
            points.draw(batch);

            if (ticketCard != null && ticketCard.isCompleted()) {
                font.setColor(Color.GREEN);
            }
            else if (ticketCard != null) {
                font.setColor(Color.RED);
            }

            completed.draw(batch);
        }

        @Override
        public int getX() {
            return (int) completed.getX();
        }

        @Override
        public int getY() {
            return (int) completed.getY();
        }

        @Override
        public int getWidth() {
            return WIDTH;
        }

        @Override
        public int getHeight() {
            return 5 * VERTICAL_OFFSET / 2;
        }

        @Override
        public float getAngle() {
            return 0;
        }

        @Override
        public Object getInfo() {
            return ticketCard;
        }
    }
}
