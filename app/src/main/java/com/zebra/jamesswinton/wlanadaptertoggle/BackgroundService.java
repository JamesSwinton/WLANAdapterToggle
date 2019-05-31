package com.zebra.jamesswinton.wlanadaptertoggle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager.PROFILE_FLAG;

public class BackgroundService extends Service {

  // Debugging
  private static final String TAG = "BackgroundService";

  // Constants
  private static final int BACKGROUND_SERVICE_NOTIFICATION = 1;

  // Static Variables


  // Non-Static Variables

  @Override
  public void onCreate() {
    super.onCreate();

    // Register Screen on/off receiver
    IntentFilter screenStateFilter = new IntentFilter();
    screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
    screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
    registerReceiver(screenStateReceiver, screenStateFilter);

    // IDK ???
    Intent notificationIntent = new Intent(this, MainActivity.class);
    PendingIntent.getActivity(this, 0, notificationIntent, 0);

    // Start Service
    startForeground(BACKGROUND_SERVICE_NOTIFICATION, createServiceNotification());
  }

  private Notification createServiceNotification() {
    // Create Variables
    String channelId = "com.zebra.wlantoggler";
    String channelName = "Custom Background Notification Channel";

    // Create Channel
    NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName,
        NotificationManager.IMPORTANCE_NONE);
    notificationChannel.setLightColor(Color.BLUE);
    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

    // Set Channel
    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    manager.createNotificationChannel(notificationChannel);

    // Build Notification
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
        channelId);

    // Return Build Notification object
    return notificationBuilder
        .setContentTitle("WLAN Toggle Active")
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setCategory(Notification.CATEGORY_SERVICE)
        .setPriority(NotificationManager.IMPORTANCE_MIN)
        .setOngoing(true)
        .build();
  }

  //Broadcast Receiver for Screen Off
  BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get Action
      String intentAction = intent.getAction();

      if (intentAction != null) {
        // Handle Screen Intents
        if (intentAction.equals(Intent.ACTION_SCREEN_ON)) {
          Log.i(TAG, "Screen State Changed: On");
          toggleWlan(true);
        } else if (intentAction.equals(Intent.ACTION_SCREEN_OFF)) {
          Log.i(TAG, "Screen State Changed: Off");
          toggleWlan(false);
        }
      } else {
        Log.e(TAG, "Intent Action was Null");
      }
    }
  };

  private void toggleWlan(boolean enable) {
    String[] toggleNFCParams = new String[1];
    toggleNFCParams[0] =
          "<wap-provisioningdoc>\n"
        + "  <characteristic type=\"Profile\">\n"
        + "    <parm name=\"ProfileName\" value=\"ToggleWLAN\"/>\n"
        + "    <characteristic type=\"Wi-Fi\" version=\"4.3\">\n"
        + "      <parm name=\"emdk_name\" value=\"\"/>\n"
        + "      <characteristic type=\"System\">\n"
        + "        <parm name=\"WiFiAction\" value=\"" + (enable ? "enable" : "disable") + "\"/>\n"
        + "      </characteristic>\n"
        + "    </characteristic>\n"
        + "  </characteristic>\n"
        + "</wap-provisioningdoc>";

    new ProcessProfile().execute(toggleNFCParams);
  }

  private static class ProcessProfile extends AsyncTask<String, Void, EMDKResults> {

    @Override
    protected EMDKResults doInBackground(String... params) {
      // Execute Profile
      return App.mProfileManager.processProfile("ToggleWLAN", PROFILE_FLAG.SET, params);
    }

    @Override
    protected void onPostExecute(EMDKResults results) {
      super.onPostExecute(results);
      // Log Result
      Log.i(TAG, "Profile Manager Result: " + results.statusCode
          + " | " + results.extendedStatusCode);

      Log.i(TAG, results.getStatusString());
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    // Unregister Receiver
    unregisterReceiver(screenStateReceiver);
  }
}
