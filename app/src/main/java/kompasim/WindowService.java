package kompasim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;


import android.content.Context;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.alvido_bahor.wechattool.R;
import com.suke.widget.SwitchButton;


public class WindowService extends Service implements SwitchButton.OnCheckedChangeListener {


    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    //
    public static boolean flag = false;//标记悬浮窗是否已经显示
    private LinearLayout window;
    public ImageButton button;
    public LinearLayout controls;
    private boolean open = true;//悬浮窗是否是打开状态

    DisplayMetrics metric = new DisplayMetrics();

    public WindowService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        //获取windowManager对象
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metric);
        //获取LayoutParams对象
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
        layoutParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        //layoutParams.format = PixelFormat.RGBA_8888;//实现渐变效果需要
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;// 模态
        layoutParams.width = (int) (300*metric.density);//窗口的宽
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;//窗口的高
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        // layoutParams.x = (screenWidth - 600) / 2;//窗口出现位置的偏移量
        layoutParams.x = 10;//窗口出现位置的偏移量
        layoutParams.y = 500;//窗口出现位置的偏移量
        //layoutParams.alpha = 0.1f;//窗口的透明度
        final LayoutInflater inflater = LayoutInflater.from(getApplication());
        //
        //
        window = (LinearLayout) inflater.inflate(R.layout.window, null);
        button = (ImageButton) window.findViewById(R.id.button);
        controls = (LinearLayout) window.findViewById(R.id.controls);

//        SwitchButton  app = (SwitchButton )controls.findViewById(R.id.app);
        MyApplication.money_ = (SwitchButton )controls.findViewById(R.id.money);
        MyApplication.answer_ = (SwitchButton )controls.findViewById(R.id.answer);
//        SwitchButton  one = (SwitchButton )controls.findViewById(R.id.one);

//        app.setOnCheckedChangeListener(this);
        MyApplication.money_.setOnCheckedChangeListener(this);
        MyApplication.answer_.setOnCheckedChangeListener(this);
//        one.setOnCheckedChangeListener(this);

        //添加desktop_pet布局文件内的图片onTouch监听器
        button.setOnTouchListener(new View.OnTouchListener() {
            private float startX;//拖动开始之前悬浮窗的x位置
            private float startY;//拖动开始之前悬浮窗的y位置
            private float lastX;//上个MotionEvent的x位置
            private float lastY;//上个MotionEvent的y位置
            private float nowX;//这次MotionEvent的x位置
            private float nowY;//这次MotionEvent的y位置
            private float translateX;//每次拖动产生MotionEvent事件之后窗口所要移动的x轴距离
            private float translateY;//每次拖动产生MotionEvent事件的时候窗口所要移动的x轴距离

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                boolean ret = false;
                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    startX = layoutParams.x;
                    startY = layoutParams.y;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    nowX = event.getRawX();
                    nowY = event.getRawY();
                    //这次MotionEvent要移动的距离
                    translateX = (int) (nowX - lastX);
                    translateY = (int) (nowY - lastY);
                    layoutParams.x += translateX;
                    layoutParams.y += translateY;
                    //更新布局
                    windowManager.updateViewLayout(window, layoutParams);
                    lastX = nowX;
                    lastY = nowY;
                } else if (action == MotionEvent.ACTION_UP) {
                    //跟开始位置比较，检测是否有明显的多动，不是的话返回false，继续执行onClick
                    boolean a = Math.abs(layoutParams.x - startX) < 5;
                    boolean b = Math.abs(layoutParams.y - startY) < 5;
                    //窗口xy移动距离小于我们期望的范围当做onClick，返回true继续执行onClick
                    if (a && b) {
                        ret = false;
                    } else {
                        layoutParams.x = 0;
                        ret = true;
                    }
                    //刷新布局
                    windowManager.updateViewLayout(window, layoutParams);
                }
                return ret;
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f, 1f);
                animator.setDuration(1000);
                animator.start();
                if (open) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(controls, "rotationX", 0.0F, 360.0F);
                    anim.setDuration(500);
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            window.removeView(controls);
                            layoutParams.width = (int) (70*metric.density);
                            windowManager.updateViewLayout(window, layoutParams);
                            open = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    anim.start();
                } else {
                    window.addView(controls);
                    layoutParams.width = (int) (300*metric.density);
                    windowManager.updateViewLayout(window, layoutParams);
                    open = true;
                    ObjectAnimator.ofFloat(controls, "rotationX", 0.0F, 360.0F).setDuration(500).start();
                }
            }
        });
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!WindowService.flag) {
            if (Build.VERSION.SDK_INT >= 23) {
                if(!Settings.canDrawOverlays(this)) {
                    Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(i);
                } else {
                    //Android6.0以上
                    windowManager.addView(window, layoutParams);
                    WindowService.flag = true;
                }
            } else {
                //Android6.0以下，不用动态声明权限
                windowManager.addView(window, layoutParams);
                WindowService.flag = true;
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (window.getParent() != null) {
            windowManager.removeView(window);
            WindowService.flag = false;
        }
        super.onDestroy();
    }



    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.money:
                if (isChecked){
                    Toast.makeText(this, "بولاق تالىشىش باشلاندى", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(this, "بولاق تالىشىش توختىتىلدى", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
                break;
        }
    }
}
