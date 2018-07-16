package com.notification.indication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by mohit.soni on 11/20/2017.
 */

public class NotificationHome extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "NotificationHome";

    private boolean granted = true;
    AppConstants appConstants = new AppConstants();
    TextClock clock;
    TextView date;
    ImageView iv;
    Animation ani;

    int[] image = new int[]{
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
    };
//    Indicator indicator;
//    NotificationListenerReceiver notificationListenerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Activity Created");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
        request();
        serviceTask();
        register();
        setInit();
    }

    /**
     * register broadcast receiver
     */
    private void register() {
        Set<String> listnerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        boolean haveAccess = false;
        for (String sd : listnerSet) {
            if (sd.equals(AppConstants.ROOT_PACKAGE)) {
                haveAccess = true;
            }
        }
        if (!haveAccess) {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }

//        notificationListenerReceiver = new NotificationListenerReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(AppConstants.NOTIFICATION_RECEIVER);
//        registerReceiver(notificationListenerReceiver, filter);
    }

    /**
     * perform service tasks
     */
    public void serviceTask() {
        if (!AppConstants.isServiceRunning(this.getApplicationContext(), NotificationMonitorService.class)) {
            if (Build.VERSION.SDK_INT > 25) {
                startForegroundService(new Intent(this, NotificationMonitorService.class));
            }else {
                startService(new Intent(this, NotificationMonitorService.class));
            }
        }
    }

    /**
     * asked for request if API > 22
     */
    private void request() {
        if (Build.VERSION.SDK_INT > 22) {
            boolean canChange = Settings.System.canWrite(this);
            if (!canChange) {
                Intent openSetting = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                openSetting.setData(Uri.parse("package:" + this.getPackageName()));
                openSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openSetting);
            }
            requestPermissions(AppConstants.PERMISSION, AppConstants.PERMISSION_REQUESTCODE);
        }
    }

    /**
     * unregister displayReceiver
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(notificationListenerReceiver);
    }

    /**
     * do nothing
     */
    @Override
    public void onBackPressed() {
        return;
    }

    /**
     * set initial variables
     */
    public void setInit() {
        setContentView(R.layout.notification_home);

        clock = (TextClock) findViewById(R.id.clock);
        date = (TextView) findViewById(R.id.date);
        iv = (ImageView) findViewById(R.id.iv);
        clock.setFormat12Hour("KK:mm");

        clock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NotificationMonitorService.display = false;
//                stopService(new Intent(NotificationHome.this,NotificationMonitorService.class));
            }
        });

        clock.setTextColor(getResources().getColor(R.color.white));
        date.setTextColor(getResources().getColor(R.color.white));

        date.setText(appConstants.getNumDate(date));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(date.getText().toString().matches(".*[a-zA-Z]+.*")){
//                }
//                if(date.getText().toString().matches(".*[0-9]+.*") && input1.matches(".*[$&+,:;=?@#|'<>.-^*()%!]+.*")){
//                }
                String tag = (String) date.getTag();
                if (tag.equals("num")) {
                    date.setText(appConstants.getTextDate(date));
                } else {
                    date.setText(appConstants.getNumDate(date));
                }
            }
        });

        //-- bottom indicator
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//
//        indicator = ((Indicator) findViewById(R.id.ind));
//        indicator.setDimen(width, 10);
//        indicator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                indicator.stop();
//            }
//        });
//        indicator.start();

        iv.setBackground(getResources().getDrawable(setimage()));
        ani = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        iv.setAnimation(ani);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ani.cancel();
                finish();
            }
        });
    }

    private void log(String msg) {
        Log.i(TAG, msg);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSION_REQUESTCODE:
                if (permissions.length > 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                }
        }
    }

//    /**
//     * get event from notification listener class
//     */
//    class NotificationListenerReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            NotificationBean statusBar = (NotificationBean) intent.getExtras().get("notification");
//        }
//    }

    /**
     * set image according to time
     * @return
     */
    public int setimage(){
        String[] time = appConstants.getTime();
        int x = Integer.parseInt(time[0]);
        int img = R.drawable.a;
        if(x <= 23){
            img = image[0];
        }
        if(x <= 18){
            img = image[1];
        }
        if(x <= 12){
            img = image[2];
        }
        if(x <= 6){
            img = image[3];
        }
        return img;
    }

}
