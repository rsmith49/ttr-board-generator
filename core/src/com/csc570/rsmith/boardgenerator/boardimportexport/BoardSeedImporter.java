package com.csc570.rsmith.boardgenerator.boardimportexport;

import com.csc570.rsmith.boardgenerator.genetic.GEBoardGenerator;
import com.csc570.rsmith.boardgenerator.genetic.Genome;
import com.csc570.rsmith.mechanics.board.GameBoard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by rsmith on 3/22/17.
 */
public class BoardSeedImporter {

    public static GameBoard importBoard(String filepath) {
        try {
            Scanner fileScanner = new Scanner(new File(filepath));
            List<Integer> seedList = new ArrayList<>();

            while (fileScanner.hasNext()) {
                seedList.add(fileScanner.nextInt());
            }

            int[] seed = new int[seedList.size()];

            for (int ndx = 0; ndx < seedList.size(); ++ndx) {
                seed[ndx] = seedList.get(ndx);
            }

            Genome genome = new Genome(seed);
            GEBoardGenerator generator = new GEBoardGenerator(genome);

            return generator.createBoard();
        }
        catch (IOException e) {
            System.err.println("Something wrong opening file");
            return null;
        }
    }
}
