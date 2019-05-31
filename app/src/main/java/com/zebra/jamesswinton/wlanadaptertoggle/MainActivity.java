package com.zebra.jamesswinton.wlanadaptertoggle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

    // Start Background Service
    startService(new Intent(MainActivity.this, BackgroundService.class));
  }
}
