package com.gameoflife.core;

import java.util.Random;

public class GameLogic {

    private int rows;
    private int columns;

    private boolean currentGenerationGrid[][];
    private boolean nextGenerationGrid[][];

    public GameLogic(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.currentGenerationGrid = new boolean[rows][columns];
        this.nextGenerationGrid = new boolean[rows][rows];
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                stringBuffer.append(nextGenerationGrid[i][j] + " ");
            }

            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    public void evolve() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int aliveNeighbourCells = countAliveNeighbourCells(i, j);
                nextGenerationGrid[i][j] = decideLiveOrDie(aliveNeighbourCells, currentGenerationGrid[i][j]);
            }
        }
        currentGenerationGrid = nextGenerationGrid;
        nextGenerationGrid = new boolean[rows][columns];
    }

    private boolean decideLiveOrDie(int aliveNeighboursCount, boolean currentState) {
        if (aliveNeighboursCount < 2) {
            // Any live cell with fewer than two live neighbours dies, as if caused by under-population.
            return false;
        } else if (aliveNeighboursCount > 3) {
            // Any live cell with more than three live neighbours dies, as if by over-population.
            return false;
        } else if (aliveNeighboursCount == 3) {
            // Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
            return true;
        } else {
            // Any live cell with two or three live neighbours lives on to the next generation.
            return currentState;
        }
    }

    private int countAliveNeighbourCells(int i, int j) {
        int aliveNeighboursCount = 0;

        //left
        if (isInGrid(i, j-1) && currentGenerationGrid[i][j-1]) aliveNeighboursCount++;

        //left-up
        if (isInGrid(i-1, j-1) && currentGenerationGrid[i-1][j-1]) aliveNeighboursCount++;

        //left-down
        if (isInGrid(i+1, j-1) && currentGenerationGrid[i+1][j-1]) aliveNeighboursCount++;

        //right
        if (isInGrid(i, j+1) && currentGenerationGrid[i][j+1]) aliveNeighboursCount++;

        //right-up
        if (isInGrid(i-1, j+1) && currentGenerationGrid[i-1][j+1]) aliveNeighboursCount++;

        //right-down
        if (isInGrid(i+1, j+1) && currentGenerationGrid[i+1][j+1]) aliveNeighboursCount++;

        //up
        if (isInGrid(i-1, j) && currentGenerationGrid[i-1][j]) aliveNeighboursCount++;

        //down
        if (isInGrid(i+1, j) && currentGenerationGrid[i+1][j]) aliveNeighboursCount++;

        return aliveNeighboursCount;
    }

    public boolean[][] getCurrentGeneration() {
        return currentGenerationGrid;
    }

    public void resurrectCell(int row, int column) {
        if (isInGrid(row, column)) {
            currentGenerationGrid[row][column] = true;
        }
    }

    private boolean isInGrid(int row, int column) {
        if (row >= 0 && row < rows && column >= 0 && column < columns) {
            return true;
        }
        return false;
    }

    public void randomize() {
        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                currentGenerationGrid[i][j] = random.nextBoolean();
            }
        }
    }

    public void clearGrid() {
        currentGenerationGrid = new boolean[rows][columns];
        nextGenerationGrid = new boolean[rows][columns];
    }
}