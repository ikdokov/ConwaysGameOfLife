package com.gameoflife.ui;

import com.gameoflife.core.GameLogic;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GameLogicUnitTest {

    @Test
    public void glider_spaceship_isGliding() {
        GameLogic gameLogic = new GameLogic(9, 9);
        gameLogic.resurrectCell(0, 0);
        gameLogic.resurrectCell(1, 1);
        gameLogic.resurrectCell(1, 2);
        gameLogic.resurrectCell(2, 0);
        gameLogic.resurrectCell(2, 1);

        gameLogic.evolve();
        gameLogic.evolve();
        gameLogic.evolve();

        boolean [][] expectedPosition = new boolean [9][9];

        expectedPosition[2][0] = true;
        expectedPosition[1][2] = true;
        expectedPosition[2][2] = true;
        expectedPosition[3][1] = true;
        expectedPosition[3][2] = true;

        assertTrue(Arrays.deepEquals(gameLogic.getCurrentGeneration(), expectedPosition));
    }

    @Test
    public void blinker_oscillators_repeat() throws Exception {
        GameLogic gameLogic = new GameLogic(9, 9);
        gameLogic.resurrectCell(2, 1);
        gameLogic.resurrectCell(2, 2);
        gameLogic.resurrectCell(2, 3);

        boolean [][] firstGeneration  = deepCopyBoolMatrix(gameLogic.getCurrentGeneration());
        gameLogic.evolve();
        boolean [][] secondGeneration = deepCopyBoolMatrix(gameLogic.getCurrentGeneration());
        gameLogic.evolve();
        boolean [][] thirdGeneration = deepCopyBoolMatrix(gameLogic.getCurrentGeneration());
        gameLogic.evolve();
        boolean [][] forthGeneration = deepCopyBoolMatrix(gameLogic.getCurrentGeneration());

        assertTrue(Arrays.deepEquals(firstGeneration, thirdGeneration));
        assertTrue(Arrays.deepEquals(secondGeneration, forthGeneration));
        assertFalse(Arrays.deepEquals(firstGeneration, secondGeneration));
    }

    @Test
    public void beehive_stillLifes_notEvolve() throws Exception {
        GameLogic gameLogic = new GameLogic(9, 9);
        gameLogic.resurrectCell(1, 2);
        gameLogic.resurrectCell(1, 3);
        gameLogic.resurrectCell(2, 1);
        gameLogic.resurrectCell(2, 4);
        gameLogic.resurrectCell(3, 2);
        gameLogic.resurrectCell(3, 3);

        boolean [][] beforeEvolve = deepCopyBoolMatrix(gameLogic.getCurrentGeneration());
        gameLogic.evolve();
        boolean [][] afterEvolve = gameLogic.getCurrentGeneration();
        assertTrue(Arrays.deepEquals(beforeEvolve, afterEvolve));
    }

    @Test
    public void block_stillLifes_notEvolve() throws Exception {
        GameLogic gameLogic = new GameLogic(9, 9);
        gameLogic.resurrectCell(1, 1);
        gameLogic.resurrectCell(1, 2);
        gameLogic.resurrectCell(2, 1);
        gameLogic.resurrectCell(2, 2);

        boolean [][] beforeEvolve = deepCopyBoolMatrix(gameLogic.getCurrentGeneration());
        gameLogic.evolve();
        boolean [][] afterEvolve = gameLogic.getCurrentGeneration();
        assertTrue(Arrays.deepEquals(beforeEvolve, afterEvolve));
    }

    @Test
    public void randomize_worksFine() throws Exception {
        GameLogic gameLogic = new GameLogic(10, 10);
        gameLogic.randomize();
        boolean previousGrid[][] = deepCopyBoolMatrix(gameLogic.getCurrentGeneration());
        gameLogic.randomize();
        boolean currentGrid[][] = gameLogic.getCurrentGeneration();
        assertFalse(Arrays.equals(currentGrid, previousGrid));
    }

    @Test
    public void clear_worksFine() throws Exception {
        GameLogic gameLogic = new GameLogic(10, 10);
        gameLogic.randomize();
        gameLogic.clearGrid();
        assertThat(Arrays.asList(gameLogic.getCurrentGeneration()).contains(true), is(false));
    }

    public static boolean[][] deepCopyBoolMatrix(boolean[][] input) {
        if (input == null)
            return null;
        boolean[][] result = new boolean[input.length][];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i].clone();
        }
        return result;
    }

}
