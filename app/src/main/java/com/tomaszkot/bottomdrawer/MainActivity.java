package com.tomaszkot.bottomdrawer;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private AppDrawer appDrawer;
    private static final int BUTTON_ONE = 1;
    private static final int BUTTON_TWO = 2;
    private static final int BUTTON_THREE = 3;

    //********************************************************************************************** Initialize
    private void initialize() {
        // ----------------------------------------------------------------------------------------- Button ONE
        final Button buttonOne = (Button) findViewById(R.id.button_one);
        assert buttonOne != null;
        buttonOne.setOnClickListener(new buttonsListener());
        buttonOne.setTag(BUTTON_ONE);
        setButtonColor(R.color.color_1, buttonOne);

        // ----------------------------------------------------------------------------------------- Button TWO
        final Button buttonTwo = (Button) findViewById(R.id.button_two);
        assert buttonTwo != null;
        buttonTwo.setOnClickListener(new buttonsListener());
        buttonTwo.setTag(BUTTON_TWO);
        setButtonColor(R.color.color_2, buttonTwo);

        // ----------------------------------------------------------------------------------------- Button THREE
        final Button buttonThree = (Button) findViewById(R.id.button_three);
        assert buttonThree != null;
        buttonThree.setOnClickListener(new buttonsListener());
        buttonThree.setTag(BUTTON_THREE);
        setButtonColor(R.color.color_3, buttonThree);

        // ----------------------------------------------------------------------------------------- AppDrawer Class Instance
        appDrawer = new AppDrawer(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        // Hiding the ActionBar
        assert getSupportActionBar() != null;
        getSupportActionBar().hide();
    }

    // --------------------------------------------------------------------------------------------- Buttons Listener
    public class buttonsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int buttonTag = (int)v.getTag();
            appDrawer.switchDrawer(buttonTag);
        }
    }

    // --------------------------------------------------------------------------------------------- Change the color of the rounded buttons
    private void setButtonColor(int color, Button selectColorButton){
        GradientDrawable circleButton = (GradientDrawable) selectColorButton.getBackground();
        circleButton.setColor(ContextCompat.getColor(getApplicationContext(),color));
    }

}

