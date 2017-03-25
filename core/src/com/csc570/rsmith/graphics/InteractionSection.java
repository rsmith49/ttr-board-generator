package com.csc570.rsmith.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnState;
import com.csc570.rsmith.mechanics.gamestate.TurnType;
import com.csc570.rsmith.graphics.TicketCardHandSection.TicketCardDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsmith on 3/8/17.
 */
public class InteractionSection extends ScreenSection {

    private GameManager manager;
    private BitmapFont font;

    private List<TurnTypeDisplay> turnTypeChoices = new ArrayList<>();
    private List<TicketCardDisplay> ticketCardChoices = new ArrayList<>();
    private List<ColorSelectionDisplay> colorChoices = new ArrayList<>();

    private List<? extends Drawable> currToDraw = new ArrayList<>();

    private RouteInfoDisplay routeInfoDisplay;

    public InteractionSection(GameManager manager, BitmapFont font) {
        super(0,
                GraphicsUtils.BOTTOM_TAB_HEIGHT,
                GraphicsUtils.LEFT_TAB_WIDTH,
                GraphicsUtils.WINDOW_HEIGHT);
        this.manager = manager;
        this.font = font;

        this.routeInfoDisplay = new RouteInfoDisplay(font);
        routeInfoDisplay.setLocation(25, 600);

        int ndx = 0;
        for (TurnType turnType : TurnType.values()) {
            TurnTypeDisplay disp = new TurnTypeDisplay(turnType);
            disp.setLocation(25, y1 - 200 - ndx * 200);
            turnTypeChoices.add(disp);
            ++ndx;
        }


    }

    private boolean selecting;

    @Override
    public void update(float delta) {

        if (manager.getState() == TurnState.SELECT_TURN) {
            if (selecting) {
                //manager.setMessage("Select a Turn Type");

                manager.selector().update(delta);
                if (manager.selector().hasResponse()) {
                    selecting = false;
                    currToDraw = new ArrayList<>();
                }
            }
            else {
                manager.selector().setSelectables(turnTypeChoices,
                        false, false, true);
                selecting = true;
                currToDraw = turnTypeChoices;
            }
        }
        else if (manager.getState() == TurnState.SELECT_TICKET_CARD) {

            if (selecting) {
                manager.selector().update(delta);
            }
            else {
                ticketCardChoices = new ArrayList<>();
                int ndx = 0;
                for (DestinationTicketCard ticketCard : manager.getTicketCardsOffered()) {
                    TicketCardDisplay disp = new TicketCardDisplay(ticketCard, font);
                    disp.setLocation(25, y1 - 200 - ndx * 150);
                    ticketCardChoices.add(disp);
                    ++ndx;
                }
                TicketCardDisplay nullDisp = new TicketCardDisplay(null, font);
                nullDisp.setLocation(25, y1 - 200 - ndx * 150);
                ticketCardChoices.add(nullDisp);

                manager.selector().setSelectables(ticketCardChoices,
                        false, false, true);
                currToDraw = ticketCardChoices;
                manager.setMessage("Select at least " + manager.getNumDestToKeep() + " ticket cards");

                selecting = true;
            }
        }
        else if (manager.getState() == TurnState.SELECT_ROUTE) {
            try {
                routeInfoDisplay.setRoute((Route) manager.selector().queryCursor());
                manager.selector().update(delta);
                selecting = true;
            } catch (ClassCastException e) {
                selecting = false;
            }

            if (manager.selector().hasResponse()) {
                selecting = false;
            }
        }
        else if (manager.getState() == TurnState.SELECT_COLOR) {

            if (selecting) {
                manager.selector().update(delta);
                if (manager.selector().hasResponse()) {
                    selecting = false;
                }

            }
            else {
                manager.setMessage("Select the track color to build on");
                selecting = true;
                colorChoices = new ArrayList<>();
                int ndx = 0;
                for (TrainColor color : manager.getRouteSelected().getColorsAllowed()) {
                    ColorSelectionDisplay colDisp = new ColorSelectionDisplay(font, color);
                    colDisp.setLocation(25, y1 - 200 - ndx * 150);
                    colorChoices.add(colDisp);

                    ++ndx;
                }

                manager.selector().setSelectables(colorChoices,
                        true, false, true);
            }
        }
        else if (manager.getState() == TurnState.SELECT_TRAIN_CARDS) {


        }
        else {
            selecting = false;
            if (currToDraw.size() > 0) {
                currToDraw = new ArrayList<>();
            }
        }


    }

