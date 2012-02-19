package cz.harvester.interf;

import cz.harvester.widget.ActionBar;
import cz.harvester.widget.ActionBar.ActionBarButtonType;
import cz.harvester.widget.ActionBar.ActionBarType;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public interface BaseActivityInterface {	
	void onMinuteChanged();
	void onSettingsUpdated();
	ActionBar setupActionbar(ActionBarType type, String text, ActionBarButtonType... buttons);
	void goSettings();
	void addContextMenuListener(View v, ContextMenuListener l);
	MenuInflater getMenuInflater();
	void showErrorMessageAndExit(String message);
	void showWarningMessage(String message);
	
	public interface ContextMenuListener {
		void onCreateContextMenu(ContextMenu menu, View v);
		boolean onContextItemSelected(MenuItem item, View v);
	}
}
