package com.notification.indication;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by mohit.soni on 11/20/2017.
 */

public class AppConstants {
    static final String NOTIFICATION_RECEIVER = "notification_receiver";
    static String ROOT_PACKAGE = "com.notification.indication";
    public static final String title_n = "notification";
    public static final String text = "monitoring x notification show";

    //permissions
    static final String[] PERMISSION = {
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.DISABLE_KEYGUARD,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.GET_TASKS,
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
    };

    static final int PERMISSION_REQUESTCODE = 811;

    /**
     * get calender of date and time
     *
     * @return date format
     */
    @NonNull
    public DateFormat getCal() {
//        DateFormat dateFormat =  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss EEEE");
//        Log.i(TAG,dateFormat.format(new Date()).split(" ")[2]);
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss EEEE", Locale.US);
    }

    /**
     * get date
     *
     * @return date format
     */
    @NonNull
    public String[] getDate() {
        return getCal().format(new Date()).split(" ")[0].split("/");
    }

    /**
     * get time
     *
     * @return time format
     */
    @NonNull
    public String[] getTime() {
        return getCal().format(new Date()).split(" ")[1].split(":");
    }

    /**
     * get day
     *
     * @return time format
     */
    @NonNull
    public String[] getDay() {
        return getCal().format(new Date()).split(" ")[2].split(":");
    }

    /**
     * get month name
     *
     * @param value
     * @return
     */
    public static String getMonth(String value) {
        switch (value) {
            case "01":
                return "Jan";

            case "02":
                return "Feb";

            case "03":
                return "March";

            case "04":
                return "April";

            case "05":
                return "May";

            case "06":
                return "June";

            case "07":
                return "July";

            case "08":
                return "Aug";

            case "09":
                return "Sep";

            case "10":
                return "Oct";

            case "11":
                return "Nov";

            case "12":
                return "Dec";
        }
        return value;
    }

    /**
     * get date in nueric value
     *
     * @param view
     * @return array
     */
    public String getNumDate(TextView view) {
        String[] date_ = getDate();
        setTag(view, "num");
        return getDay()[0] + "" + "\n" + date_[2] + "-" + date_[1] + "-" + date_[0];
    }

    /**
     * get date in text value
     *
     * @param view
     * @return array
     */
    public String getTextDate(TextView view) {
        String[] date_ = getDate();
        setTag(view, "text");
        return getDay()[0] + "" + "\n" + date_[2] + " " + getMonth(date_[1]) + " " + date_[0].substring(2, 4);
    }

    /**
     * set tag to text view
     *
     * @param view
     * @param value
     */
    public void setTag(TextView view, String value) {
        view.setTag(value);
    }


    /**
     * check if perticular permission is granted or not
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean checkPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * if our activity is running
     *
     * @return status
     */
    public static boolean isActivityRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = manager.getRunningTasks(Integer.MAX_VALUE);

        // iterate over activity list
        for (ActivityManager.RunningTaskInfo info : runningTaskInfoList) {
            if (info.topActivity.getPackageName().equals(AppConstants.ROOT_PACKAGE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check if service is running or not
     *
     * @return state
     */
    public static boolean isServiceRunning(Context context, Class<?> cls) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo ser : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(ser.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * generate notification
     */
    public void generateNotification(Activity activity) {
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_id = "notification.x.service";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channel_id, "notification monitoring", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(text);
                mNotificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity.getApplicationContext(), channel_id)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle(title_n)
                    .setContentText(text)
                    .setAutoCancel(true);
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
        } else {
            NotificationCompat.Builder ncomp = new NotificationCompat.Builder(activity);
            ncomp.setContentTitle(title_n);
            ncomp.setContentText(text);
            ncomp.setTicker("Notification Listener Service");
            ncomp.setSmallIcon(R.mipmap.app_icon);
            ncomp.setAutoCancel(true);
            mNotificationManager.notify((int) System.currentTimeMillis(), ncomp.build());
        }
    }
}
