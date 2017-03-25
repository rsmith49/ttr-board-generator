package com.csc570.rsmith.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.csc570.rsmith.mechanics.board.Destination;
import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.gamestate.TurnState;
import com.csc570.rsmith.mechanics.player.TTRPlayerColor;

import java.util.*;

/**
 * Created by rsmith on 3/8/17.
 */
public class MapSection extends ScreenSection {

    private GameBoard board;
    private GameManager manager;
    private BitmapFont font;

    private Texture destText;
    private Texture routeText;

    private Map<String, DestDisplay> destDisps = new HashMap<>();
    private List<RouteDisplay> routeDisps = new ArrayList<>();

    public MapSection(GameBoard board, GameManager manager) {
        super(GraphicsUtils.LEFT_TAB_WIDTH,
                GraphicsUtils.BOTTOM_TAB_HEIGHT,
                GraphicsUtils.WINDOW_WIDTH - GraphicsUtils.RIGHT_TAB_WIDTH,
                GraphicsUtils.WINDOW_HEIGHT);
        this.board = board;
        this.manager = manager;
        this.font = new BitmapFont();

        int ndx = 0;

        for (Route route : board.getRoutes()) {

            if (ndx++ >= 0) {
                destDisps.putIfAbsent(route.getStart().getLocationName(),
                        new DestDisplay(route.getStart(), font));
                destDisps.putIfAbsent(route.getEnd().getLocationName(),
                        new DestDisplay(route.getEnd(), font));

                routeDisps.add(new RouteDisplay(route,
                        destDisps.get(route.getStart().getLocationName()),
                        destDisps.get(route.getEnd().getLocationName())));
            }
        }

    }

    private boolean selecting = false;

    @Override
    public void update(float delta) {
        for (RouteDisplay routeDisp : routeDisps) {
            routeDisp.update(delta);
        }

        if (manager.getState() == TurnState.SELECT_ROUTE) {

            if (selecting) {

                // This updates twice, which makes the selector select every 2
                //manager.selector().update(delta);

                if (manager.selector().hasResponse()) {
                    selecting = false;
                }
            }
            else {
                List<RouteDisplay> availableRoutes = new ArrayList<>();
                for (RouteDisplay disp : routeDisps) {
                    if (disp.route.numTracks() > 0) {
                        availableRoutes.add(disp);
                    }
                }

                availableRoutes.sort(new Comparator<RouteDisplay>() {
                    @Override
                    public int compare(RouteDisplay o1, RouteDisplay o2) {
                        return (int) (o1.xCord - o2.xCord);
                    }
                });

                manager.selector().setSelectables(availableRoutes,
                        true, true, true);
                selecting = true;
                manager.setMessage("Select the route you wish to build on.");
            }

        }
    }

