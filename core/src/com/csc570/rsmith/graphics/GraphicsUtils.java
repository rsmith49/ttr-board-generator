package com.csc570.rsmith.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsmith on 3/1/17.
 */
public class GraphicsUtils {

    // Constants

    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 900;

    public static final int BOTTOM_TAB_HEIGHT = 255;
    public static final int BOTTOM_TAB_WIDTH = 1200;
    public static final Color BOTTOM_TAB_COLOR = Color.DARK_GRAY;

    public static final int LEFT_TAB_HEIGHT = 590;
    public static final int LEFT_TAB_WIDTH = 250;
    public static final Color LEFT_TAB_COLOR = Color.BLUE;

    public static final int RIGHT_TAB_HEIGHT = 590;
    public static final int RIGHT_TAB_WIDTH = 190;
    public static final Color RIGHT_TAB_COLOR = Color.FOREST;

    public static final int TRAIN_CARD_WIDTH = 90;
    public static final int TRAIN_CARD_HEIGHT = 50;
    public static final int TRAIN_X_OFFSET = 20;
    public static final int TRAIN_X_INC = 110;
    public static final int TRAIN_Y_OFFSET = 45;
    public static final int TRAIN_Y_INC = 100;
    public static final int TRAINCARD_NUM_Y_OFFSET = 20;

    public static final int TRAIN_COUNT_OFFSET = 50;

    public static final int DEST_WIDTH = 60;
    public static final int DEST_HEIGHT = 25;

    public static final int MAP_TAB_WIDTH = 760;
    public static final int MAP_TAB_HEIGHT = 490;

    private static List<Disposable> disps = new ArrayList<>();

    private static Texture normalTrainCardImage = null;
    private static Texture locomotiveTrainCardImage = null;

    public static List<TextureRegion> getTextureRegions(String fileName, int cols, int rows) {
        int count = 0;
        Texture text;

        if (normalTrainCardImage == null) {
            text = new Texture(Gdx.files.internal(fileName));
            normalTrainCardImage = text;
            disps.add(text);
        }
        else {
            text = normalTrainCardImage;
        }

        TextureRegion[][] tmp = TextureRegion.split(text, text.getWidth()/cols, text.getHeight()/rows);
        List<TextureRegion> frames = new ArrayList<>(cols * rows);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                frames.add(tmp[i][j]);
            }
        }

        return frames;
    }

    public static List<Sprite> getTrainCardSprites() {

        List<Sprite> ans = new ArrayList<>();
        Texture lastTrain;

        int ndx = 0, x = TRAIN_X_OFFSET;
        for (TextureRegion region : getTextureRegions("ttr_train_cards.jpg", 2, 4)) {
            Sprite sprite = new Sprite(region);
            if (ndx++ % 2 == 0) {
                sprite.setBounds(x, TRAIN_Y_OFFSET + TRAIN_Y_INC,
                        TRAIN_CARD_WIDTH, TRAIN_CARD_HEIGHT);
            }
            else {
                sprite.setBounds(x, TRAIN_Y_OFFSET,
                        TRAIN_CARD_WIDTH, TRAIN_CARD_HEIGHT);
                x += TRAIN_X_INC;
            }
            ans.add(sprite);
        }

        if (locomotiveTrainCardImage == null) {
            lastTrain = new Texture(Gdx.files.internal("ttr_locomotive.jpg"));
            locomotiveTrainCardImage = lastTrain;
            disps.add(lastTrain);
        }
        else {
            lastTrain = locomotiveTrainCardImage;
        }

        Sprite lastTrainSprite = new Sprite(lastTrain);
        lastTrainSprite.setBounds(x, TRAIN_Y_OFFSET + TRAIN_Y_INC,
                TRAIN_CARD_WIDTH, TRAIN_CARD_HEIGHT);
        ans.add(lastTrainSprite);

        return ans;
    }

    public static Sprite getYellowBlockSprite() {
        Texture text = new Texture(Gdx.files.internal("select_rect.png"));
        disps.add(text);

        return new Sprite(text);
    }

    private static Map<String, Texture> filledImageMap = new HashMap<>();

    public static Sprite getFilledSprite(String filePath) {
        if (filledImageMap.get(filePath) == null) {
            Texture filledText = new Texture(Gdx.files.internal(filePath));
            disps.add(filledText);
            filledImageMap.put(filePath, filledText);
        }

        return new Sprite(filledImageMap.get(filePath));
    }

    private static Map<String, Texture> trackImageMap = new HashMap<>();

    public static Sprite getTrackSprite(String filePath) {
        if (trackImageMap.get(filePath) == null) {
            Texture trackText = new Texture(Gdx.files.internal(filePath));
            disps.add(trackText);
            trackImageMap.put(filePath, trackText);
        }

        return new Sprite(trackImageMap.get(filePath));
    }

    private static Texture destText = null;

    public static Sprite getDestSprite() {
        if (destText == null) {
            destText = new Texture(Gdx.files.internal("map_images/DestinationImage.png"));
            disps.add(destText);
        }

        return new Sprite(destText);
    }


    public static void disposeAll() {
        for (Disposable disp : disps) {
            disp.dispose();
        }
    }
}
