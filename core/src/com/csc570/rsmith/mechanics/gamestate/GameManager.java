package com.csc570.rsmith.mechanics.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.csc570.rsmith.graphics.SelectBar;
import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.mechanics.board.Route;
import com.csc570.rsmith.mechanics.board.TrainColor;
import com.csc570.rsmith.mechanics.cards.*;
import com.csc570.rsmith.mechanics.exceptions.IncompatibleCardsException;
import com.csc570.rsmith.mechanics.exceptions.IncompatibleVarsException;
import com.csc570.rsmith.mechanics.exceptions.OutOfCardsException;
import com.csc570.rsmith.mechanics.player.TTRComputerPlayer;
import com.csc570.rsmith.mechanics.player.TTRHumanPlayer;
import com.csc570.rsmith.mechanics.player.TTRPlayer;
import com.csc570.rsmith.mechanics.player.TTRPlayerColor;
import com.csc570.rsmith.playerai.BasicPlayerAI;
import com.csc570.rsmith.playerai.RandomPlayerAI;
import com.csc570.rsmith.playerai.TTRPlayerAI;

import java.util.*;

/**
 * Created by rsmith on 2/25/17.
 */
public class GameManager {

    public static final int DEFAULT_NUM_PLAYERS = 5;
    public static final int NUM_START_TRAIN_CARDS = 7;
    public static final int NUM_START_DEST_CARDS = 3;
    public static final int NUM_DEST_TO_KEEP_START = 2;
    public static final int NUM_DEST_CARDS = 3;
    public static final int NUM_DEST_TO_KEEP = 1;

    private List<TTRPlayer> players;
    private GameBoard board;
    private DestinationTicketCardManager ticketCardManager;
    private TrainCardManager trainCardManager;
    private int turnNdx = 0;

    public GameManager(GameBoard board, List<TTRPlayerColor> humanPlayerColors,
                       List<TTRPlayerAI> playerAIs) {
        this.board = board;

        Set<TTRPlayerColor> allColors = new HashSet<>();
        Collections.addAll(allColors, TTRPlayerColor.values());

        players = new ArrayList<>(humanPlayerColors.size() + playerAIs.size());
        for (TTRPlayerColor color : humanPlayerColors) {
            players.add(new TTRHumanPlayer(color));
            allColors.remove(color);
        }

        int ndx = 0;
        for (TTRPlayerColor color : allColors) {
            if (ndx < playerAIs.size()) {
                players.add(new TTRComputerPlayer(color, playerAIs.get(ndx++), this));
            }
        }

        if (humanPlayerColors.size() > 0) {
            playerInit();
        }
        else {
            compInit();
        }
    }

    public GameManager(GameBoard board, List<TTRPlayerColor> humanPlayerColors,
                       int numOpponents, TTRPlayerAI playerAI) {
        this(board, humanPlayerColors,
                Collections.nCopies(numOpponents, playerAI));
    }

    public GameManager(GameBoard board, int numPlayers) {
        this.board = board;
        players = new ArrayList<>(numPlayers);
        for (int ndx = 0; ndx < numPlayers; ++ndx) {
            // Will have to be modified in long term
            // TODO: Address this
            players.add(new TTRComputerPlayer(TTRPlayerColor.values()[ndx], new BasicPlayerAI(board), this));
        }

        compInit();
    }

    public GameManager(GameBoard board) {
        this(board, DEFAULT_NUM_PLAYERS);
    }

    /**
     * Method to be called in every constructor, designed to set up the game.
     */
    private void init() {
        this.ticketCardManager = new DestinationTicketCardManager(board.getTicketCards());
        this.trainCardManager = new TrainCardManager();

    }

    private void playerInit() {
        init();

        for (TTRPlayer player : players) {
            player.addTrainCards(trainCardManager.draw(NUM_START_TRAIN_CARDS));
        }

        currPlayer = nextPlayer();
        turnsLeft = players.size() - 1;

        currTurnType = null;
        currTurnState = TurnState.TURN_START;

        selectBar = new SelectBar();
    }

