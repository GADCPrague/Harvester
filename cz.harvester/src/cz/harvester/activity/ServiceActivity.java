package cz.harvester.activity;

import com.google.common.collect.ImmutableMap;

import cz.harvester.api.ApiFunc;
import cz.harvester.helper.ServiceActivityHelper;
import cz.harvester.interf.ServiceActivityInterface;

import android.os.Bundle;

public class ServiceActivity extends BaseActivity implements ServiceActivityInterface {
	private final ServiceActivityHelper<ServiceActivity> rshelper = new ServiceActivityHelper<ServiceActivity>(this);
	
	public boolean isServiceBound() { return rshelper.isServiceBound(); }
	public ApiFunc getApiFunc() { return rshelper.getApiFunc(); }
	public boolean isConfigurationChanging() { return rshelper.isConfigurationChanging(); }
	@Override
	protected void onCreate (Bundle savedInstanceState) { super.onCreate(savedInstanceState); rshelper.onCreate(savedInstanceState); }
	@Override
	protected void onStart() { super.onStart(); rshelper.onStart(); }
    @Override
    protected void onDestroy() { super.onDestroy(); rshelper.onDestroy(); }
	public void onServiceConnected() { rshelper.onServiceConnected(); }
	public void onServiceDisconnected() { rshelper.onServiceDisconnected(); }
	public void addOnServiceConnectedListener(OnServiceConnectedListener l) { rshelper.addOnServiceConnectedListener(l); }
	public void addOnServiceDisconnectedListener(OnServiceDisconnectedListener l) { rshelper.addOnServiceDisconnectedListener(l); }
	@Override
	public final Object onRetainNonConfigurationInstance() { return rshelper.onRetainNonConfigurationInstance(); }
	public void onRetainNonConfigurationInstance(ImmutableMap.Builder<Integer, Object> map) { rshelper.onRetainNonConfigurationInstance(map); }
	@Override
	public final ImmutableMap<Integer, Object> getLastNonConfigurationInstance () { return rshelper.getLastNonConfigurationInstance(super.getLastNonConfigurationInstance());	}
}
