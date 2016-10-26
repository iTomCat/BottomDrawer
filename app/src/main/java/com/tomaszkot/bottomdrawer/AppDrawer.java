package com.tomaszkot.bottomdrawer;


import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * height of drawer set in res/dimens/"drawer_height"
 */

class AppDrawer {
    private int waitMS = 2000;  // The time after which the drawer is closing automatically
    private Handler handlCountDown;
    private  View bottomDrawer;
    private Activity activity;
    private static final int DRAWER_UP = 1;
    private static final int DRAWER_DOWN = 0;
    private static int direct;
    private int color;
    private int mainlayoutHeight;
    private int currentDrawer = -1;
    private boolean isSwitched = false;
    private TextView drawerTxt;
    private RelativeLayout mainLayout;

    private enum S {OPEN_NOW, OPEN, CLOSE_NOW, CLOSE, CANCELED_NOW, CANCEL, TIME_OFF}     // States of animation
    private S animState = S.CLOSE;                                                        // Set state

    //*********************************************************************************************** Constructor
    AppDrawer(Activity mainActivity) {
        activity = mainActivity;
        initialize();
        getLayoutHeight();
    }

    // ********************************************************************************************** Initialize
    private void initialize() {
        // Bottom Drawer
        bottomDrawer =  activity.findViewById(R.id.bottom_drawer);
        drawerTxt = (TextView) bottomDrawer.findViewById(R.id.drawer_txt);

        // Handler for timing for automatically closing the drawer
        handlCountDown = new Handler();
    }

    //********************************************************************************************** Open and Close the Drawer /Animation/
    private void drawerMovement(int movement){
        switch (movement) {
            case DRAWER_UP: // --------------------------------------------------------------------- Drawer UP
                float heightStatusMenu = activity.getResources().getDimension(R.dimen.drawer_height);
                bottomDrawer.animate().translationY(mainlayoutHeight - heightStatusMenu)
                        .setListener(new animationListener());
                direct = DRAWER_UP;
                break;

            case DRAWER_DOWN: // ------------------------------------------------------------------- Drawer DOWN
                bottomDrawer.animate().translationY(mainlayoutHeight)
                        .setListener(new animationListener());
                direct = DRAWER_DOWN;
                break;
        }
    }

    //**********************************************************************************************  Animation Listener
    private class animationListener implements  Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            if ((direct == DRAWER_UP) && (animState != S.CANCELED_NOW) && (animState != S.CANCEL)  && (animState != S.TIME_OFF)) animState = S.OPEN_NOW;
            if ((direct == DRAWER_DOWN) && (animState != S.CANCELED_NOW) && (animState != S.CANCEL)  && (animState != S.TIME_OFF)) animState = S.CLOSE_NOW;
            Log.d("Test", "Start Animation: " + animState);

            // Turning off the automatic timer closing drawer
            handlCountDown.removeCallbacks(closeDrawerTimer);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if ((direct == DRAWER_UP) && (animState != S.CANCELED_NOW) && (animState != S.CANCEL)){
                animState = S.OPEN;

                // Turning on the automatic timer closing drawer
                handlCountDown.postDelayed(closeDrawerTimer, waitMS);
            }
            if ((direct == DRAWER_DOWN) && (animState != S.CANCELED_NOW) && (animState != S.CANCEL)) animState = S.CLOSE;
            Log.d("Test", "End Animation: " + animState);

            // Animation Cancel
            if (animState == S.CANCELED_NOW){
                if (direct == DRAWER_UP){
                    Log.d("Test", "Animation Cancel - DIRECT UP: " + animState);
                    drawerMovement(DRAWER_DOWN);
                    animState = S.CANCEL;
                }else { // DIRECT DOWN
                    Log.d("Test", "Animation Cancel - DIRECT DOWN: " + animState);
                    animState = S.CANCEL;
                }
            }

            if ((animState != S.CANCELED_NOW) && (animState != S.CANCEL) && (animState != S.TIME_OFF))
                switchToNewDrawer(currentDrawer);

            // Close Drawer after animation cancel
            if (animState == S.CANCEL){
                if (animState == S.CLOSE)refreshData(currentDrawer);
                animState = S.OPEN_NOW;
                drawerMovement(DRAWER_UP);
                Log.d("Test", "Animation Cancel");
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animState = S.CANCELED_NOW;
            isSwitched = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {}
    }

    // ********************************************************************************************* The action performed when you press the button with a choice of drawer
    void switchDrawer(int selectedDrawer){
        switch (animState) {
            case CLOSE: // ------------------------------------------------------------------------- DRAWER UP
                refreshData(selectedDrawer);
                drawerMovement(DRAWER_UP);
                break;

            case OPEN: // -------------------------------------------------------------------------- DRAWER DOWN
                if (selectedDrawer != currentDrawer){
                    drawerMovement(DRAWER_DOWN);
                    this.isSwitched = true;
                }
                break;

            case OPEN_NOW: // ---------------------------------------------------------------------- DRAWER is OPENING NOW
                if (selectedDrawer != currentDrawer){
                    drawerMovement(DRAWER_DOWN);
                }
                break;

            case CLOSE_NOW: // --------------------------------------------------------------------- DRAWER is CLOSING NOW
                if (selectedDrawer != currentDrawer){
                    drawerMovement(AppDrawer.DRAWER_UP);
                }
                break;

            case TIME_OFF: // ---------------------------------------------------------------------- Closing the drawer because time is over
                    drawerMovement(AppDrawer.DRAWER_DOWN);
                break;
        }
        currentDrawer = selectedDrawer;
    }

    // --------------------------------------------------------------------------------------------- Switching between drawers
    private void switchToNewDrawer(int currentDrawer){
        if (this.isSwitched){
            Log.d("Test" , "Switch Drawer " +currentDrawer);
            refreshData(currentDrawer);
            switchDrawer(currentDrawer);
            this.isSwitched = false;
        }
    }

    // --------------------------------------------------------------------------------------------- Changing the information on the drawer
    private void refreshData(int currentDrawer){
        switch (currentDrawer) {
            case 1: // Drawer 1
                color = R.color.color_1;
                break;
            case 2: // Drawer 2
                color = R.color.color_2;
                break;
            case 3: // Drawer 3
                color = R.color.color_3;
                break;
        }
        bottomDrawer.setBackgroundColor(ContextCompat.getColor(activity, color));
        String drawerDescr = "Drawer " + Integer.toString(currentDrawer);
        drawerTxt.setText(drawerDescr);
    }

    // --------------------------------------------------------------------------------------------- Closing the drawer
    private void closeDrawer(){
        animState = S.TIME_OFF;
        this.isSwitched = false;
        drawerMovement(DRAWER_DOWN);

        // Turning on the automatic timer closing drawer
        handlCountDown.postDelayed(closeDrawerTimer, waitMS);
    }

    // ********************************************************************************************* Get the Layout Height
    private void getLayoutHeight() {
        mainLayout = (RelativeLayout) activity.findViewById(R.id.main_layout);
        ViewTreeObserver vto = mainLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mainlayoutHeight = mainLayout.getMeasuredHeight();
                bottomDrawer.setY(mainlayoutHeight);
                Log.d("Test", "Layout Height: " + mainlayoutHeight );
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    // ********************************************************************************************* Timer for closing the drawer
    // Automatically closes drawer after a set time
    private Runnable closeDrawerTimer = new Runnable() {
        @Override
        public void run() {
            closeDrawer();
        }
    };

}