    private void compInit() {
        init();

        for (TTRPlayer player : players) {
            player.addTrainCards(trainCardManager.draw(NUM_START_TRAIN_CARDS));
            ticketCardManager.discardAll(player.addTicketCards(
                    ticketCardManager.draw(NUM_START_DEST_CARDS),
                    NUM_DEST_TO_KEEP_START));
        }
    }

    // BOTH AUTOMATED AND HUMAN

    private boolean enoughTrains() {
        for (TTRPlayer player : players) {
            if (player.getTrainsLeft() <= 2) {
                lastTurn = true;
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the next player in the progression of turns
     * @return The player object for the next player whose turn it is
     */
    private TTRPlayer nextPlayer() {
        return players.get(turnNdx++ % players.size());
    }

    // AUTOMATED GAMES

    /**
     * Only called if there are no Human Players
     */
    public void runAutomatedGame() {

        while (enoughTrains()) {
            TTRPlayer player = nextPlayer();
            TurnType turnType = player.selectTurnType();
            stats.takeTurn(turnType);
            conductAutomatedTurn(turnType, player);
        }

        for (int endNdx = 0; endNdx < players.size() - 1; ++endNdx) {
            TTRPlayer player = nextPlayer();
            TurnType turnType = player.selectTurnType();
            stats.takeTurn(turnType);
            conductAutomatedTurn(turnType, player);
        }

        stats.endGameStats(this);
    }

    private void conductAutomatedTurn(TurnType turnType, TTRPlayer player) {

        switch (turnType) {
            case BUILD:
                Route selectedRoute = player.selectRoute(board);
                TrainColor selectedColor = player.selectTrackColor(board, selectedRoute);
                Collection<TrainCard> cardsPlayed = player.playTrainCards(board, selectedRoute, selectedColor);

                trainCardManager.discardAll(cardsPlayed);

                break;
            case GET_TRAIN_CARDS:
                int numToDraw = 2;
                while (numToDraw > 0) {
                    int flopNdx = player.selectTrainCard(
                            trainCardManager.viewFlop());

                    if (flopNdx >= 0 && trainCardManager.viewFlop().get(flopNdx)
                            .getColor().equals(TrainColor.ANY)) {
                        --numToDraw;
                    }

                    player.addTrainCard(trainCardManager.takeFromFlop(flopNdx));
                    --numToDraw;
                }

                break;
            case GET_DEST_CARDS:
                ticketCardManager.discardAll(
                        player.addTicketCards(
                                ticketCardManager.draw(NUM_DEST_CARDS),
                                NUM_DEST_TO_KEEP));
                break;
        }
    }

    // HUMAN INTERACTION GAMES

    private TurnType currTurnType;
    private TTRPlayer currPlayer;
    private TurnState currTurnState;

    private SelectBar selectBar;

    private boolean lastTurn = false;
    private int turnsLeft;
    private boolean gameOver = false;

    private int trainCardsLeftToPick;

    private Collection<DestinationTicketCard> ticketCardsOffered;
    private Collection<DestinationTicketCard> ticketCardsKept;

    public Collection<DestinationTicketCard> getTicketCardsOffered() {
        return ticketCardsOffered;
    }

    private Route routeSelected;
    private TrainColor colorSelected;
    private List<TrainCard> cardsSelected;

    public Route getRouteSelected() {
        return routeSelected;
    }

    public TrainColor getColorSelected() {
        return colorSelected;
    }

    private String currentMessage = "";
    private int numDestToKeep;

    private boolean firstEntering = false;

    public int getNumDestToKeep() {
        return numDestToKeep;
    }

    public void update() {

        if (lastTurn) {
            --turnsLeft;
        }

        // TODO: Longest route


        if (currPlayer instanceof TTRHumanPlayer) {

            /*if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                currPlayer.addTrainCard(trainCardManager.draw());
                currPlayer = nextPlayer();
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                ticketCardManager.discardAll(
                        currPlayer.addTicketCards(
                                ticketCardManager.draw(NUM_START_DEST_CARDS),
                                NUM_DEST_TO_KEEP));
                currPlayer = nextPlayer();
            } */

            if (!isGameOver()) {

                if (currTurnState == TurnState.TURN_START) {

                    firstEntering = true;

                    setMessage("Press Enter to begin turn");
                    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                        if (turnNdx <= players.size()) {
                            currTurnState = TurnState.INIT_TURN;
                            currTurnType = TurnType.GET_DEST_CARDS;
                        }
                        else {
                            currTurnState = TurnState.SELECT_TURN;
                        }
                    }

                }
                else if (currTurnState == TurnState.SELECT_TURN) {
                    if (firstEntering) {
                        setMessage("Select Turn Type");
                        firstEntering = false;
                    }

                    currTurnState = TurnState.SELECT_TURN;

                    if (selectBar.hasResponse()) {
                        currTurnType = (TurnType) selectBar.getResponse();
                        currTurnState = TurnState.INIT_TURN;
                    }
                }
                else {

                    switch (currTurnType) {
                        case BUILD:

                            switch (currTurnState) {

                                case INIT_TURN:
                                    routeSelected = null;
                                    colorSelected = null;
                                    cardsSelected = new ArrayList<>();

                                    currTurnState = TurnState.SELECT_ROUTE;
                                    break;

                                case SELECT_ROUTE:

                                    if (selectBar.hasResponse()) {
                                        routeSelected = (Route) selectBar.getResponse();
                                        currTurnState = TurnState.SELECT_COLOR;
                                    }

                                    break;
                                case SELECT_COLOR:

                                    if (selectBar.hasResponse()) {
                                        colorSelected = (TrainColor) selectBar.getResponse();
                                        currTurnState = TurnState.SELECT_TRAIN_CARDS;
                                    }

                                    break;
                                case SELECT_TRAIN_CARDS:

                                    if (selectBar.hasResponse()) {
                                        TrainCard cardSelected = new TrainCard((TrainCardType) selectBar.getResponse());
                                        cardsSelected.add(cardSelected);
                                        currPlayer.removeTrainCard(cardSelected);

                                        if (cardsSelected.size() == routeSelected.getLength()
                                                || !currPlayer.hasCards()) {
                                            try {
                                                currPlayer.addTrainCards(cardsSelected);

                                                ((TTRHumanPlayer) currPlayer).setSelectedRoute(routeSelected);
                                                ((TTRHumanPlayer) currPlayer).setCardsChosen(cardsSelected);
                                                ((TTRHumanPlayer) currPlayer).setColorPicked(colorSelected);

                                                currPlayer.playTrainCards(board, routeSelected, colorSelected);

                                                trainCardManager.discardAll(cardsSelected);

                                                currTurnState = TurnState.TURN_START;
                                                currPlayer = nextPlayer();
                                            }
                                            catch (IncompatibleCardsException | IncompatibleVarsException e) {
                                                setMessage("Invalid selection, conduct your turn again");
                                                currTurnState = TurnState.SELECT_TURN;
                                            }

                                        }
                                        // Logic stuff to see if length is satisfied
                                    }

                                    break;
                            }

                            break;
                        case GET_DEST_CARDS:

                            switch (currTurnState) {

                                case INIT_TURN:
                                    try {
                                        ticketCardsOffered = ticketCardManager.draw(NUM_DEST_CARDS);
                                        ticketCardsKept = new ArrayList<>();

                                        currTurnState = TurnState.SELECT_TICKET_CARD;

                                        numDestToKeep = turnNdx > players.size() ?
                                                NUM_DEST_TO_KEEP : NUM_DEST_TO_KEEP_START;

                                    } catch (OutOfCardsException e) {
                                        setMessage("Out of cards, select a different turn type");
                                        currTurnState = TurnState.SELECT_TURN;
                                    }

                                    break;

                                case SELECT_TICKET_CARD:

                                    if (selectBar.hasResponse()) {
                                        DestinationTicketCard ticketCard =
                                                (DestinationTicketCard) selectBar.pollResponse();
                                        if (ticketCard == null) {
                                            if (ticketCardsKept.size() < numDestToKeep) {
                                                setMessage("Invalid Selection");
                                                selectBar.removeResponse();
                                                return;
                                            }
                                            else {
                                                selectBar.getResponse();

                                                ((TTRHumanPlayer) currPlayer).setTicketCardsToKeep(ticketCardsKept);
                                                ticketCardManager.discardAll(
                                                        currPlayer.addTicketCards(ticketCardsOffered, numDestToKeep));

                                                currPlayer = nextPlayer();


                                                currTurnState = TurnState.TURN_START;
                                                currTurnType = null;

                                            }
                                        }
                                        else {
                                            ticketCard = (DestinationTicketCard) selectBar.getResponse();

                                            ticketCardsKept.add(ticketCard);

                                        }
                                    }
                                    break;
                            }

                            break;
                        case GET_TRAIN_CARDS:

                            switch (currTurnState) {

                                case INIT_TURN:

                                    if (trainCardManager.getDeckSize() <= 1) {
                                        setMessage("Out of cards, select a different turn type");
                                        currTurnState = TurnState.SELECT_TURN;
                                    }
                                    else {
                                        trainCardsLeftToPick = 2;
                                        currTurnState = TurnState.SELECT_TRAIN_CARD;
                                    }
                                    break;

                                case SELECT_TRAIN_CARD:

                                    if (selectBar.hasResponse()) {

                                        int index = (Integer) selectBar.getResponse();
                                        if (index == trainCardManager.viewFlop().size()) {

                                            currPlayer.addTrainCard(trainCardManager.draw());
                                            trainCardsLeftToPick--;

                                        }
                                        else {
                                            if (trainCardManager.viewFlop().get(index).getColor() == TrainColor.ANY) {
                                                if (trainCardsLeftToPick == 1) {
                                                    setMessage("Invalid Selection");
                                                    return;
                                                }
                                                else {
                                                    trainCardsLeftToPick--;
                                                }
                                            }
                                            trainCardsLeftToPick--;

                                            currPlayer.addTrainCard(trainCardManager.takeFromFlop(index));
                                        }

                                        if (trainCardsLeftToPick == 0) {
                                            currTurnType = null;
                                            currTurnState = TurnState.TURN_START;

                                            currPlayer = nextPlayer();
                                        }
                                    }

                                    break;
                            }

                            break;
                    }
                }

            }
        }

        if (currPlayer instanceof TTRComputerPlayer && enoughTrains() && !isGameOver()) {
            // This automates all of the computer turns

            while (currPlayer instanceof TTRComputerPlayer) {
                currTurnType = currPlayer.selectTurnType();
                conductAutomatedTurn(currTurnType, currPlayer);
                currPlayer = nextPlayer();
            }
        }

        if (isGameOver()) {
            int maxPoints = players.get(0).getPoints();
            winner = players.get(0);
            for (TTRPlayer player : players) {
                if (player.getPoints() > maxPoints) {
                    maxPoints = player.getPoints();
                    winner = player;
                }
            }
        }

    }

    private TTRPlayer winner;

    public TTRPlayer getWinner() {
        return winner;
    }

    // Getters for information purposes

    public boolean isGameOver() {
        if (!lastTurn) {
            return false;
        }
        if (gameOver) {
            return true;
        }

        gameOver = turnsLeft == 0;
        return gameOver;
    }

    public TTRPlayer getCurrentPlayer() { return currPlayer; }

    public TrainCardManager getTrainCardManager() {
        return trainCardManager;
    }

    public DestinationTicketCardManager getTicketCardManager() {
        return ticketCardManager;
    }

    public TurnState getState() {
        return currTurnState;
    }

    public TurnType getTurnType() {
        return currTurnType;
    }

    public SelectBar selector() {
        return selectBar;
    }

    public void setMessage(String message) {
        currentMessage = "Player " + currPlayer.getColor().name() + ": " + message;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public int getTurnNdx() {
        return turnNdx;
    }

    public List<TTRPlayer> getPlayers() {
        return players;
    }

    public boolean isLastTurn() {
        return lastTurn;
    }

    public GameBoard getBoard() {
        return board;
    }

    // Fitness Stats stuff
    private FitnessStats stats = new FitnessStats();

    public FitnessStats getStats() {
        return stats;
    }
}
