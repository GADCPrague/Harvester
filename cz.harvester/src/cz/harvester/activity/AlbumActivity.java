package cz.harvester.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import cz.harvester.R;
import cz.harvester.adapter.CardsUniqueAdapter;
import cz.harvester.api.ApiFunc;
import cz.harvester.api.ApiParams.ApiGetAvailCardsParam;
import cz.harvester.api.ApiParams.ApiLoginParam;
import cz.harvester.api.ApiParams.ApiRegisterParam;
import cz.harvester.api.ApiResults.ApiCard;
import cz.harvester.api.ApiResults.ApiGetAvailCardsResult;
import cz.harvester.api.ApiResults.ApiLoginResult;
import cz.harvester.api.ApiResults.ApiRegisterResult;
import cz.harvester.data.Album;
import cz.harvester.data.Cards.Card;
import cz.harvester.data.Cards.CardUnique;
import cz.harvester.widget.ActionBar;
import cz.harvester.widget.ActionBar.ActionBarButtonType;
import cz.harvester.widget.ActionBar.OnActionBarButtonClickListener;
import cz.harvester.widget.AlbumPages;
import cz.harvester.widget.AlbumPages.OnCardClickListener;
import cz.harvester.widget.CardPage;
import cz.harvester.widget.ActionBar.ActionBarType;
import cz.harvester.widget.LoginForm;
import cz.harvester.widget.LoginForm.OnLoginButtonClickListener;
import cz.harvester.widget.LoginForm.OnRegisterButtonClickListener;
import cz.harvester.widget.Workspace;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

public class AlbumActivity extends ServiceActivity {
	public static final String BUNDLE_NICK = "nick";
	public static final String BUNDLE_PASS = "pass";
	
	private static final int PAGE_LOGIN = 0;
	private static final int PAGE_LOADING = 1;
	private static final int PAGE_NO_BUSES = 2;
	private static final int PAGE_BUSES_LIST = 3;
	
	private static final int CURRENT_TASK_ID = 1;
	
	private static final int DIALOG_SELECT_CARD_UNIQUE = 1;
	
	private ViewFlipper loginViewFlipper;
	private LoginForm loginForm;
	private AlbumPages albumGallery;
	
	private String nick;
	private String password;
	private ImmutableList<CardUnique> cardsUnique;
	
	private ProgressDialog progressDialog;
	private IAlbumAsyncTask currentTask;
	
