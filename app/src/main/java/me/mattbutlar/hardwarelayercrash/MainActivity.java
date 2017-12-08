package me.mattbutlar.hardwarelayercrash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_VIEW_HEIGHT = 20000;  //Some large number to hit the limit

    private static final int ANIMATION_DURATION = 1000;

    private RelativeLayout animatedView;

    private boolean collapse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animatedView = findViewById(R.id.animated_view);

        findViewById(R.id.button_hardware_layer_crash).setOnClickListener(v -> {
            hardwareLayerAnimate(collapse);
            collapse = !collapse;
        });

        findViewById(R.id.button_transition_manager_crash).setOnClickListener(v -> {
            tranistionManagerAnimate(collapse);
            collapse = !collapse;
        });

        findViewById(R.id.button_no_crash).setOnClickListener(v -> {
            noLayerAnimate(collapse);
            collapse = !collapse;
        });
    }

    private void hardwareLayerAnimate(boolean collapse) {
        resetAnimatedView();

        final int viewHeight = animatedView.getHeight();
        final float differential = (collapse ? animatedView.getHeight() : (animatedView.getHeight() - MAX_VIEW_HEIGHT));

        animatedView.animate()
                .setDuration(ANIMATION_DURATION)
                .setUpdateListener(animation -> {
                    ViewGroup.LayoutParams layoutParams = animatedView.getLayoutParams();
                    layoutParams.height = (int) (viewHeight - (differential * animation.getAnimatedFraction()));
                    animatedView.setLayoutParams(layoutParams);
                })
                .withStartAction(() -> animatedView.setLayerType(View.LAYER_TYPE_HARDWARE, null))
                .withEndAction(() -> animatedView.setLayerType(View.LAYER_TYPE_NONE, null))
                .start();
    }

    private void tranistionManagerAnimate(boolean collapse) {
        animatedView.setVisibility((collapse ? View.VISIBLE : View.GONE));

        if (animatedView.getHeight() < MAX_VIEW_HEIGHT) {
            ViewGroup.LayoutParams layoutParams = animatedView.getLayoutParams();
            layoutParams.height = MAX_VIEW_HEIGHT;
            animatedView.setLayoutParams(layoutParams);
        }

        Transition transition = new AutoTransition().setOrdering(TransitionSet.ORDERING_TOGETHER);
        transition.setDuration(ANIMATION_DURATION);
        TransitionManager.beginDelayedTransition(animatedView, transition);

        animatedView.setVisibility((collapse ? View.GONE : View.VISIBLE));
    }

    private void noLayerAnimate(boolean collapse) {
        resetAnimatedView();

        final int viewHeight = animatedView.getHeight();
        final float differential = (collapse ? animatedView.getHeight() : (animatedView.getHeight() - MAX_VIEW_HEIGHT));

        animatedView.animate()
                .setDuration(ANIMATION_DURATION)
                .setUpdateListener(animation -> {
                    ViewGroup.LayoutParams layoutParams = animatedView.getLayoutParams();
                    layoutParams.height = (int) (viewHeight - (differential * animation.getAnimatedFraction()));
                    animatedView.setLayoutParams(layoutParams);
                })
                .start();
    }

    private void resetAnimatedView() {
        animatedView.setVisibility(View.VISIBLE);
        animatedView.animate().cancel();
    }
}
