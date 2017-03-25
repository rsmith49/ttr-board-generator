package com.csc570.rsmith.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.csc570.rsmith.mechanics.player.TTRHumanPlayer;
import com.csc570.rsmith.mechanics.player.TTRPlayerColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsmith on 3/23/17.
 */
public class TTRStartScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private BitmapFont font;

    private boolean doneChoosing = false;

    private List<Drawable> toDraw = new ArrayList<>();

    private List<MessageOption> humanPlayerNum = new ArrayList<>();
    private List<MessageOption> computerPlayerNum = new ArrayList<>();
    private List<MessageOption> colorSelection = new ArrayList<>();
    private List<MessageOption> boardGen = new ArrayList<>();

    private int numHumanPlayers = 0;
    private int numCompPlayers = 0;
    private List<TTRPlayerColor> colorsSelected = new ArrayList<>();
    private boolean useGE = false;

    private int colorChoiceNdx = 1;
    private List<TTRPlayerColor> colorsAvailable = new ArrayList<>();

    private SelectState state = SelectState.NUM_HUMAN_PLAYERS;
    private Message instructions;

    private SelectBar selectBar;

    public TTRStartScreen() {
        font = new BitmapFont();
        batch = new SpriteBatch();

        for (TTRPlayerColor color : TTRPlayerColor.values()) {
            colorsAvailable.add(color);
        }

        selectBar = new SelectBar();

        instructions = new Message(font, "hello");
        instructions.setCords(GraphicsUtils.WINDOW_WIDTH / 2 - 100, 700);

        initHumanPlayerNum();
    }

    @Override
    public void render(float delta) {

        if (!doneChoosing) {
            selectBar.update(delta);

            switch (state) {
                case NUM_HUMAN_PLAYERS:
                    if (selectBar.hasResponse()) {
                        numHumanPlayers = Integer.parseInt((String) selectBar.getResponse());

                        if (numHumanPlayers < 5) {
                            initComputerPlayerNum();
                            state = SelectState.NUM_COMP_PLAYERS;
                        }
                        else {
                            initColorChoice(colorChoiceNdx);
                            state = SelectState.PLAYER_COLOR;
                        }
                    }

                    break;
                case NUM_COMP_PLAYERS:
                    if (selectBar.hasResponse()) {
                        numCompPlayers = Integer.parseInt((String) selectBar.getResponse());
                        initColorChoice(colorChoiceNdx);
                        state = SelectState.PLAYER_COLOR;
                    }

                    break;
                case PLAYER_COLOR:
                    if (selectBar.hasResponse()) {
                        ++colorChoiceNdx;
                        colorsSelected.add(TTRPlayerColor.valueOf((String) selectBar.getResponse()));
                        colorsAvailable.remove(colorsSelected.get(colorsSelected.size() - 1));

                        if (colorChoiceNdx > numHumanPlayers) {
                            initBoardGen();
                            state = SelectState.BOARD_GEN;
                        }
                        else {
                            initColorChoice(colorChoiceNdx);
                        }
                    }

                    break;
                case BOARD_GEN:
                    if (selectBar.hasResponse()) {
                        if (!"Default Board".equals(selectBar.getResponse())) {
                            useGE = true;
                        }

                        doneChoosing = true;
                    }

                    break;
            }

            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();

            font.setColor(Color.WHITE);
            instructions.draw(batch);

            for (Drawable drawable : toDraw) {
                drawable.draw(batch);
            }

            selectBar.draw(batch);

            batch.end();
        }
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public boolean isDoneChoosing() {
        return doneChoosing;
    }

    public int getNumHumanPlayers() {
        return numHumanPlayers;
    }

    public int getNumCompPlayers() {
        return numCompPlayers;
    }

    public List<TTRPlayerColor> getColorsSelected() {
        return colorsSelected;
    }

    public boolean useGE() {
        return useGE;
    }

    static class MessageOption implements Selectable, Drawable {

        Message message;
        int width;
        int height;

        public MessageOption(BitmapFont font, String message, int width, int height) {
            this.message = new Message(font, message);
            this.width = width;
            this.height = height;
        }

        public void setLocation(int x, int y) {
            message.setCords(x, y);
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
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public float getAngle() {
            return 0;
        }

        @Override
        public Object getInfo() {
            return message.getMessage();
        }

        @Override
        public void draw(Batch batch) {
            message.getFont().setColor(Color.WHITE);
            message.draw(batch);
        }
    }

    enum SelectState {
        NUM_HUMAN_PLAYERS,
        NUM_COMP_PLAYERS,
        PLAYER_COLOR,
        BOARD_GEN
    }


    private void initHumanPlayerNum() {

        for (int ndx = 1; ndx <= 5; ++ndx) {
            MessageOption option = new MessageOption(font, "" + ndx, 10, 20);
            option.setLocation(ndx * 100 + 100, 400);
            humanPlayerNum.add(option);
        }

        toDraw.clear();
        toDraw.addAll(humanPlayerNum);
        selectBar.setSelectables(humanPlayerNum, false, false, false);

        instructions.setMessage("Select number of human players");
    }

    private void initComputerPlayerNum() {
        int startNdx = numHumanPlayers > 1 ? 0 : 1;

        for (int ndx = startNdx; ndx <= 5 - numHumanPlayers; ++ndx) {
            MessageOption option = new MessageOption(font, "" + ndx, 10, 20);
            option.setLocation(ndx * 100 + 100, 400);
            computerPlayerNum.add(option);
        }

        toDraw.clear();
        toDraw.addAll(computerPlayerNum);
        selectBar.setSelectables(computerPlayerNum, false, false, false);

        instructions.setMessage("Select number of computer players");
    }

    private void initColorChoice(int selectNdx) {
        colorSelection.clear();

        int ndx = 0;
        for (TTRPlayerColor color : colorsAvailable) {
            MessageOption option = new MessageOption(font, color.name(), 100, 20);
            option.setLocation(ndx++ * 110 + 100, 400);
            colorSelection.add(option);
        }

        toDraw.clear();
        toDraw.addAll(colorSelection);
        selectBar.setSelectables(colorSelection, false, false, false);

        instructions.setMessage("Player " + selectNdx + " select color");
    }

    private void initBoardGen() {
        MessageOption option1 = new MessageOption(font, "Default Board", 100, 20);
        option1.setLocation(300, 400);
        boardGen.add(option1);

        MessageOption option2 = new MessageOption(font, "GE Board", 100, 20);
        option2.setLocation(900, 400);
        boardGen.add(option2);

        toDraw.clear();
        toDraw.addAll(boardGen);
        selectBar.setSelectables(boardGen, false, false, false);

        instructions.setMessage("Select board generation method");
    }
}