	private CardsUniqueAdapter tempCardsToSelectAdapter;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.album_activity);
		
		super.setupActionbar(ActionBarType.NORMAL, "Harvester");
		
		this.albumGallery = (AlbumPages)findViewById(R.id.gallery);
		this.albumGallery.setOnCardClickListener(new OnCardClickListener() {
			@Override
			public void onCardClick(Card c) {
				if (c != null && c.getCardsIUnique().size() > 0) {
					if (c.getCardsIUnique().size() == 1) {
						showCardDetail(c.getCardsIUnique().get(0));
					}
					else {
						tempCardsToSelectAdapter.setCards(c.getCardsIUnique());
						showDialog(DIALOG_SELECT_CARD_UNIQUE);
					}
				}
				
			}
		});
		
		
		this.loginViewFlipper = (ViewFlipper)findViewById(R.id.login_view_flipper);
        this.loginForm = (LoginForm)findViewById(R.id.login_form);
        
        this.tempCardsToSelectAdapter = new CardsUniqueAdapter(this);
        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		this.nick = pref.getString(BUNDLE_NICK, null);
		this.password = pref.getString(BUNDLE_PASS, null);
        
        this.loginForm.setOnLoginButtonClickListener(new OnLoginButtonClickListener() {
			@Override
			public void onLoginButtonClick(String nick, String password) {
				loginAsync(nick, password);
			}
		});
        this.loginForm.setOnRegisterButtonClickListener(new OnRegisterButtonClickListener() {
			@Override
			public void onRegisterButtonClick(String nick, String password) {
				registerAsync(nick, password);
			}
		});
        
        ImmutableMap<Integer, Object> map = getLastNonConfigurationInstance();
		if (map != null && map.containsKey(CURRENT_TASK_ID)) {
			this.currentTask = (LoadCardsTask)map.get(CURRENT_TASK_ID);
			this.currentTask.attach(this);
			
			if (this.currentTask instanceof RegisterTask) {
				showProgressBar(0);
			}
			else if (this.currentTask instanceof LoginTask) {
				showProgressBar(2);
			}
			else if (this.currentTask instanceof GetAvailCardsTask) {
				showProgressBar(1);
			}
		}
		else {			
			if (!TextUtils.isEmpty(this.nick) && !TextUtils.isEmpty(this.password)) {
				loadCardsAsync(true);
				flipToPage(PAGE_LOADING);
			}
			else {
				flipToPage(PAGE_LOGIN);
			}
		}
	}
	
	@Override
	public void onRetainNonConfigurationInstance(ImmutableMap.Builder<Integer, Object> map) {
		super.onRetainNonConfigurationInstance(map);
		if (this.currentTask != null) {
			this.currentTask.detach();
			map.put(CURRENT_TASK_ID, this.currentTask);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {	
		case DIALOG_SELECT_CARD_UNIQUE: {
			AlertDialog.Builder builder = new AlertDialog.Builder(AlbumActivity.this);
			builder.setTitle("Select card:");
			builder.setAdapter(tempCardsToSelectAdapter,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CardUnique c = AlbumActivity.this.tempCardsToSelectAdapter.getItem(which);
						AlbumActivity.this.showCardDetail(c);
					}
				});
			return builder.create();
		}
		
		default: return super.onCreateDialog(id);
		}
	}
	