    @Override
    public void draw(Batch batch) {
        font.setColor(Color.BLACK);

        for (Drawable drawable : currToDraw) {
            drawable.draw(batch);
        }

        if (selecting) {
            manager.selector().draw(batch);

            if (manager.getState() == TurnState.SELECT_ROUTE) {
                routeInfoDisplay.draw(batch);
            }
            else if (manager.getState() == TurnState.SELECT_COLOR) {
                for (ColorSelectionDisplay colDisp : colorChoices) {
                    colDisp.draw(batch);
                }
            }
        }
    }

    @Override
    public void dispose() {

    }

    private class TurnTypeDisplay implements Selectable, Drawable {

        static final int WIDTH = 150;
        static final int HEIGHT = 20;

        public Message message;
        public TurnType turnType;

        public TurnTypeDisplay(TurnType turnType) {
            this.turnType = turnType;
            this.message = new Message(font, turnType.name());
        }

        public void setLocation(int x, int y) {
            message.setCords(x, y);
        }

        public void draw(Batch batch) {
            message.draw(batch);
        }

        @Override
        public int getX() {
            return (int) message.getX();
        }

        @Override
        public int getY() {
            return (int) message.getY();
        }

        @Override
        public int getWidth() {
            return WIDTH;
        }

        @Override
        public int getHeight() {
            return HEIGHT;
        }

        @Override
        public Object getInfo() {
            return turnType;
        }

        @Override
        public float getAngle() {
            return 0;
        }
    }

    public static class ColorSelectionDisplay implements Drawable, Selectable {

        static final int WIDTH = 80;
        static final int HEIGHT = 20;

        private TrainColor color;
        private Message colorMessage;

        public ColorSelectionDisplay(BitmapFont font, TrainColor color) {
            this.color = color;
            colorMessage = new Message(font, color.name());
        }

        public void setLocation(int x, int y) {
            colorMessage.setCords(x, y);
        }

        @Override
        public int getX() {
            return (int) colorMessage.getX();
        }

        @Override
        public int getY() {
            return (int) colorMessage.getY() - HEIGHT + 5;
        }

        @Override
        public int getWidth() {
            return WIDTH;
        }

        @Override
        public int getHeight() {
            return HEIGHT;
        }

        @Override
        public float getAngle() {
            return 0;
        }

        @Override
        public Object getInfo() {
            return color;
        }

        @Override
        public void draw(Batch batch) {
            colorMessage.draw(batch);
        }
    }

    public static class RouteInfoDisplay implements Drawable {

        static final int Y_OFFSET = 20;

        private BitmapFont font;

        private int x;
        private int y;

        private Message banner;
        private Message start;
        private Message end;
        private Message length;
        private Message colors;

        public RouteInfoDisplay(BitmapFont font) {
            this.font = font;

            banner = new Message(font, "Route Information");
        }

        public RouteInfoDisplay(Route route, BitmapFont font) {
            this(font);
            setRoute(route);
        }

        public void setRoute(Route route) {
            start = new Message(font, "Start: " + route.getStart().getLocationName(),
                    x, y + 3 * Y_OFFSET);
            end = new Message(font, "End: " + route.getEnd().getLocationName(),
                    x, y + 2 * Y_OFFSET);
            length = new Message(font, "Length: " + route.getLength(),
                    x, y + Y_OFFSET);

            String colorString = "Colors Available: ";
            for (TrainColor color : route.getColorsAllowed()) {
                colorString += color.toString() + ", ";
            }
            colorString = colorString.substring(0, colorString.length() - 2);
            colors = new Message(font, colorString, x, y);
        }

        public void setLocation(int x, int y) {
            this.x = x;
            this.y = y;

            banner.setCords(x, y + 6 * Y_OFFSET);
        }

        @Override
        public void draw(Batch batch) {
            banner.draw(batch);
            start.draw(batch);
            end.draw(batch);
            length.draw(batch);
            colors.draw(batch);
        }
    }


}
