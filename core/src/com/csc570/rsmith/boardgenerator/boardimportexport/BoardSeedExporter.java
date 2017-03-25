package com.csc570.rsmith.boardgenerator.boardimportexport;

import com.csc570.rsmith.mechanics.board.GameBoard;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by rsmith on 3/22/17.
 */
public class BoardSeedExporter {

    public static void saveNewBoard(GameBoard board) {
        String fileName = "assets/boards/seed_" + board.getFitness() + ".txt";
        int[] seed = board.getSeed();

        try{
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            for (int ndx = 0; ndx < seed.length; ++ndx) {
                writer.println("" + seed[ndx]);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Something wrong with file storing");
        }

    }
}
