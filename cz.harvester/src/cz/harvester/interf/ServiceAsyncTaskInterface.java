package cz.harvester.interf;

import cz.harvester.api.ApiFunc;

public interface ServiceAsyncTaskInterface<Activity extends ServiceActivityInterface> {
	void attach(Activity activity);
	void detach();
	Activity getActivity();
	ApiFunc getApiFunc();
	void executeTask();
	boolean isPending();
}
