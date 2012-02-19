package cz.harvester.api;

import com.google.common.collect.ImmutableList;

public class ApiParams {
	public static abstract class ApiParam {
		private final String nick;
		private final String password;
		
		public ApiParam(String nick, String password) {
			this.nick = nick;
			this.password = password;
		}
		
		public String getNick() {
			return this.nick;
		}
		
		public String getPassword() {
			return this.password;
		}
		
		public String getUri() {
			return "http://harvester.circlegate.com/HarvesterService.svc/" +
				getUriFuncName() + "/?nick=" + nick + "&password=" + password;
		}
		
		public abstract String getUriFuncName();
	}
	
	public static class ApiRegisterParam extends ApiParam {
		public ApiRegisterParam(String nick, String password) {
			super(nick, password);
		}
		
		@Override
		public String getUriFuncName() {
			return "register";
		}
	}
	
	public static class ApiLoginParam extends ApiParam {
		public ApiLoginParam(String nick, String password) {
			super(nick, password);
		}
		
		@Override
		public String getUriFuncName() {
			return "login";
		}
	}
	
	public static class ApiGetAvailCardsParam extends ApiParam {
		private final ImmutableList<Integer> myCardIds;
		
		public ApiGetAvailCardsParam(String nick, String password, ImmutableList<Integer> myCardIds) {
			super(nick, password);
			this.myCardIds = myCardIds;
		}
		
		public ImmutableList<Integer> getMyCardIds() {
			return this.myCardIds;
		}
		
		@Override
		public String getUri() {
			StringBuilder ret = new StringBuilder(super.getUri());
			ret.append("&myCardIds=");
			for (int i = 0; i < myCardIds.size(); i++) {
				ret.append(myCardIds.get(i));
				if (i < myCardIds.size() - 1) {
					ret.append("~");
				}
			}
			return ret.toString();
		}
		
		@Override
		public String getUriFuncName() {
			return "getavailcards";
		}
	}
	
	public static class ApiSendCardsParam extends ApiParam {
		private final String userTo;
		private final ImmutableList<Integer> cardIds;
		
		public ApiSendCardsParam(String nick, String password, String userTo, ImmutableList<Integer> cardIds) {
			super(nick, password);
			this.userTo = userTo;
			this.cardIds = cardIds;
		}
		
		public String getUserTo() {
			return this.userTo;
		}
		
		public ImmutableList<Integer> getCardIds() {
			return this.cardIds;
		}
		
		@Override
		public String getUri() {
			StringBuilder ret = new StringBuilder(super.getUri());
			ret.append("&userTo=" + userTo);
			ret.append("&cardIds=");
			for (int i = 0; i < cardIds.size(); i++) {
				ret.append(cardIds.get(i));
				if (i < cardIds.size() - 1) {
					ret.append("~");
				}
			}
			return ret.toString();
		}
		
		@Override
		public String getUriFuncName() {
			return "sendcards";
		}
	}
}
