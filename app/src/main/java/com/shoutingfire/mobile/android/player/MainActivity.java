/**
 * Transient foreground class which starts/stops/queries the player.
 *
 * Copyright (c) 2011, 2018 bmir.org and shoutingfire.com
 */
package com.shoutingfire.mobile.android.player;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.shoutingfire.mobile.android.player.Constants;

public class MainActivity extends Activity {

	/**
	 * Commands to this service.
	 * Note: This must match definitions in AndroidManifest.xml
	 */
	public static final String ACTION_IMAGE = Constants.PACKAGE_NAME + ".mainactivity.action.IMAGE";

	/**
	 * Logs messages to the console.
     * Enable/disable logging here when publishing to Android Market.
	 */
	private static final String _logTag = MainActivity.class.getName().toString();
	private static void sop(String method, String message) {
		Log.d(_logTag, method + ": " + message);
	}

	/**
	 * A thread object to query the current song.
	 */
	NowPlayingThread _nowPlayingThread = null;

    /**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    String m = "onCreate";
	    super.onCreate(savedInstanceState);
	    sop(m,"Entry.");

	    // Paint the screen.
	    setContentView(R.layout.main);

	    // Create a thread object to query the current song.
	    _nowPlayingThread = new NowPlayingThread(this, this);

	 	sop(m,"Exit.");
	}

	/**
	 * Handles onClick for the play/stop ImageButton.
	 */
	public void handleButton(View view) {
	 	String m = "handleButton";
	 	sop(m,"Entry. Calling MediaPlayerService...");

	    boolean productionMode = true;

	 	if (productionMode) {
	 	 	// Inform the player service that the user clicked the button.
	 	 	// Note: If the service has already been started, the running service receives this intent.
	 	 	//startService(new Intent(PlayerService.ACTION_BUTTON));
			Intent intent = new Intent(PlayerService.ACTION_BUTTON);
			sop(m,"===> ANDY MA 77 <===");
			intent.setPackage(this.getPackageName());
			startService(intent);

		}
	 	else {
	 		// Start the music stream inline.  For debug purposes only.
	 		debugStartMusic();
	 	}

	 	sop(m,"Exit");
    }

    @Override
	protected void onStop() {
	    String m = "onStop";
		sop(m,"Entry");
	    super.onStop();
	    sop(m,"Exit");
    }

	@Override
	protected void onDestroy() {
		String m = "onDestroy";
		sop(m,"Entry");
	    super.onDestroy();
	    sop(m,"Exit");
	}

	/**
	 * This broadcast receiver listens for state changes from the player service
	 * and changes the image on the button accordingly.
	 */
    private BroadcastReceiver _mainActivityBroadcastReceiver =
        new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String m = "BroadcastReceiver/onReceive";
				sop(m,"Entry.");

				// 2011-1213 Tolerate null intent.  Added prophylactically, not actually reported by a user.
				if (null == intent) {
					sop(m,"Early exit. intent is unexpectedly null. Nothing to do.");
					return;
				}

				// Access the play/stop button.
				ImageButton imageButton = (ImageButton)findViewById(R.id.playStopButton);

				// Change the image corresponding to the state of the player service.
				String state = intent.getExtras().getString(PlayerService.STATE_KEY);
				if (PlayerService.STATE_PLAYING.equals(state) ||
					PlayerService.STATE_PAUSED.equals(state)) {
					sop(m,"Setting button image to 'stop'.");
					imageButton.setImageResource(Constants.IMG_STOP);
					if (null != _nowPlayingThread) {
					    sop(m,"Starting nowPlayingThread.");
			            _nowPlayingThread.go();
					}
				}
				else if (PlayerService.STATE_PREPARING.equals(state)) {
					sop(m,"Setting button image to 'dots'.");
					imageButton.setImageResource(Constants.IMG_DOTS);
				}
				else if (PlayerService.STATE_STOPPED.equals(state)) {
					sop(m,"Setting button image to 'play'.");
					imageButton.setImageResource(Constants.IMG_PLAY);
				}
				else {
					sop(m, "ERROR: Unrecognized state value: " + state);
				}
				sop(m,"Exit.");
			}
		};

	/**
	 * Registers the broadcastReceiver to receive state changes from the running service when this activity activates.
	 */
    public void onResume() {
    	String m = "onResume";
    	sop(m,"Entry.");
        super.onResume();

        sop(m,"Enabling the service to notify this application of state changes.");
        registerReceiver(_mainActivityBroadcastReceiver, new IntentFilter(ACTION_IMAGE));

        sop(m,"Requesting status from the service in order to update the MainActivity button image.");
 	 	// Note: If the service has already been started, the running service receives this intent.
		Intent intent = new Intent(PlayerService.ACTION_STATUS);
		sop(m,"===> ANDY MA 160 <===");
		intent.setPackage(this.getPackageName());
 	 	startService(intent);

 	 	sop(m,"Exit.");
    }

	/**
	 * Unregisters the broadcastReceiver from receiving state changes from the running service when this activity deactivates.
	 */
    public void onPause() {
    	String m = "onPause";
        super.onPause();

        sop(m,"Disabling the service from notifying this application of state changes.");
        unregisterReceiver(_mainActivityBroadcastReceiver);
    }

	/**
	 * Generates a toast message to the user.
	 */
	private void postToast(String message) {
		String m = "postToast";
		sop(m,"Entry. message=" + message);
		String app_name = Constants.APP_NAME_MIXED;
		Toast.makeText(this, app_name + ": " + message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Checks whether this app has its required Android Permissions.
	 */
	private boolean hasPermissions() {
		String m = "hasPermissions";
		Context context = this;
		//boolean rc = context.checkPermission("b", Process.myPid(), Process.myUid());
		if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_CHECKIN_PROPERTIES)) {
			sop(m,"Has checkin permission");
		}
		return true;
	}

	/**
     * Simple synchronous example, for debug only.  Not for production use.
     * From http://developer.android.com/guide/topics/media/mediaplayer.html
     * To stop, go to Settings-> Applications-> Manage Applications-> appname-> Stop application.
     */
    private void debugStartMusic() {
    	String m = "debugStartMusic";
    	sop(m,"Entry.");

		String url = PlayerService.getMediaURLString();
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			sop(m,"Setting data source.");
			mediaPlayer.setDataSource(url);
			sop(m,"Calling prepare.");
			mediaPlayer.prepare();
		}
		catch(Exception e) {
			sop(m,"ERROR: Caught expection e=" + e.getMessage());
		}
		sop(m,"Calling start.");
        mediaPlayer.start();

        sop(m,"Exit.");
    }
}
