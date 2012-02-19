package cz.harvester.api;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.common.collect.ImmutableList;

import cz.harvester.api.ApiParams.ApiParam;

public class ApiResults {
	public static abstract class ApiResult {
		public static final int NO_ERROR_CODE = 0;
		public static final int FATAL_ERROR = 1;
		
		private final ApiParam param;
		private final DateTime resultDateTime;
		private final int errorCode;
		private final String errorMsg;
		
		public ApiResult(ApiParam param, DateTime resultDateTime, int errorCode, String errorMsg) {
			this.param = param;
			this.resultDateTime = resultDateTime;
			this.errorCode = errorCode;
			this.errorMsg = errorMsg;
		}
		
		public ApiResult(ApiParam param, int errorCode, String errorMsg) {
			this.param = param;
			this.resultDateTime = new DateTime();
			this.errorCode = errorCode;
			this.errorMsg = errorMsg;
		}
		
		public ApiResult(ApiParam param, JSONObject j) {
			try {
				this.param = param;
				this.resultDateTime = new DateTime();
				this.errorCode = j.getInt("ErrorCode");
				this.errorMsg = j.optString("ErrorMsg");
			}
			catch (JSONException ex) {
				throw new RuntimeException("ApiResult");
			}
		}
		
		public ApiParam getParam() {
			return this.param;
		}
		
		public DateTime getResultDateTime() {
			return this.resultDateTime;
		}
		
		public int getErrorCode() {
			return this.errorCode;
		}
		
		public String getErrorMsg() {
			return this.errorMsg;
		}
	}
	
	public static class ApiRegisterResult extends ApiResult {
		private final ImmutableList<ApiCard> initialCards;
		
		public ApiRegisterResult(ApiParam param, DateTime resultDateTime, int errorCode, String errorMsg, ImmutableList<ApiCard> initialCards) {
			super(param, resultDateTime, errorCode, errorMsg);
			this.initialCards = initialCards;
		}
		
		public ApiRegisterResult(ApiParam param, int errorCode, String errorMsg) {
			super(param, errorCode, errorMsg);
			if (errorCode == 0)
				throw new RuntimeException();
			this.initialCards = ImmutableList.of();
		}
		
		public ApiRegisterResult(ApiParam param, JSONObject j) {
			super(param, j);
			try {
				JSONArray a = j.getJSONArray("InitialCards");
				ImmutableList.Builder<ApiCard> tmp = new ImmutableList.Builder<ApiCard>();
				for (int i = 0; i < a.length(); i++) {
					tmp.add(new ApiCard(a.getJSONObject(i)));
				}
				this.initialCards = tmp.build();
			}
			catch (JSONException ex) {
				throw new RuntimeException("ApiResult");
			}
		}
		
		public ImmutableList<ApiCard> getInitialCards() {
			return this.initialCards;
		}
	}
	
	public static class ApiLoginResult extends ApiResult {
		public ApiLoginResult(ApiParam param, DateTime resultDateTime, int errorCode, String errorMsg) {
			super(param, resultDateTime, errorCode, errorMsg);
		}
		
		public ApiLoginResult(ApiParam param, int errorCode, String errorMsg) {
			super(param, errorCode, errorMsg);
			if (errorCode == 0)
				throw new RuntimeException();
		}
		
		public ApiLoginResult(ApiParam param, JSONObject j) {
			super(param, j);
		}
	}

	public static class ApiGetAvailCardsResult extends ApiResult {
		private final ImmutableList<ApiCard> availCards;
		
		public ApiGetAvailCardsResult(ApiParam param, DateTime resultDateTime, int errorCode, String errorMsg, ImmutableList<ApiCard> availCards) {
			super(param, resultDateTime, errorCode, errorMsg);
			this.availCards = availCards;
		}
		
		public ApiGetAvailCardsResult(ApiParam param, int errorCode, String errorMsg) {
			super(param, errorCode, errorMsg);
			if (errorCode == 0)
				throw new RuntimeException();
			this.availCards = ImmutableList.of();
		}
		
		public ApiGetAvailCardsResult(ApiParam param, JSONObject j) {
			super(param, j);
			try {
				JSONArray a = j.getJSONArray("AvailCards");
				ImmutableList.Builder<ApiCard> tmp = new ImmutableList.Builder<ApiCard>();
				for (int i = 0; i < a.length(); i++) {
					tmp.add(new ApiCard(a.getJSONObject(i)));
				}
				this.availCards = tmp.build();
			}
			catch (JSONException ex) {
				throw new RuntimeException("ApiResult");
			}
		}
		
		public ImmutableList<ApiCard> getAvailCards() {
			return this.availCards;
		}
	}
	
	public static class ApiSendCardsResult extends ApiResult {
		public ApiSendCardsResult(ApiParam param, DateTime resultDateTime, int errorCode, String errorMsg) {
			super(param, resultDateTime, errorCode, errorMsg);
		}
		
		public ApiSendCardsResult(ApiParam param, int errorCode, String errorMsg) {
			super(param, errorCode, errorMsg);
			if (errorCode == 0)
				throw new RuntimeException();
		}
		
		public ApiSendCardsResult(ApiParam param, JSONObject j) {
			super(param, j);
		}
	}
	
	public static class ApiCard {
		private final int uniqueId;
		private final int cardId;
		private final int index;
		private final int type;
		private final String gotFrom;
		private final String name;
		private final String text;
		private final Bitmap picture;
		
		public ApiCard(int uniqueId, int cardId, int index, int type, String gotFrom, String name, String text, Bitmap picture) {
			this.uniqueId = uniqueId;
			this.cardId = cardId;
			this.index = index;
			this.type = type;
			this.gotFrom = gotFrom;
			this.name = name;
			this.text = text;
			this.picture = picture;
		}
		
		public ApiCard(JSONObject j) {
			try {
				this.uniqueId = j.getInt("UniqueId");
				this.cardId = j.getInt("CardId");
				this.index = j.getInt("Index");
				this.type = j.getInt("Type");
				this.gotFrom = j.getString("GotFrom");
				this.name = j.getString("Name");
				this.text = j.getString("Text");
				
				String str = j.getString("Picture");
				byte[] bytes = Base64.decode(str, Base64.DEFAULT);
				this.picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			}
			catch (JSONException ex) {
				throw new RuntimeException("ApiResult");
			}
		}
		
		public int getUniqueId() {
			return this.uniqueId;
		}
		
		public int getCardId() {
			return this.cardId;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public int getType() {
			return this.type;
		}
		
		public String getGotFrom() {
			return this.gotFrom;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getText() {
			return this.text;
		}
		
		public Bitmap getPicture() {
			return this.picture;
		}
	}
}
