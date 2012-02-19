package cz.harvester.helper;

import java.util.HashMap;

import cz.harvester.R;
import cz.harvester.interf.BaseActivityInterface;
import cz.harvester.interf.BaseActivityInterface.ContextMenuListener;
import cz.harvester.widget.ActionBar;
import cz.harvester.widget.ActionBar.ActionBarButtonType;
import cz.harvester.widget.ActionBar.ActionBarType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

/**
 * @author Honza
 *
 * @param <BActivity>
 */
public class BaseActivityHelper<BActivity extends Activity & BaseActivityInterface> {
	private static final String BUNDLE_KEY_LAST_ERROR_MESSAGE = "BaseActivityHelper|lastErrorMessage";
	private static final String BUNDLE_KEY_LAST_WARNING_MESSAGE = "BaseActivityHelper|lastWarningMessage";
	
	private static final int DIALOG_ERROR_MESSAGE = 65427843;
	private static final int DIALOG_WARNING_MESSAGE = 65427844;
	
	private static final int REQUEST_CODE_SETTINGS = 4531563;
	
	private final BActivity activity;
	private final IntentFilter minuteChangedIntentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
	private final HashMap<View, ContextMenuListener> contextMenuListeners = new HashMap<View, ContextMenuListener>();
	private View lastOnCreateContextMenuView;
	private String lastErrorMessage = "";
	private String lastWarningMessage = "";
	
	public BaseActivityHelper(BActivity activity) {
		this.activity = activity;
	}
	
	public void onCreate(Bundle savedInstanceState) {	
		if (savedInstanceState != null) {
			this.lastErrorMessage = savedInstanceState.getString(BUNDLE_KEY_LAST_ERROR_MESSAGE);
			this.lastWarningMessage = savedInstanceState.getString(BUNDLE_KEY_LAST_WARNING_MESSAGE);
		}
	}
	
	public void onResume() {
		activity.registerReceiver(minuteChangedBroadcastReceiver, minuteChangedIntentFilter);
		activity.onMinuteChanged();
	}
	
	public void onPause() {
		try {
			activity.unregisterReceiver(minuteChangedBroadcastReceiver);
		}
		catch (Exception ex) {}
	}
	
	public void onSaveInstanceState (Bundle outState) {
		outState.putString(BUNDLE_KEY_LAST_ERROR_MESSAGE, lastErrorMessage);
		outState.putString(BUNDLE_KEY_LAST_WARNING_MESSAGE, lastWarningMessage);
	}
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        case REQUEST_CODE_SETTINGS: activity.onSettingsUpdated(); break;
	    }
	}
	
    public void addContextMenuListener(View v, ContextMenuListener l) {
    	activity.registerForContextMenu(v);
    	if (contextMenuListeners.containsKey(v))
    		contextMenuListeners.remove(v);
    	this.contextMenuListeners.put(v, l);
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	this.lastOnCreateContextMenuView = v;
    	if (contextMenuListeners.containsKey(v)) {
    		contextMenuListeners.get(v).onCreateContextMenu(menu, v);
    	}
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	if (lastOnCreateContextMenuView != null && contextMenuListeners.containsKey(lastOnCreateContextMenuView)) {
    		return contextMenuListeners.get(lastOnCreateContextMenuView).onContextItemSelected(item, lastOnCreateContextMenuView);
    	}
    	else
    		return false;
    }
	
    public Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ERROR_MESSAGE: 
			if (lastErrorMessage != null && lastErrorMessage.length() > 0) {
				AlertDialog.Builder b = new AlertDialog.Builder(activity)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(R.string.error)
		        .setMessage(lastErrorMessage)
		        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();						
					}
				})
				.setCancelable(false);
		        return b.create();
			}
			
		case DIALOG_WARNING_MESSAGE: 
			if (lastWarningMessage != null && lastWarningMessage.length() > 0) {
				AlertDialog.Builder b = new AlertDialog.Builder(activity)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(R.string.warning)
		        .setMessage(lastWarningMessage)
		        .setPositiveButton(android.R.string.ok, null)
				.setCancelable(false);
		        return b.create();
			}
			
		default: return null;
		}
	}
    
    public void onPrepareDialog (int id, Dialog dialog) {
    	switch (id) {
    	case DIALOG_ERROR_MESSAGE:
    		if (lastErrorMessage != null) {
    			AlertDialog d = (AlertDialog)dialog;
    			d.setMessage(lastErrorMessage);
    		}
    		break;
    		
    	case DIALOG_WARNING_MESSAGE:
    		if (lastWarningMessage != null) {
    			AlertDialog d = (AlertDialog)dialog;
    			d.setMessage(lastWarningMessage);
    		}
    		break;
    	}
    }
    
	// Sets up actionbar, except the OnActionbarButtonClickListener
	public ActionBar setupActionbar(ActionBarType type, String text, ActionBarButtonType... buttons) {
		ActionBar actionbar = (ActionBar)activity.findViewById(R.id.actionbar); 
        actionbar.setActionBarType(type);
        actionbar.setActionBarText(text);
        
        for (int i = 0; i < buttons.length; i++) {
        	actionbar.setActionBarButtonType(i, buttons[i]);
        }
        for (int i = buttons.length; i < actionbar.getButtonsCapacity(); i++) {
        	actionbar.setActionBarButtonType(i, ActionBarButtonType.NONE);
        }
        
//        if (type == ActionBarType.NORMAL) {
//        	actionbar.setOnActionBarHomeClickListener(new OnActionBarHomeClickListener() {
//    			public void onClick() {
//    				goHome();
//    			}
//    		});
//        }
        
        actionbar.setTextNarrowPaddings(buttons.length >= 3);
        return actionbar;
	}
		
	public void goSettings() {
//		if (activity instanceof SettingsActivity) {
//            return;
//        }
//
//        final Intent intent = new Intent(activity, SettingsActivity.class);
//        activity.startActivityForResult(intent, REQUEST_CODE_SETTINGS);        
	}	
	

	/** Zobrazí chybovou hlášku a skonèí aktivitu
	 * @param message Pokud je prázdná (null nebo prázdný string), použije se string R.string.fatal_error
	 */
	public void showErrorMessageAndExit(String message) {
		this.lastErrorMessage = TextUtils.isEmpty(message) ? activity.getString(R.string.fatal_error) : message;
		activity.showDialog(DIALOG_ERROR_MESSAGE);
	}
	
	/** Zobrazí varovnou hlášku
	 * @param message Pokud je prázdná (null nebo prázdný string), použije se string R.string.unknown_warning
	 */
	public void showWarningMessage(String message) {
		this.lastWarningMessage = TextUtils.isEmpty(message) ? activity.getString(R.string.unknown_warning) : message;
		activity.showDialog(DIALOG_WARNING_MESSAGE);
	}
	
	private final BroadcastReceiver minuteChangedBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			activity.onMinuteChanged();
		}
	};
}
