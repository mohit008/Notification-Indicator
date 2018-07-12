package com.notification.indication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DisplayOffReceiver extends BroadcastReceiver {
    Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
        this.context = context.getApplicationContext();
		String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            if (!AppConstants.isServiceRunning(context,NotificationMonitorService.class)) {
                context.startService(new Intent(context, NotificationMonitorService.class));
            }
            NotificationMonitorService.display = true;
        }
	}
}
