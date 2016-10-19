package com.gameoflife.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {

    public static final int INITIAL_GRID_LENGTH = 50;
    private GameGrid gameGrid;
    private LinearLayout gameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gameLayout = (LinearLayout) findViewById(R.id.game_layout);

        gameGrid = new GameGrid(this, INITIAL_GRID_LENGTH);

        gameLayout.addView(gameGrid);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.populate_grid:
                gameGrid.randomizeGrid();
                break;

            case R.id.clear_grid:
                gameGrid.clearGrid();
                break;

            case R.id.resize:
                showSizeSettingDialog();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void showSizeSettingDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.grid_size);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.grid_size_picker_dialog, null);
        builder.setView(dialogView);

        final NumberPicker np = (NumberPicker) dialogView.findViewById(R.id.numberPicker1);
        np.setMinValue(10);
        np.setMaxValue(500);
        np.setValue(gameGrid.getGridLength());

        np.setWrapSelectorWheel(false);

        builder.setPositiveButton(R.string.set_grid_size, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gameGrid.changeGridLength(np.getValue());
            }
        });

        builder.show();
    }
}