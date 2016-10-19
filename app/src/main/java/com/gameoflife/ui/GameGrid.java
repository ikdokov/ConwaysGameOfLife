package com.gameoflife.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gameoflife.core.GameLogic;

public class GameGrid extends SurfaceView implements SurfaceHolder.Callback {

    private int gridLength;
    private float cellSizeInPixels;

    private Paint grayPaintBrush = new Paint();
    private Paint whitePaintBrush = new Paint();

    private boolean canDraw;

    private GameLogic gameLogic;

    private Thread redrawThread;

    private Canvas canvas;
    private final SurfaceHolder surfaceHolder;

    public GameGrid(final Context context, int gridLength) {
        super(context);

        this.gridLength = gridLength;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        initBrushes();
    }

    public void initBrushes() {
        whitePaintBrush.setStyle(Paint.Style.FILL_AND_STROKE);
        whitePaintBrush.setColor(Color.WHITE);
        whitePaintBrush.setStrokeWidth(1);

        grayPaintBrush.setStyle(Paint.Style.STROKE);
        grayPaintBrush.setColor(Color.DKGRAY);
        grayPaintBrush.setStrokeWidth(1);
    }

    private class GameLoop extends Thread {
        @Override
        public void run() {

            while (canDraw) {
                try {
                    canvas = surfaceHolder.lockCanvas();

                    synchronized (surfaceHolder) {
                        doDraw(canvas);
                        gameLogic.evolve();
                    }

                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        float cellMiddle = cellSizeInPixels / 2;

        boolean [][] currentGrid = gameLogic.getCurrentGeneration();

        for (int i = 0; i < currentGrid.length; i++) {
            for (int j = 0; j < currentGrid[i].length; j++) {
                if (currentGrid[i][j]) {
                    canvas.drawCircle((j * cellSizeInPixels) + cellMiddle, (i * cellSizeInPixels) + cellMiddle, cellMiddle, whitePaintBrush);
                } else {
                    canvas.drawCircle((j * cellSizeInPixels) + cellMiddle, (i * cellSizeInPixels) + cellMiddle, cellMiddle, grayPaintBrush);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            gameLogic.resurrectCell(getRowIndex(event), getColumnIndex(event));
            return true;
        }

        return super.onTouchEvent(event);
    }

    private int getColumnIndex(MotionEvent event) {
        return (int)(event.getX() / cellSizeInPixels);
    }

    private int getRowIndex(MotionEvent event) {
        return (int)(event.getY() / cellSizeInPixels);
    }

    public void randomizeGrid(){
        clearGrid();
        gameLogic.randomize();
    }

    public void clearGrid() {
        gameLogic.clearGrid();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initGameLogic();
    }

    private void initGameLogic() {
        int width = getWidth();
        int height = getHeight();

        // find the shorter side to calculate cell size in pixels and the number of cells which should be
        // needed to fill the entire screen
        if (width > height) {
            cellSizeInPixels = calculateCellSizeInPixels(height);
            gameLogic = new GameLogic(gridLength, (int)(width / cellSizeInPixels));
        } else {
            cellSizeInPixels = calculateCellSizeInPixels(width);
            gameLogic = new GameLogic((int)(height / cellSizeInPixels), gridLength);
        }
    }

    private float calculateCellSizeInPixels(int length) {
        return length / (float) gridLength;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        startRedrawThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopRedrawThread();
    }

    private void startRedrawThread() {
        canDraw = true;
        redrawThread = new GameLoop();
        redrawThread.start();
    }

    private void stopRedrawThread() {
        canDraw = false;
        try {
            redrawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void changeGridLength(int newLength) {
        stopRedrawThread();
        gridLength = newLength;
        initGameLogic();
        startRedrawThread();
    }

    public int getGridLength() {
        return gridLength;
    }
}
