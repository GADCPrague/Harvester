package cz.harvester.interf;

import com.google.common.collect.ImmutableMap;

import cz.harvester.api.ApiFunc;

public interface ServiceActivityInterface {
	boolean isServiceBound();
	ApiFunc getApiFunc();
	boolean isConfigurationChanging();
	void onServiceConnected();
	void onServiceDisconnected();
	void addOnServiceConnectedListener(OnServiceConnectedListener l);
	void addOnServiceDisconnectedListener(OnServiceDisconnectedListener l);
	void onRetainNonConfigurationInstance(ImmutableMap.Builder<Integer, Object> map);

	public static interface OnServiceConnectedListener {
		void onServiceConnected();
	}
	
	public static interface OnServiceDisconnectedListener {
		void onServiceDisconnected();
	}
}
