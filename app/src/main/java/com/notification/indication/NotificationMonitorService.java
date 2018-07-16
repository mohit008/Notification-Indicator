package com.notification.indication;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;

import static com.notification.indication.AppConstants.text;
import static com.notification.indication.AppConstants.title_n;


/**
 * Created by mohit on 28-Mar-17.
 */
public class NotificationMonitorService extends NotificationListenerService {

    private static final String TAG = "[Notification.Service]";

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    Context context;
    DisplayOffReceiver displayReceiver;

    static boolean display = false;

    @Override
    public void onCreate() {
        log("Service Created");
        // disable key guard
        KeyguardManager kManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock key = kManager.newKeyguardLock(KEYGUARD_SERVICE);
        key.disableKeyguard();

        // call display reciever with filter
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        // register display receiver
        displayReceiver = new DisplayOffReceiver();
        registerReceiver(displayReceiver, filter);

        context = this.getApplicationContext();

        if (Build.VERSION.SDK_INT >= 26) {
            String channel_id = "notification.x.service";
            NotificationChannel channel = new NotificationChannel(channel_id, "notification monitoring", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, channel_id)
                    .setContentTitle(title_n)
                    .setContentText(text).build();
            startForeground(963852741, notification);
        }

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("Service Destroyed");
        unregisterReceiver(displayReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        log("========== NotificationPosted :: ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        sendNotificationDetail(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        log("========== NotificationRemoved :: ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
    }

    /**
     * send notification detail to activity class
     */
    public void sendNotificationDetail(StatusBarNotification sbn) {
        NotificationBean bean = new NotificationBean();
        bean.setPackageName(sbn.getPackageName());
        bean.setId(sbn.getId() + "");
        bean.setTag(sbn.getTag());
        bean.setPostTime(sbn.getPostTime() + "");
        bean.setAppName(getAppName(sbn.getPackageName()));
        bean.setIcon(sbn.getNotification().icon);
        bean.setCount(0);
        CharSequence ticker = sbn.getNotification().tickerText;
        bean.setTicker(ticker);
        log("========== Notification :: " + bean.toString());
        if (ticker != null) {
            if (!checkPckg(bean)) {
               show();
//                Intent intent = new Intent(AppConstants.NOTIFICATION_RECEIVER);
//                intent.putExtra("notification", bean);
//                sendBroadcast(intent);
            }
        }
        // for dialog generating missed call
        // STATUS_BAR_NOTIFIER - call comming / out going
        // GroupSummary_MissedCall - call missed
        if(sbn.getTag()!=null){
            if (sbn.getId() == 1 && sbn.getNotification().tickerText == null && sbn.getTag().equals("GroupSummary_MissedCall")) {
                show();
            }
        }
    }


    /**
     * get application name from package
     *
     * @param pkg
     * @return
     */
    public String getAppName(String pkg) {
        try {
            PackageManager manager = context.getPackageManager();
            ApplicationInfo applicationInfo = manager
                    .getApplicationInfo(pkg, PackageManager.GET_META_DATA);
            return (String) manager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * switch on device
     */
    public void wakeUP() {
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        wakeLock.acquire();
        wakeLock.release();
    }

    /**
     * call activity
     */
    public void callHome() {
        if (!AppConstants.isActivityRunning(context)) {
            Intent in = new Intent(context, NotificationHome.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }
    }

    public boolean checkPckg(NotificationBean notificationBeans) {
        for (int i = 0; i < pckgName.length; i++) {
            String one = notificationBeans.packageName;
            String second = pckgName[i];
            if (one.equals(second)) {
                return true;
            }
        }
        return false;
    }

    /**
     * exclude these package
     */
    public static String[] pckgName = new String[]{
            "com.android.providers.downloads",
            "com.android.vending",
            "com.android.dialer"
    };

    private void log(String msg) {
        Log.i(TAG, msg);
    }

    public void show(){
        if(display){
            wakeUP();
            callHome();
            display = false;
        }
    }
}
