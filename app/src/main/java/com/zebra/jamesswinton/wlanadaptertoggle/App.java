package com.zebra.jamesswinton.wlanadaptertoggle;

import android.app.Application;
import android.util.Log;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKManager.FEATURE_TYPE;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.EMDKResults.STATUS_CODE;
import com.symbol.emdk.ProfileManager;

public class App extends Application implements EMDKListener {

  // Debugging
  public static final boolean DEBUGGING = false;
  private static final String TAG = "ApplicationClass";

  // Constants


  // Static Variables


  // Non-Static Variables
  private EMDKManager mEmdkManager;
  public static ProfileManager mProfileManager = null;

  @Override
  public void onCreate() {
    super.onCreate();

    // Init EMDK
    EMDKResults emdkManagerResults = EMDKManager.getEMDKManager(this, this);

    // Verify EMDK Manager
    if (emdkManagerResults == null || emdkManagerResults.statusCode != STATUS_CODE.SUCCESS) {
      // Log Error
      Log.e(TAG, "onCreate: Failed to get EMDK Manager -> " +
          (emdkManagerResults == null ? "No Results Returned" : emdkManagerResults.statusCode));
    }
  }

  /**
   * EMDK Manager Callback - Fired when EMDK Manager is available.
   * @param emdkManager -> EMDK Manager Instance
   */
  @Override
  public void onOpened(EMDKManager emdkManager) {
    // Assign EMDK Reference
    mEmdkManager = emdkManager;

    // Get Profile & Version Manager Instances
    mProfileManager = (ProfileManager) mEmdkManager.getInstance(FEATURE_TYPE.PROFILE);
  }

  /**
   * EMDK Manage Callback - Fired when EMDK Manager is no longer available
   */
  @Override
  public void onClosed() {
    // Release EMDK Manager Instance
    if (mEmdkManager != null) {
      mEmdkManager.release();
      mEmdkManager = null;
    }
  }

}
