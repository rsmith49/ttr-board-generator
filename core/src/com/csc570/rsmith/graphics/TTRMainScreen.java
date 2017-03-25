package com.csc570.rsmith.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.csc570.rsmith.boardgenerator.BoardGenerator;
import com.csc570.rsmith.boardgenerator.DefaultBoardGenerator;
import com.csc570.rsmith.boardgenerator.boardimportexport.BoardJSONImporter;
import com.csc570.rsmith.boardgenerator.boardimportexport.BoardSeedExporter;
import com.csc570.rsmith.boardgenerator.boardimportexport.BoardSeedImporter;
import com.csc570.rsmith.boardgenerator.genetic.GEBoardGenerator;
import com.csc570.rsmith.boardgenerator.genetic.GeneticAlgorithmDriver;
import com.csc570.rsmith.boardgenerator.genetic.Genome;
import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.cards.DestinationTicketCard;
import com.csc570.rsmith.mechanics.exceptions.InfiniteLoopException;
import com.csc570.rsmith.mechanics.exceptions.NoPossibleMovesException;
import com.csc570.rsmith.mechanics.gamestate.FitnessStats;
import com.csc570.rsmith.mechanics.gamestate.GameManager;
import com.csc570.rsmith.mechanics.player.TTRPlayer;
import com.csc570.rsmith.mechanics.player.TTRPlayerColor;
import com.csc570.rsmith.playerai.BasicPlayerAI;
import com.csc570.rsmith.playerai.RandomPlayerAI;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.System.exit;

/**
 * Created by rsmith on 3/2/17.
 */
public class TTRMainScreen extends ScreenAdapter {

    private SpriteBatch batch;

    private Texture bgTexture;
    private Sprite bg;
    private BitmapFont font;

    Random random = new Random();
    private GameManager manager;

    private Message banner;

    private List<ScreenSection> screenSections = new ArrayList<>();

    public TTRMainScreen(int numHumanPlayers, int numCompPlayers,
                         List<TTRPlayerColor> playerColors, boolean useGE) {

        font = new BitmapFont();
        batch = new SpriteBatch();

        bgTexture = new Texture("game_background.png");
        bg = new Sprite(bgTexture);
        bg.setBounds(0, 0, GraphicsUtils.WINDOW_WIDTH, GraphicsUtils.WINDOW_HEIGHT);

        GameBoard board = getNewBoard(useGE);

        manager = new GameManager(board, playerColors, numCompPlayers, new BasicPlayerAI(board));

        banner = new Message(font, "");
        banner.setCords(GraphicsUtils.LEFT_TAB_WIDTH + 25, GraphicsUtils.WINDOW_HEIGHT - 108);

        screenSections.add(new TrainCardHandSection(manager, font));
        screenSections.add(new TicketCardHandSection(manager, font));
        screenSections.add(new FlopSection(manager, font));
        screenSections.add(new InteractionSection(manager, font));
        screenSections.add(new MapSection(board, manager));
    }

    @Override
    public void show() {


    }

    private boolean gameEnded = false;

    @Override
    public void render(float delta) {

        manager.update();

        if (manager.isGameOver() && !gameEnded) {

            System.out.println();
            System.out.println("GAME OVER");
            for (TTRPlayer player : manager.getPlayers()) {
                System.out.println("Player: " + player.getColor());
                System.out.println("    Score: " + player.getPoints());
                System.out.println("    Trains Left: " + player.getTrainsLeft());
                for (DestinationTicketCard ticketCard : player.getTicketCards()) {
                    System.out.println("        " + ticketCard.toString());
                    System.out.println("        " + ticketCard.isCompleted());
                }
                System.out.println();
            }

            gameEnded = true;
        }

        // Need some sort of update of game manager here -- maybe have each ScreenSection take a GameManager Object?

        for (ScreenSection section : screenSections) {
            section.update(delta);
        }

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        bg.draw(batch);

        banner.setMessage(manager.getCurrentMessage());
        font.setColor(Color.WHITE);
        banner.draw(batch);

        for (ScreenSection section : screenSections) {
            section.draw(batch);
        }

        batch.end();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        for (ScreenSection section : screenSections) {
            section.dispose();
        }

        batch.dispose();
        bgTexture.dispose();
        font.dispose();

    }

    private GameBoard getNewBoard(boolean useGE) {
        GameBoard bestBoard = null;

        if (useGE) {
            // Time test
            /*for (int genSize = 100; genSize < 1000; genSize += 100) {
                GeneticAlgorithmDriver.GEN_POP = genSize;
                System.out.println();
                System.out.println("Generation Size: " + genSize);

                for (int ndx = 0; ndx < 8; ++ndx) {
                    bestBoard = runGA();
                }
            }*/

            bestBoard = runGA();
            //bestBoard = BoardSeedImporter.importBoard("boards/seed_508.0687807144917.txt");
        }
        else {
            BoardGenerator generator = new DefaultBoardGenerator();
            bestBoard = generator.createBoard();
        }

        //BoardGenerator generator = new DefaultBoardGenerator();
        //BoardGenerator generator = new GEBoardGenerator(new Genome());
        //bestBoard = generator.createBoard();

        //runDefaultBoard();

        return bestBoard;
    }

