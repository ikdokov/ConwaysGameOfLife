package com.gameoflife.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gameoflife.core.GameLogic;

public class GameGrid extends SurfaceView implements Runnable, SurfaceHolder.Callback, ScaleGestureDetector.OnScaleGestureListener {

    private int cellCount;
    private float cellSize;

    private float scaleValue = 1.0f;

    private Paint grayPaintBrush = new Paint();
    private Paint whitePaintBrush = new Paint();

    private boolean canDraw;

    private GameLogic gameLogic;

    private Thread gameThread;

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private ScaleGestureDetector sgd;

    public GameGrid(final Context context, int cellCount) {
        super(context);
        this.cellCount = cellCount;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        initBrushes();
        sgd = new ScaleGestureDetector(context, this);
    }

    public void initBrushes() {
        whitePaintBrush.setStyle(Paint.Style.FILL_AND_STROKE);
        whitePaintBrush.setColor(Color.WHITE);
        whitePaintBrush.setStrokeWidth(1);

        grayPaintBrush.setStyle(Paint.Style.STROKE);
        grayPaintBrush.setColor(Color.DKGRAY);
        grayPaintBrush.setStrokeWidth(1);
    }

    @Override
    public void run() {

        while (canDraw) {
            if(!surfaceHolder.getSurface().isValid()) {
                continue;
            }

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

    public void doDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        float cellMiddle = cellSize / 2;

        boolean [][] currentGrid = gameLogic.getCurrentGeneration();

        for (int i = 0; i < currentGrid.length; i++) {
            for (int j = 0; j < currentGrid[i].length; j++) {
                if (currentGrid[i][j]) {
                    canvas.drawCircle((j * cellSize) + cellMiddle, (i * cellSize) + cellMiddle, cellMiddle, whitePaintBrush);
                } else {
                    canvas.drawCircle((j * cellSize) + cellMiddle, (i * cellSize) + cellMiddle, cellMiddle, grayPaintBrush);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int row = (int)(event.getY() / cellSize);
        int column = (int)(event.getX() / cellSize);

        sgd.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                gameLogic.resurrectCell(row, column);
                return true;
            case MotionEvent.ACTION_MOVE:
                gameLogic.resurrectCell(row, column);
                return true;

            default:
                invalidate();
        }

        return super.onTouchEvent(event);
    }

    public void randomizeGrid(){
        gameLogic.clearGrid();
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
        int width = getWidth();// surfaceHolder.getSurfaceFrame().width();
        int height = getHeight(); // surfaceHolder.getSurfaceFrame().height();

        int columns, rows;

        if (width > height) {
            rows = cellCount;
            cellSize = height / (float) cellCount;
            columns = (int)(width / cellSize);
        } else {
            columns = cellCount;
            cellSize = width / (float) cellCount;
            rows = (int)(height / cellSize);
        }

        gameLogic = new GameLogic(rows, columns);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        startGameThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopGameThread();
    }

    private void startGameThread() {
        canDraw = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void stopGameThread() {
        canDraw = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setGridSize(int size) {
        stopGameThread();
        cellCount = size;
        initGameLogic();
        startGameThread();
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        scaleValue *= scaleGestureDetector.getScaleFactor();

        scaleValue = Math.max(5.0f, Math.min(scaleValue, 300f));

        setGridSize((int)scaleValue);
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    public int getCellCount() {
        return cellCount;
    }
}
