package com.zebra.jamesswinton.wlanadaptertoggle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.zebra.jamesswinton.wlanadaptertoggle.App.EXIT_APP_INTENT_FILTER;

public class MainActivity extends AppCompatActivity {

  // Debugging
  private static final String TAG = "MainActivity";

  // Constants


  // Static Variables


  // Non-Static Variables


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Register Broadcast Receiver
    registerReceiver(mExitAppReceiver, new IntentFilter(EXIT_APP_INTENT_FILTER));

    // Start Background Service
    startService(new Intent(MainActivity.this, BackgroundService.class));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    // Unregister Receiver
    unregisterReceiver(mExitAppReceiver);
  }

  private BroadcastReceiver mExitAppReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      finish();
    }
  };
}