    private GameBoard runGA() {
        double avgTime = 0;
        int NUM_ITS = 1;
        GameBoard ans = null;

        for (int ndx = 0; ndx < NUM_ITS; ++ndx) {

            long start = System.currentTimeMillis();
            GeneticAlgorithmDriver driver = new GeneticAlgorithmDriver();
            List<GameBoard> bestBoards = driver.generateBoards(1);

            double timeTaken = (System.currentTimeMillis() - start) / 1000.0;
            avgTime += timeTaken;

            System.out.println("Took: " + timeTaken + " seconds");

            //for (GameBoard board : bestBoards) {
            //    BoardSeedExporter.saveNewBoard(board);
            //}
            ans = bestBoards.get(0);
        }

        avgTime /= NUM_ITS;
        //System.out.println("Average time taken: " + avgTime);

        return ans;
    }

    private void runDefaultBoard() {
        List<FitnessStats> statsList = new ArrayList<>();
        for (int ndx = 0; ndx < 10000; ++ndx) {
            try {
                BoardGenerator generator = new DefaultBoardGenerator();
                GameBoard board = generator.createBoard();

                GameManager manager = new GameManager(board);
                manager.runAutomatedGame();
                statsList.add(manager.getStats());
            }
            catch (NoPossibleMovesException | InfiniteLoopException e) {

            }
        }

        try{
            PrintWriter writer = new PrintWriter("assets/default_board_results.csv", "UTF-8");
            writer.println("avg_score,score_var," +
                    "num_dests,avg_routes," +
                    "num_turns,train_turns,build_turns,dest_turns," +
                    "path_changes," +
                    "clutter,route_ints,city_cov");

            double avgScore = 0;
            double scoreVar = 0;
            double numDests = 0;
            double numRoutes = 0;
            double numTurns = 0;
            double trainTurns = 0;
            double buildTurns = 0;
            double destTurns = 0;
            double pathChanges = 0;
            double clutter = 0;
            double routeInts = 0;
            double citiesCovered = 0;

            for (FitnessStats stats : statsList) {

                String line = "" + stats.getAvgScore();
                avgScore += stats.getAvgScore();
                line += "," + stats.getScoreVariance();
                scoreVar += stats.getScoreVariance();

                line += "," + stats.getNumDests();
                numDests += stats.getNumDests();
                line += "," + stats.getRoutesPerDest();
                numRoutes += stats.getRoutesPerDest();

                line += "," + stats.getNumTurns();
                numTurns += stats.getNumTurns();
                line += "," + stats.getAvgTrainCardsTurns();
                trainTurns += stats.getAvgTrainCardsTurns();
                line += "," + stats.getAvgBuildTurns();
                buildTurns += stats.getAvgBuildTurns();
                line += "," + stats.getAvgDestCardsTurns();
                destTurns += stats.getAvgDestCardsTurns();

                line += "," + stats.getAvgPathChanges();
                pathChanges += stats.getAvgPathChanges();

                line += "," + stats.getClutter();
                clutter += stats.getClutter();
                line += "," + stats.getRouteOverRoutes();
                routeInts += stats.getRouteOverRoutes();
                line += "," + stats.getCitiesCovered();
                citiesCovered += stats.getCitiesCovered();

                writer.println(line);
            }

            System.out.println("Avg Avg Score: " + avgScore / statsList.size());
            System.out.println("Avg Score Var: " + scoreVar / statsList.size());

            System.out.println("Avg Num Dests: " + numDests / statsList.size());
            System.out.println("Avg Avg Routes: " + numRoutes / statsList.size());

            System.out.println("Avg Num Turns: " + numTurns / statsList.size());
            System.out.println("Avg Train Turns: " + trainTurns / statsList.size());
            System.out.println("Avg Build Turns: " + buildTurns / statsList.size());
            System.out.println("Avg Dest Turns: " + destTurns / statsList.size());

            System.out.println("Avg Path Changes: " + pathChanges / statsList.size());

            System.out.println("Avg Clutter: " + clutter / statsList.size());
            System.out.println("Route Intersections: " + routeInts / statsList.size());
            System.out.println("Cities Covered: " + citiesCovered / statsList.size());

            writer.close();

        } catch (IOException e) {
            System.err.println("Something wrong with file storing");
        }
    }
}
