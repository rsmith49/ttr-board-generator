package com.csc570.rsmith.boardgenerator;

import com.csc570.rsmith.boardgenerator.boardimportexport.BoardJSONImporter;
import com.csc570.rsmith.mechanics.board.GameBoard;

/**
 * Created by rsmith on 2/25/17.
 */
public class DefaultBoardGenerator implements BoardGenerator {

    private static final String DEFAULT_BOARD_FILEPATH = "approved_boards/DefaultBoard.json";

    @Override
    public GameBoard createBoard() {

        return BoardJSONImporter.importBoard(DEFAULT_BOARD_FILEPATH);
    }
}
