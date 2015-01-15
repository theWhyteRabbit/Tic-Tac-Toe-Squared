package com.diusrex.tictactoe.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.diusrex.tictactoe.R;

import java.util.Calendar;

public class HowToPlayActivity extends Activity {
    static private final String CURRENT_SCREEN_INDEX_SAVE = "CurrentScreenIndex";
    static private final int ANIMATION_LENGTH = 300;
    static private final int NUMBER_OF_SCREENS = 9;

    ViewFlipper flipper;
    int currentScreenIndex;

    long previousAnimationTime;

    boolean[] shouldAnimateForNext;
    boolean[] shouldAnimateForBack;

    Button backButton;
    Button nextButton;

    Animation toLeftIn;
    Animation toLeftOut;
    Animation toRightIn;
    Animation toRightOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        loadAnimations();

        backButton = (Button) findViewById(R.id.backButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        addAllScreens();

        if (savedInstanceState != null) {
            currentScreenIndex = savedInstanceState.getInt(CURRENT_SCREEN_INDEX_SAVE);
        } else {
            currentScreenIndex = 0;
        }

        updateDisplay();
    }

    private void updateDisplay() {
        flipper.setDisplayedChild(currentScreenIndex);

        updateButtonsText();
        previousAnimationTime = Calendar.getInstance().getTimeInMillis();
    }

    private void loadAnimations() {
        toLeftIn = AnimationUtils.loadAnimation(this, R.anim.left_in);
        toLeftOut = AnimationUtils.loadAnimation(this, R.anim.left_out);
        toRightIn = AnimationUtils.loadAnimation(this, R.anim.right_in);
        toRightOut = AnimationUtils.loadAnimation(this, R.anim.right_out);
    }

    private void addAllScreens() {
        shouldAnimateForNext = new boolean[NUMBER_OF_SCREENS];
        shouldAnimateForBack = new boolean[NUMBER_OF_SCREENS];
        currentScreenIndex = 0;
        addHowToWin();
        addBoardInfo();
        addPlayRules();
    }

    private void addHowToWin() {
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_how_to_win_one, true, true);
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_how_to_win_two, true, false);
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_how_to_win_three, false, true);
    }

    private void addBoardInfo() {
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_board_info_one, true, false);
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_board_info_two, false, true);
    }

    private void addPlayRules() {
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_rules_one, true, false);
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_rules_two, false, false);
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_rules_three, false, true);
        addViewIdAndSetCanAnimateNextAndBack(R.layout.how_to_play_rules_four, true, true);
    }

    private void addViewIdAndSetCanAnimateNextAndBack(int layoutId, boolean next, boolean back) {
        LayoutInflater inflater = getLayoutInflater();
        flipper.addView(inflater.inflate(layoutId, null));
        shouldAnimateForNext[currentScreenIndex] = next;
        shouldAnimateForBack[currentScreenIndex] = back;
        ++currentScreenIndex;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SCREEN_INDEX_SAVE, currentScreenIndex);
    }

    public void nextScreen(View v) {
        if (!canChangeScreens())
            return;

        ++currentScreenIndex;

        if (currentScreenIndex == NUMBER_OF_SCREENS) {
            finish();
            return;
        }

        if (shouldAnimateNextScreen()) {
            flipper.setInAnimation(toLeftIn);
            flipper.setOutAnimation(toLeftOut);
        } else {
            disableAnimations();
        }

        updateDisplay();
    }

    public void previousScreen(View v) {
        if (!canChangeScreens())
            return;

        --currentScreenIndex;

        if (currentScreenIndex < 0) {
            finish();
            return;
        }

        if (shouldAnimateBackScreen()) {
            flipper.setInAnimation(toRightIn);
            flipper.setOutAnimation(toRightOut);
        } else {
            disableAnimations();
        }
        updateDisplay();
    }

    private void disableAnimations() {
        flipper.setInAnimation(null);
        flipper.setOutAnimation(null);
    }

    private boolean shouldAnimateNextScreen() {
        return shouldAnimateForNext[currentScreenIndex];
    }

    private boolean shouldAnimateBackScreen() {
        return shouldAnimateForBack[currentScreenIndex];
    }

    private boolean canChangeScreens() {
        return Calendar.getInstance().getTimeInMillis() - ANIMATION_LENGTH >= previousAnimationTime;
    }

    private void updateButtonsText() {
        if (currentScreenIndex == 0) {
            backButton.setText(R.string.cancel);
        } else {
            backButton.setText(R.string.back);
        }

        if (currentScreenIndex + 1 == NUMBER_OF_SCREENS) {
            nextButton.setText(R.string.finish);
        } else {
            nextButton.setText(R.string.next);
        }
    }
}
