package cz.harvester.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import cz.harvester.R;
import cz.harvester.adapter.CardsUniqueAdapter;
import cz.harvester.api.ApiFunc;
import cz.harvester.api.ApiParams.ApiGetAvailCardsParam;
import cz.harvester.api.ApiParams.ApiLoginParam;
import cz.harvester.api.ApiParams.ApiRegisterParam;
import cz.harvester.api.ApiParams.ApiSendCardsParam;
import cz.harvester.api.ApiResults.ApiCard;
import cz.harvester.api.ApiResults.ApiGetAvailCardsResult;
import cz.harvester.api.ApiResults.ApiLoginResult;
import cz.harvester.api.ApiResults.ApiRegisterResult;
import cz.harvester.api.ApiResults.ApiSendCardsResult;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class CardDetailActivity extends ServiceActivity {
	public static String BUNDLE_CARD_DETAIL = "cardDetail";
	
	private CardUnique card;
	
	String nick;
	String password;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.card_detail_activity);
		
		super.setupActionbar(ActionBarType.NORMAL, "Nine Wonders of the World");
		
	  SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		this.nick = pref.getString(AlbumActivity.BUNDLE_NICK, null);
		this.password = pref.getString(AlbumActivity.BUNDLE_PASS, null);
		
		this.card = (CardUnique)getIntent().getExtras().get(BUNDLE_CARD_DETAIL);
		
		ImageView pic = (ImageView)findViewById(R.id.picture);
		TextView title = (TextView)findViewById(R.id.title);
		
		TextView number = (TextView)findViewById(R.id.number);
		TextView value = (TextView)findViewById(R.id.value);
		TextView gotFrom = (TextView)findViewById(R.id.got_from);
		TextView gotWhen = (TextView)findViewById(R.id.got_when);
		
		pic.setImageBitmap(card.getPicture());
		title.setText(card.getName());
		
		number.setText(card.getName());
		//title.setText(card.get);
		gotFrom.setText(card.getGotFrom());
		gotWhen.setText(card.getGotWhen().toString("dd.MM.yyyy"));
		
		Button btn = (Button)findViewById(R.id.send_button);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(CardDetailActivity.this);
	        	AlertDialog dg = new AlertDialog.Builder(CardDetailActivity.this)
	            	.setTitle("Select nickname:")
	            	//.setMessage(R.string.prompt_enter_promo_code_message)
	            	.setView(input)
	            	.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	            		public void onClick(DialogInterface dialog, int whichButton) {
	            			ApiFunc f = new ApiFunc(CardDetailActivity.this);
	        				ApiSendCardsResult result = f.sendCards(new ApiSendCardsParam(nick, password, input.getText().toString(), ImmutableList.of(card.getUniqueId())));
	        				
	        				if (result.getErrorCode() != 0) {
	        					showWarningMessage(result.getErrorMsg());
	        				}
	        				else {
	        					Album.removeCard(CardDetailActivity.this, card.getUniqueId());
	        					CardDetailActivity.this.setResult(10);
	        					CardDetailActivity.this.finish();
	        				}
	            		}
	            	}).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	            		public void onClick(DialogInterface dialog, int whichButton) {
	            			// 	Do nothing.
	            		}
	            	}).create();
	        	dg.show();
			}
		});
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {	
		
		default: return super.onCreateDialog(id);
		}
	}
}