    @Override
    public void draw(Batch batch) {
        for (DestDisplay destDisp : destDisps.values()) {
            destDisp.drawBackground(batch);
        }

        for (RouteDisplay routeDisp : routeDisps) {
            routeDisp.draw(batch);
        }

        for (DestDisplay destDisp : destDisps.values()) {
            destDisp.drawName(batch);
        }

        if (selecting) {
            manager.selector().draw(batch);
        }
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public static class DestDisplay implements Drawable {

        public static final float RADIUS =
                (GraphicsUtils.DEST_HEIGHT + 0.8f * GraphicsUtils.DEST_WIDTH) / 2;

        private static final float PIX_PER_LETTER = 4;
        private static final int Y_OFFSET = 5;

        private Destination dest;
        private Message destNameMessage;
        private BitmapFont font;
        private Sprite destSprite;

        private int xCord;
        private int yCord;

        public DestDisplay(Destination destination, BitmapFont font) {
            dest = destination;
            this.font = font;
            destNameMessage = new Message(font, dest.getLocationName());
            destSprite = GraphicsUtils.getDestSprite();

            xCord = (int) ((dest.getxCord() - GraphicsUtils.DEST_WIDTH / 2)
                    / Destination.TOTAL_WIDTH * (GraphicsUtils.MAP_TAB_WIDTH - GraphicsUtils.DEST_WIDTH))
                    + GraphicsUtils.LEFT_TAB_WIDTH + GraphicsUtils.DEST_WIDTH / 2;
            yCord = (int) ((dest.getyCord() - GraphicsUtils.DEST_HEIGHT / 2)
                    / Destination.TOTAL_HEIGHT * (GraphicsUtils.MAP_TAB_HEIGHT - GraphicsUtils.DEST_HEIGHT))
                    + GraphicsUtils.BOTTOM_TAB_HEIGHT + GraphicsUtils.DEST_HEIGHT / 2;

            destSprite.setBounds(xCord, yCord,
                    GraphicsUtils.DEST_WIDTH, GraphicsUtils.DEST_HEIGHT);


            float messageX = xCord + GraphicsUtils.DEST_WIDTH / 2;
            messageX -= dest.getLocationName().length() * PIX_PER_LETTER / 2;

            destNameMessage.setCords(messageX, yCord + GraphicsUtils.DEST_HEIGHT / 2 + Y_OFFSET);
        }

        public float getXCord() {
            return xCord + GraphicsUtils.DEST_WIDTH / 2;
        }

        public float getYCord() {
            return yCord + GraphicsUtils.DEST_HEIGHT / 2;
        }

        public void drawBackground(Batch batch) {
            destSprite.draw(batch);
        }

        public void drawName(Batch batch) {
            font.setColor(Color.BLUE);
            font.getData().setScale(0.6f, 0.7f);

            destNameMessage.draw(batch);
        }

        @Override
        public void draw(Batch batch) {
            drawBackground(batch);
            drawName(batch);
        }

        @Override
        public int hashCode() {
            return dest.hashCode();
        }
    }

    public static class RouteDisplay implements Drawable, Selectable {

        public static final float TRACK_LONG_SPACE = 4;
        public static final float TRACK_WIDE_SPACE = 3;
        public static final float TOTAL_ROUTE_HEIGHT = 10;
        public static final float RAD_TO_DEG = (float) (180 / Math.PI);

        private Route route;
        private DestDisplay start;
        private DestDisplay end;

        private int tracksFilled = 0;
        private Set<TTRPlayerColor> playerColorsFilled = new HashSet<>();

        private List<TracksPair> trackSprites = new ArrayList<>();
        private List<Sprite> filledSprites = new ArrayList<>();

        private float xCord = -1;
        private float yCord = -1;

        private float routeLength;
        private float routeAngle;

        public RouteDisplay(Route route, DestDisplay start, DestDisplay end) {
            this.route = route;
            this.start = start;
            this.end = end;

            routeLength = (float) Math.sqrt(
                    Math.pow(end.getXCord() - start.getXCord(), 2) +
                    Math.pow(end.getYCord() - start.getYCord(), 2))
                    - DestDisplay.RADIUS;

            float trackSize = (routeLength - (route.getLength() - 1) * TRACK_LONG_SPACE)
                    / route.getLength();

            float routeHeight = (TOTAL_ROUTE_HEIGHT - (route.getTotalTracks() - 1) * TRACK_WIDE_SPACE) *1.0f
                    / route.getTotalTracks();

            routeAngle = RAD_TO_DEG * (float) Math.atan2(
                    end.getYCord() - start.getYCord(),
                    end.getXCord() - start.getXCord());

            if (routeAngle < 0) {
                routeAngle += 360;
            }

            int colorNum = 0;

            for (TrainColor color : route.getColorsAllowed()) {
                List<Sprite> trackList = new ArrayList<>();

                float startOffsetAlong = DestDisplay.RADIUS / 2;

                for (int ndx = 0; ndx < route.getLength(); ++ndx) {
                    Sprite trackSprite = color.getTrackSprite();

                    float offsetAlong = ndx * (trackSize + TRACK_LONG_SPACE) + startOffsetAlong;
                    float offsetAgainst =  colorNum * (routeHeight + TRACK_WIDE_SPACE);

                    if (xCord == -1) {
                        xCord = start.getXCord() +
                                (float) Math.cos(routeAngle / RAD_TO_DEG) * offsetAlong -
                                (float) Math.sin(routeAngle / RAD_TO_DEG) * offsetAgainst;
                    }

                    if (yCord == -1) {
                        yCord = start.getYCord() +
                                (float) Math.sin(routeAngle / RAD_TO_DEG) * offsetAlong +
                                (float) Math.cos(routeAngle / RAD_TO_DEG) * offsetAgainst;
                    }

                    trackSprite.setBounds(start.getXCord() +
                                    (float) Math.cos(routeAngle / RAD_TO_DEG) * offsetAlong -
                                    (float) Math.sin(routeAngle / RAD_TO_DEG) * offsetAgainst,
                            start.getYCord() +
                                    (float) Math.sin(routeAngle / RAD_TO_DEG) * offsetAlong +
                                    (float) Math.cos(routeAngle / RAD_TO_DEG) * offsetAgainst,
                            trackSize, routeHeight);

                    trackSprite.setOrigin(0, 0);
                    trackSprite.rotate(routeAngle);

                    trackList.add(trackSprite);
                }

                trackSprites.add(new TracksPair(color, trackList,
                        trackList.get(0).getX(), trackList.get(0).getY(),
                        routeLength, routeHeight, routeAngle));
                ++colorNum;
            }

        }

        public DestDisplay getStart() {
            return start;
        }

        public DestDisplay getEnd() {
            return end;
        }

        public void update(float delta) {
            if (route.getTracksFilled() > tracksFilled) {
                tracksFilled++;
                TTRPlayerColor colorFilled = null;

                for (TTRPlayerColor playerColor : route.getColorsFilled()) {
                    if (!playerColorsFilled.contains(playerColor)) {
                        colorFilled = playerColor;
                        playerColorsFilled.add(playerColor);
                    }
                }

                int colorsDeleted = 0;
                for (TracksPair pair : trackSprites) {
                    if (!pair.isFilled() && !route.getColorsAllowed().contains(pair.getColor())) {
                        colorsDeleted++;
                        pair.fill(colorFilled);
                    }
                }

                if (colorsDeleted == 0) {
                    for (TracksPair pair : trackSprites) {
                        if (!pair.isFilled()) {
                            pair.fill(colorFilled);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public int getX() {
            return (int) xCord;
        }

        @Override
        public int getY() {
            return (int) yCord;
        }

        @Override
        public int getWidth() {
            return (int) routeLength;
        }

        @Override
        public int getHeight() {
            return (int) TOTAL_ROUTE_HEIGHT;
        }

        @Override
        public float getAngle() {
            return routeAngle;
        }

        @Override
        public Object getInfo() {
            return route;
        }

        @Override
        public void draw(Batch batch) {
            drawTracks(batch);
        }

        private void drawTracks(Batch batch) {
            for (TracksPair pair : trackSprites) {
                for (Sprite trackSprite : pair.getSprites()) {
                    trackSprite.draw(batch);
                }
            }
        }

    }

    public static class TracksPair {
        public boolean filled;
        public List<Sprite> trackSprites;
        public TrainColor color;

        float x, y, width, height, angle;

        public TracksPair(TrainColor color, List<Sprite> trackSprites,
                          float x, float y, float width, float height, float angle) {
            this.color = color;
            this.trackSprites = new ArrayList<>(trackSprites);

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.angle = angle;
        }

        public void fill(TTRPlayerColor playerColor) {
            filled = true;

            Sprite filledSprite = playerColor.getFilledSprite();

            filledSprite.setBounds(x, y, width, height);
            filledSprite.setOrigin(0, 0);
            filledSprite.setRotation(angle);

            trackSprites = new ArrayList<>();
            trackSprites.add(filledSprite);
        }

        public boolean isFilled() {
            return filled;
        }

        public void setSprites(List<Sprite> sprites) {
            this.trackSprites = sprites;
        }

        public List<Sprite> getSprites() {
            return trackSprites;
        }

        public TrainColor getColor() {
            return color;
        }
    }
}
