package cz.harvester.data;

import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;

import cz.harvester.api.ApiBase.ApiObject;
import cz.harvester.api.ApiBase.ApiParcelable;
import cz.harvester.api.ApiDataIO.ApiDataInput;
import cz.harvester.api.ApiDataIO.ApiDataOutput;
import cz.harvester.api.ApiDataIO.ApiParcelWrp;
import cz.harvester.api.ApiResults.ApiCard;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Cards {
	public static class Card extends ApiObject {
		private final ImmutableList<CardUnique> cardsIUnique;
		private final int index;
		
		public Card(ImmutableList<CardUnique> cardsIUnique) {
			this.cardsIUnique = cardsIUnique;
			
			int index = 0;
			DateTime lastDateTime = new DateTime(2000, 1, 1, 0, 0, 0, 0);
			for (int i = 0; i < this.cardsIUnique.size(); i++) {
				if (this.cardsIUnique.get(i).getGotWhen().isAfter(lastDateTime)) {
					lastDateTime = this.cardsIUnique.get(i).getGotWhen();
					index = this.cardsIUnique.get(i).getIndex();
				}
			}
			this.index = index;
		}
		
		public Card(int index) {
			this.cardsIUnique = ImmutableList.of();
			this.index = index;
		}
		
		public Card(ApiDataInput d) {
			CardUnique[] tmp = new CardUnique[d.readInt()];
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = new CardUnique(d);
			}
			this.cardsIUnique = ImmutableList.copyOf(tmp);
			this.index = d.readInt();
		}
		
		@Override
		public void save(ApiDataOutput d, int flags) {
			writeToDataOutput(d, flags, cardsIUnique);
			d.write(this.index);
		}
		
		public ImmutableList<CardUnique> getCardsIUnique() {
			return this.cardsIUnique;
		}

		public int getIndex() {
			return this.index;
		}
	}
	
	public static class CardUnique extends ApiParcelable {
		private final int uniqueId;
		private final int cardId;
		private final int index;
		private final int type;
		private final String gotFrom;
		private final String name;
		private final String text;
		private final Bitmap picture;
		private final DateTime gotWhen;
		
		public CardUnique(int uniqueId, int cardId, int index, int type, String gotFrom, String name, String text, Bitmap picture, DateTime gotWhen) {
			this.uniqueId = uniqueId;
			this.cardId = cardId;
			this.index = index;
			this.type = type;
			this.gotFrom = gotFrom;
			this.name = name;
			this.text = text;
			this.picture = picture;
			this.gotWhen = gotWhen;
		}
		
		public CardUnique(ApiCard c) {
			this.uniqueId = c.getUniqueId();
			this.cardId = c.getCardId();
			this.index = c.getIndex();
			this.type = c.getType();
			this.gotFrom = c.getGotFrom();
			this.name = c.getName();
			this.text = c.getText();
			this.picture = c.getPicture();
			this.gotWhen = new DateTime();
		}
		
		public CardUnique(ApiDataInput d) {
			this.uniqueId = d.readInt();
			this.cardId = d.readInt();
			this.index = d.readInt();
			this.type = d.readInt();
			this.gotFrom = d.readString();
			this.name = d.readString();
			this.text = d.readString();
			this.picture = d.readBitmap();
			this.gotWhen = new DateTime(d.readLong());
		}

		@Override
		public void save(ApiDataOutput d, int flags) {
			d.write(uniqueId);
			d.write(cardId);
			d.write(index);
			d.write(type);
			d.write(gotFrom);
			d.write(name);
			d.write(text);
			d.write(picture, flags);
			d.write(gotWhen.getMillis());
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
		
		public DateTime getGotWhen() {
			return this.gotWhen;
		}
		
		public static final Parcelable.Creator<CardUnique> CREATOR = new Parcelable.Creator<CardUnique>() {
			public CardUnique createFromParcel(Parcel source) {
				ApiParcelWrp wrp = new ApiParcelWrp(source, false);
				return new CardUnique(wrp);
			}

			public CardUnique[] newArray(int size) {
				return new CardUnique[size];
			}
 		};
	}
}
