package cz.harvester.helper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.common.collect.ImmutableMap;

import cz.harvester.R;
import cz.harvester.api.ApiFunc;
import cz.harvester.api.ApiService;
import cz.harvester.interf.ServiceActivityInterface;
import cz.harvester.interf.ServiceActivityInterface.OnServiceConnectedListener;
import cz.harvester.interf.ServiceActivityInterface.OnServiceDisconnectedListener;

public class ServiceActivityHelper<RSActivity extends Activity & ServiceActivityInterface> {
	private final RSActivity activity;
	private ServiceConnection<RSActivity> service;
	private boolean isConfigurationChanging = false;
	private final List<OnServiceConnectedListener> onServiceConnectedListeners = new ArrayList<OnServiceConnectedListener>();
	private final List<OnServiceDisconnectedListener> onServiceDisconnectedListeners = new ArrayList<OnServiceDisconnectedListener>();
	
	public ServiceActivityHelper(RSActivity activity) {
		this.activity = activity;
	}
	
	public boolean isServiceBound() {
		return service.isBound() && service.getApiFunc() != null;
	}
	
	public ApiFunc getApiFunc() {
		return service.getApiFunc();
	}
	
	public boolean isConfigurationChanging() {
		return isConfigurationChanging;
	}
	
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {        
        isConfigurationChanging = false;
        ImmutableMap<Integer, Object> map = (ImmutableMap<Integer, Object>)activity.getLastNonConfigurationInstance();
        if (map != null)
        	this.service = (ServiceConnection<RSActivity>)map.get(R.id.service_connection);
        else 
        	this.service = new ServiceConnection<RSActivity>();
        this.service.attach(activity);
        if (!service.isBound())
        	service.bind();
    }
	
	public void onStart() {
		isConfigurationChanging = false;
	}
    
    public void onDestroy() {
    	if (!isConfigurationChanging) {
    		service.unbind();
    	}
    }
		
	public void onServiceConnected() {
		for (OnServiceConnectedListener l : this.onServiceConnectedListeners) {
			l.onServiceConnected();
		}
	}
	
	public void onServiceDisconnected() {
		for (OnServiceDisconnectedListener l : this.onServiceDisconnectedListeners) {
			l.onServiceDisconnected();
		}
	}
	
	public void addOnServiceConnectedListener(OnServiceConnectedListener l) {
		this.onServiceConnectedListeners.add(l);
	}
	
	public void addOnServiceDisconnectedListener(OnServiceDisconnectedListener l) {
		this.onServiceDisconnectedListeners.add(l);
	}
	
	public final Object onRetainNonConfigurationInstance () {
		this.isConfigurationChanging = true;
		ImmutableMap.Builder<Integer, Object> map = new ImmutableMap.Builder<Integer, Object>(); 
		activity.onRetainNonConfigurationInstance(map);
		return map.build();
	}
	
	public void onRetainNonConfigurationInstance(ImmutableMap.Builder<Integer, Object> map) {
		map.put(R.id.service_connection, this.service);
	}
	
	@SuppressWarnings("unchecked")
	public final ImmutableMap<Integer, Object> getLastNonConfigurationInstance (Object o) {
		if (o != null)
			return (ImmutableMap<Integer, Object>)o;
		else
			return null;
	}	

	private static class ServiceConnection<RSActivity extends Activity & ServiceActivityInterface> implements android.content.ServiceConnection {
		private RSActivity activity = null;
		private boolean bound = false;
		private ApiFunc apiFunc;
		
		public void attach(RSActivity activity) {
			this.activity = activity;
		}
		
		public void bind() {
			if (!bound) {
				Intent intent = new Intent(activity, ApiService.class);
	            activity.getApplicationContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
			}
		}
		
		public void unbind() {
			if (bound) {
				activity.getApplicationContext().unbindService(this);
				this.bound = false;
			}
		}
		
		public void onServiceConnected(ComponentName className, IBinder binder) {
			this.apiFunc = (ApiFunc)binder;
            bound = true;
			activity.onServiceConnected();
        }

        public void onServiceDisconnected(ComponentName arg0) {
        	bound = false;
        	activity.onServiceDisconnected();
        }
        
        public boolean isBound() {
        	return bound;
        }
        
        public ApiFunc getApiFunc() {
        	return apiFunc;
        }
	}
}
