package org.zarroboogs.maps.ui.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;


/**
 * Created by andforce on 15/8/16.
 */
public class ViewAnimUtils {

    public static void pushOutWithInterpolator(View view, final AnimEndListener listener) {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
        animation.setDuration(300);
        Interpolator interpolator = AnimationUtils.loadInterpolator(view.getContext().getApplicationContext(), android.R.anim.accelerate_interpolator);

        animation.setInterpolator(interpolator);
        if (listener != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onAnimEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        view.startAnimation(animation);
    }

    public static void dropDownWithInterpolator(View view, final AnimEndListener listener) {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300);
        Interpolator interpolator = AnimationUtils.loadInterpolator(view.getContext().getApplicationContext(), android.R.anim.accelerate_decelerate_interpolator);

        animation.setInterpolator(interpolator);
        if (listener != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onAnimEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        view.startAnimation(animation);
    }


    public static void popupinWithInterpolator(View view, final AnimEndListener listener) {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300);
        Interpolator interpolator = AnimationUtils.loadInterpolator(view.getContext().getApplicationContext(), android.R.anim.accelerate_decelerate_interpolator);

        animation.setInterpolator(interpolator);
        if (listener != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onAnimEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        view.startAnimation(animation);
    }

    public static void popupoutWithInterpolator(View view, final AnimEndListener listener) {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        animation.setDuration(300);
        Interpolator interpolator = AnimationUtils.loadInterpolator(view.getContext().getApplicationContext(), android.R.anim.accelerate_interpolator);
        animation.setInterpolator(interpolator);
        if (listener != null){
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onAnimEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        view.startAnimation(animation);
    }

}
