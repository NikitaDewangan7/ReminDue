package blocker.com.newalarmservice.Activity.splashActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import blocker.com.newalarmservice.Activity.mainDrawerActivity.DrawerActivity;
import blocker.com.newalarmservice.R;


public class SplashActivity extends AppCompatActivity {
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15, img16, img17, img18, img19, img20;
    private ImageView imgAlarm;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        initUIElement();

        ObjectAnimator animator = ObjectAnimator.ofFloat(img5, "scaleX", 1.0f, 1.2f);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorSet set = new AnimatorSet();
                set.setStartDelay(250);
                set.setDuration(400);
                ObjectAnimator alarmScaleX = ObjectAnimator.ofFloat(imgAlarm, "scaleX", 1.0f, 1.4f);
                ObjectAnimator alarmScaleY = ObjectAnimator.ofFloat(imgAlarm, "scaleY", 1.0f, 1.4f);
                ObjectAnimator alarmRotateLeft = ObjectAnimator.ofFloat(imgAlarm, "rotation", 0, -10);
                ObjectAnimator alarmRotateRight = ObjectAnimator.ofFloat(imgAlarm, "rotation", -10, 10);
                ObjectAnimator alarmRotateRight1 = ObjectAnimator.ofFloat(imgAlarm, "rotation", 10, 0);
                set.playTogether(alarmScaleX, alarmScaleY);
                set.play(alarmRotateLeft).after(alarmScaleY);
                set.play(alarmRotateRight).after(alarmRotateLeft);
                set.play(alarmRotateRight1).after(alarmRotateRight);
                ObjectAnimator alarmScaleXAgain = ObjectAnimator.ofFloat(imgAlarm, "scaleX", 1.4f, 1f);
                ObjectAnimator alarmScaleYAgain = ObjectAnimator.ofFloat(imgAlarm, "scaleY", 1.4f, 1f);
                alarmScaleXAgain.setDuration(100);
                alarmScaleYAgain.setDuration(100);
                set.play(alarmScaleXAgain).with(alarmScaleYAgain).after(alarmRotateRight1);
                set.start();
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (flag) {
                            Intent intent = new Intent(SplashActivity.this, DrawerActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                translateView(imgAlarm, img1, 600);
                translateView(imgAlarm, img2, 200);
                translateView(imgAlarm, img3, 400);
                translateView(imgAlarm, img4, 100);
                translateView(imgAlarm, img5, 600);
                translateView(imgAlarm, img6, 400);
                translateView(imgAlarm, img7, 200);
                translateView(imgAlarm, img8, 300);
                translateView(imgAlarm, img9, 600);
                translateView(imgAlarm, img10, 200);
                translateView(imgAlarm, img11, 400);
                translateView(imgAlarm, img12, 500);
                translateView(imgAlarm, img13, 600);
                translateView(imgAlarm, img14, 100);
                translateView(imgAlarm, img15, 500);
                translateView(imgAlarm, img16, 300);
                translateView(imgAlarm, img17, 600);
                translateView(imgAlarm, img18, 500);
                translateView(imgAlarm, img19, 400);
                translateView(imgAlarm, img20, 300);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    private void translateView(View centreView, final View serviceIcon, long delay) {
        int coordinates[] = new int[2];
        centreView.getLocationOnScreen(coordinates);
        int endX = coordinates[0] - serviceIcon.getLeft() + (centreView.getWidth() / 4);
        int endY = coordinates[1] - serviceIcon.getTop() + (centreView.getHeight() / 4);

        int coordinatesServiceIcon[] = new int[2];
        serviceIcon.getLocationOnScreen(coordinatesServiceIcon);
        int startX = coordinatesServiceIcon[0] - serviceIcon.getLeft();
        int startY = coordinatesServiceIcon[1] - serviceIcon.getTop();

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(serviceIcon, "translationX", startX, endX);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(serviceIcon, "translationY", startY, endY);
        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(serviceIcon, "scaleX", 1.5f, .5f);
        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(serviceIcon, "scaleY", 1.5f, .5f);
        ObjectAnimator iconAlphaAnimator = ObjectAnimator.ofFloat(serviceIcon, "alpha", 0f, 1f);

        iconAlphaAnimator.setStartDelay(delay);
        iconAlphaAnimator.setDuration(700);
        iconAlphaAnimator.start();

        AnimatorSet translateSet = new AnimatorSet();
        translateSet.setInterpolator(new AccelerateInterpolator());
        translateSet.playTogether(objectAnimatorX, objectAnimatorY, objectAnimatorScaleX, objectAnimatorScaleY);

        translateSet.setDuration(1200);
        translateSet.setStartDelay(delay);
        translateSet.start();
        translateSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                serviceIcon.setAlpha(0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void initUIElement() {
        imgAlarm = (ImageView) findViewById(R.id.imgAlarm);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        img5 = (ImageView) findViewById(R.id.img5);
        img6 = (ImageView) findViewById(R.id.img6);
        img7 = (ImageView) findViewById(R.id.img7);
        img8 = (ImageView) findViewById(R.id.img8);
        img9 = (ImageView) findViewById(R.id.imgi9);
        img10 = (ImageView) findViewById(R.id.img9);
        img11 = (ImageView) findViewById(R.id.img10);
        img12 = (ImageView) findViewById(R.id.img11);
        img13 = (ImageView) findViewById(R.id.img12);
        img14 = (ImageView) findViewById(R.id.img13);
        img15 = (ImageView) findViewById(R.id.img14);
        img16 = (ImageView) findViewById(R.id.img15);
        img17 = (ImageView) findViewById(R.id.img17);
        img18 = (ImageView) findViewById(R.id.img18);
        img19 = (ImageView) findViewById(R.id.img19);
        img20 = (ImageView) findViewById(R.id.img20);
        img1.setImageResource(R.drawable.accomodation);
        img2.setImageResource(R.drawable.car);
        img3.setImageResource(R.drawable.pet);
        img4.setImageResource(R.drawable.creditcard);
        img5.setImageResource(R.drawable.twowheeler);
        img6.setImageResource(R.drawable.sports);
        img7.setImageResource(R.drawable.electricity);
        img8.setImageResource(R.drawable.food);
        img9.setImageResource(R.drawable.shopping);
        img10.setImageResource(R.mipmap.technology);
        img11.setImageResource(R.drawable.gifts);
        img12.setImageResource(R.drawable.other);
        img13.setImageResource(R.drawable.dth);
        img14.setImageResource(R.drawable.insurance);
        img15.setImageResource(R.drawable.vacation);
        img16.setImageResource(R.drawable.education);
        img17.setImageResource(R.drawable.medicare);
        img18.setImageResource(R.drawable.investment);
        img19.setImageResource(R.drawable.saving);
        img20.setImageResource(R.drawable.childcare);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!flag) {
            Intent intent = new Intent(SplashActivity.this, DrawerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        flag = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
