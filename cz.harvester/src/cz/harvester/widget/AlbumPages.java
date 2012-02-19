package cz.harvester.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import cz.harvester.data.Cards.Card;
import cz.harvester.data.Cards.CardUnique;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class AlbumPages extends Workspace {
	
	private OnCardClickListener onCardClickListener;

	public AlbumPages(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setCards(ImmutableList<CardUnique> cardsUnique) {
		Map<Integer, List<CardUnique>> map = new HashMap<Integer, List<CardUnique>>();
		
		int maxIndex = 27;
		for (CardUnique c : cardsUnique) {
			if (!map.containsKey(c.getIndex())) {
				map.put(c.getIndex(), new ArrayList<CardUnique>());
			}
			map.get(c.getIndex()).add(c);
			maxIndex = Math.max(maxIndex, c.getIndex());
		}
		
		Card[] tmp = new Card[maxIndex + 1];
		for (int i = 0; i < tmp.length; i++) {
			if (map.containsKey(i)) {
				tmp[i] = new Card(ImmutableList.copyOf(map.get(i)));
			}
			else {
				tmp[i] = new Card(i);
			}
		}
		
		int elemsCount = CardPage.COLUMNS_COUNT * CardPage.ROW_COUNT;
		ImmutableList.Builder<Card> b = new ImmutableList.Builder<Card>();
		ImmutableList.Builder<ImmutableList<Card>> newItems = new ImmutableList.Builder<ImmutableList<Card>>();
		
		for (int i = 0; i < tmp.length; i++) {
			b.add(tmp[i]);
			if (i % elemsCount == elemsCount - 1) {
				newItems.add(b.build());
				b = new ImmutableList.Builder<Card>();
			}
		}
		ImmutableList<Card> tmp1 = b.build();
		if (tmp1.size() > 0)
			newItems.add(tmp1);
		
		ImmutableList<ImmutableList<Card>> finCards = newItems.build();
		
		removeAllViews();
		for (int i = 0; i < finCards.size(); i++) {
			CardPage cardPage = new CardPage(getContext());
			cardPage.setCards(finCards.get(i));
			cardPage.setOnCardClickListener(new CardPage.OnCardClickListener() {
				@Override
				public void onCardClick(Card c) {
					AlbumPages.this.onCardClick(c);		
				}
			});
			
			addView(cardPage, new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 
					ViewGroup.LayoutParams.FILL_PARENT));
		}
		
		
	}
	
	public interface OnCardClickListener {
		void onCardClick(Card c);
	}
	
	public void setOnCardClickListener(OnCardClickListener l) {
		this.onCardClickListener = l;
	}
	
	protected void onCardClick(Card c) {
		if (this.onCardClickListener != null) {
			this.onCardClickListener.onCardClick(c);
		}
	}

}
