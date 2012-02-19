package cz.harvester.activity;

import cz.harvester.helper.BaseActivityHelper;
import cz.harvester.interf.BaseActivityInterface;
import cz.harvester.widget.ActionBar;
import cz.harvester.widget.ActionBar.ActionBarButtonType;
import cz.harvester.widget.ActionBar.ActionBarType;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public abstract class BaseActivity extends Activity implements BaseActivityInterface {
	private final BaseActivityHelper<BaseActivity> bshelper = new BaseActivityHelper<BaseActivity>(this);
		
	@Override
	protected void onCreate (Bundle savedInstanceState) { super.onCreate(savedInstanceState); bshelper.onCreate(savedInstanceState); }
	@Override
	protected void onResume() {	super.onResume(); bshelper.onResume(); }
	@Override
	protected void onPause() { super.onPause();	bshelper.onPause(); }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { super.onActivityResult(requestCode, resultCode, data); bshelper.onActivityResult(requestCode, resultCode, data); }
	@Override
	protected void onSaveInstanceState (Bundle outState) { super.onSaveInstanceState(outState); bshelper.onSaveInstanceState(outState); }
	public void addContextMenuListener(View v, ContextMenuListener l) { bshelper.addContextMenuListener(v, l);}
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { super.onCreateContextMenu(menu, v, menuInfo); bshelper.onCreateContextMenu(menu, v, menuInfo); }
    @Override
    public boolean onContextItemSelected(MenuItem item) { if (bshelper.onContextItemSelected(item)) return true; else return super.onContextItemSelected(item); }
    @Override
    protected Dialog onCreateDialog(int id) { Dialog d = bshelper.onCreateDialog(id); return d == null ? super.onCreateDialog(id) : d; }
    @Override
    protected void onPrepareDialog (int id, Dialog dialog) { super.onPrepareDialog(id, dialog); bshelper.onPrepareDialog(id, dialog); }
    @Override
    public void onMinuteChanged() {}
    @Override
	public void onSettingsUpdated() { }
	public ActionBar setupActionbar(ActionBarType type, String text, ActionBarButtonType... buttons) { return bshelper.setupActionbar(type, text, buttons); }	
	public void goSettings() { bshelper.goSettings(); }	
	public void showErrorMessageAndExit(String message) { bshelper.showErrorMessageAndExit(message); }
	public void showWarningMessage(String message) { bshelper.showWarningMessage(message); }
}
 