//	@Override
//	protected void onPrepareDialog(int id, Dialog dialog) {
//		switch (id) {		
//		case DIALOG_SELECT_CARD_UNIQUE: {			
//			if (this.ticketList.getBusPlanAndListResult() != null) {
//				TicketListItem ticket = this.ticketList.getTicketListItem(this.dialogTicketInd);
//				dialog.setTitle(getString(R.string.prompt_place_detail) + " " + (ticket.hasPlaceNumber() ? ticket.getIPlace() : "--"));
//				
//				this.detailTicketListAdapter.setItems(ImmutableList.of(ticket), this.getBusPlanAndListResult.getBusPlanAndListInfo().getAoRoute());
//			}
//			else
//				this.detailTicketListAdapter.clear();
//			break;
//		}
//		
//		default: super.onPrepareDialog(id, dialog);
//		}
//	}

	
	private void showProgressBar(int type) {
		if (this.progressDialog == null || !this.progressDialog.isShowing()) {
			final String t;
			switch (type) {
			case 0: t = "Registering..."; break;
			case 1: return;//t = "Loading available cards..."; break;
			case 2: t = "Logging in..."; break;
			default: throw new RuntimeException("showProgressBar");
			}
			
			this.progressDialog = ProgressDialog.show(this, "", t, false, false);
		}
	}
	
	private void dismissProgressBar() {
		if (this.progressDialog != null) {
			this.progressDialog.dismiss();
			this.progressDialog = null;
		}
	}
	
	private void flipToPage(int pageInd) {
		loginViewFlipper.setDisplayedChild(pageInd);
		switch (pageInd) {
		case PAGE_LOGIN:
		{
			ActionBar actionBar = setupActionbar(ActionBarType.NORMAL, "Harvester");
			actionBar.setOnActionBarButtonClickListener(null);
			
			this.nick = null;
			this.password = null;
			this.cardsUnique = null;
			break;
		}
			
		case PAGE_LOADING:			
		case PAGE_NO_BUSES:			
		case PAGE_BUSES_LIST: {
			ActionBar actionBar = setupActionbar(ActionBarType.NORMAL, "Harvester", ActionBarButtonType.LOGOUT, 
					ActionBarButtonType.ARROW_LEFT);
			actionBar.setOnActionBarButtonClickListener(new OnActionBarButtonClickListener() {
				@Override
				public void onClick(int buttonIndex) {
					switch (buttonIndex) {
					case 0: logout(); break;
					case 1: getAvailCardsAsync(); break;
					}
				}
			});
			break;
		}
		}
	}
	
	private void showCardDetail(CardUnique c) {
		if (c != null) {
	        Intent intent = new Intent(AlbumActivity.this, CardDetailActivity.class);
	        intent.putExtra(CardDetailActivity.BUNDLE_CARD_DETAIL, c);
	        AlbumActivity.this.startActivityForResult(intent, 10);
		}
	}
	
	private void registerAsync(String nick, String password) {
		if (currentTask == null) 
		{
			this.currentTask = new RegisterTask(new ApiRegisterParam(nick, password));
			this.currentTask.attach(this);
			this.currentTask.doExecute();
			showProgressBar(0);
		}
	}
	
	private void loginAsync(String nick, String password) {
		if (currentTask == null) 
		{
			this.currentTask = new LoginTask(new ApiLoginParam(nick, password));
			this.currentTask.attach(this);
			this.currentTask.doExecute();
			showProgressBar(2);
		}
	}
	
	private void logout() {
		if (this.currentTask == null) {
			this.nick = null;
			this.password = null;
			this.cardsUnique = null;
			Album.removeAllCards(this);
			
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString(BUNDLE_NICK, "");
			editor.putString(BUNDLE_PASS, "");
			editor.commit();
			
			flipToPage(PAGE_LOGIN);
		}
	}
	
	private void loadCardsAsync(boolean getAvailCardsAfter) {
		if (currentTask == null) 
		{
			this.currentTask = new LoadCardsTask(getAvailCardsAfter);
			this.currentTask.attach(this);
			this.currentTask.doExecute();
			flipToPage(PAGE_LOADING);
		}
	}
	
	private void getAvailCardsAsync() {
		if (currentTask == null && !TextUtils.isEmpty(this.nick) && !TextUtils.isEmpty(this.password) &&
			this.cardsUnique != null) 
		{
			ImmutableList.Builder<Integer> myCards = new ImmutableList.Builder<Integer>();
			for (CardUnique c : this.cardsUnique) {
				myCards.add(c.getUniqueId());
			}
			
			this.currentTask = new GetAvailCardsTask(new ApiGetAvailCardsParam(nick, password, myCards.build()));
			this.currentTask.attach(this);
			this.currentTask.doExecute();
			showProgressBar(1);
		}
	}
	
	
	 @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (resultCode) {
    	case 10: loadCardsAsync(false); break;
    	}
    }
	
	private void setConfirmedNickPass(String nick, String password) {
		this.nick = nick;
		this.password = password;
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(BUNDLE_NICK, nick);
		editor.putString(BUNDLE_PASS, password);
		editor.commit();
	}
	
	
	private interface IAlbumAsyncTask {
		void attach(AlbumActivity activity);
		void detach();
		void doExecute();
		AlbumActivity getActivity();
	}
	
	private static abstract class AlbumAsyncTask<Result> extends AsyncTask<Void, Void, Result> implements IAlbumAsyncTask {
		private AlbumActivity activity;
		
		@Override
		public void attach(AlbumActivity activity) {
			if (this.activity != null)
				throw new RuntimeException("ServiceAsyncTask|1");
			this.activity = activity;
		}
		
		@Override
		public void detach()  {
			if (this.activity == null)
				throw new RuntimeException("ServiceAsyncTask|2");
			this.activity = null;
		}
		
		@Override
		public void doExecute() {
			this.execute();
		}
		
		@Override
		public AlbumActivity getActivity() {
			return activity;
		}
		
		@Override
		protected final void onPostExecute(Result result) {
			if (this.activity != null) {
				this.activity.currentTask = null;
				this.activity.dismissProgressBar();
				onPostExecute(activity, result);
			}
		}
		
		protected abstract void onPostExecute(AlbumActivity activity, Result result);
	}
	
	private static class RegisterTask extends AlbumAsyncTask<ApiRegisterResult> {
		private final ApiRegisterParam param;
		
		public RegisterTask(ApiRegisterParam param) {
			this.param = param;
		}
		
		@Override
		protected ApiRegisterResult doInBackground(Void... params) {
			ApiFunc apiFunc = new ApiFunc(getActivity());
			return apiFunc.register(param);
		}
		
		@Override
		protected void onPostExecute(AlbumActivity activity, ApiRegisterResult result) {	
			if (result.getErrorCode() != 0) {
				activity.showWarningMessage(result.getErrorMsg());
			}
			else {
				activity.setConfirmedNickPass(result.getParam().getNick(), result.getParam().getPassword());
				Album.removeAllCards(activity);
				
				for (ApiCard c : result.getInitialCards()) {
					CardUnique n = new CardUnique(c);
					Album.writeSingleCard(activity, n);
				}
				
				activity.loadCardsAsync(false);
			}
		}
	}
	
	private static class LoginTask extends AlbumAsyncTask<ApiLoginResult> {
		private final ApiLoginParam param;
		
		public LoginTask(ApiLoginParam param) {
			this.param = param;
		}
		
		@Override
		protected ApiLoginResult doInBackground(Void... params) {
			ApiFunc apiFunc = new ApiFunc(getActivity());
			return apiFunc.login(param);
		}
		
		@Override
		protected void onPostExecute(AlbumActivity activity, ApiLoginResult result) {	
			if (result.getErrorCode() != 0) {
				activity.showWarningMessage(result.getErrorMsg());
			}
			else {
				activity.setConfirmedNickPass(result.getParam().getNick(), result.getParam().getPassword());
				activity.loadCardsAsync(true);
			}
		}
	}
	
	
	private static class LoadCardsTask extends AlbumAsyncTask<ImmutableList<CardUnique>> {
		private final boolean getAvailCardsAfter;
		
		public LoadCardsTask(boolean getAvailCardsAfter) {
			this.getAvailCardsAfter = getAvailCardsAfter;
		}
		
		@Override
		protected ImmutableList<CardUnique> doInBackground(Void... params) {
//			ImmutableList.Builder<Card> cards = new ImmutableList.Builder<Card>();
//			Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_image_01);
//			
//			for (int i = 0; i < 40; i++) {
//				cards.add(new Card(bmp, i, i, "", "", new ApiLocPoint(0, 0)));
//			}
//			return cards.build();
			
			return Album.readAllCards(getActivity());
		}
		
		@Override
		protected void onPostExecute(AlbumActivity activity, ImmutableList<CardUnique> cardsUnique) {
			activity.cardsUnique = cardsUnique;
			
			activity.albumGallery.setCards(cardsUnique);
			activity.flipToPage(PAGE_BUSES_LIST);
			if (getAvailCardsAfter)
				activity.getAvailCardsAsync();
		}
	}
	
	private static class GetAvailCardsTask extends AlbumAsyncTask<ApiGetAvailCardsResult> {
		private final ApiGetAvailCardsParam param;
		
		public GetAvailCardsTask(ApiGetAvailCardsParam param) {
			this.param = param;
		}
		
		@Override
		protected ApiGetAvailCardsResult doInBackground(Void... params) {
			ApiFunc apiFunc = new ApiFunc(getActivity());
			return apiFunc.getAvailCards(param);
		}
		
		@Override
		protected void onPostExecute(AlbumActivity activity, ApiGetAvailCardsResult result) {	
			if (result.getErrorCode() != 0) {
				activity.showWarningMessage(result.getErrorMsg());
			}
			else {
				if (result.getAvailCards().size() > 0) {					
					for (ApiCard c : result.getAvailCards()) {
						CardUnique n = new CardUnique(c);
						Album.writeSingleCard(activity, n);
					}
					activity.loadCardsAsync(false);
					
					
					activity.showWarningMessage("New cards where added to your album!");
				}
			}
		}
	}
}
