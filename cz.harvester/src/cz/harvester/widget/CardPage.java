package cz.harvester.widget;

import com.google.common.collect.ImmutableList;

import cz.harvester.R;
import cz.harvester.data.Cards.Card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class CardPage extends TableLayout {
	public static final int COLUMNS_COUNT = 3;
	public static final int ROW_COUNT = 3;
	
	private final LayoutInflater inflater;
	private ImmutableList<Card> cards;
	
	private OnCardClickListener onCardClickListener;

	public CardPage(Context context) {
		this(context, null);
	}

	public CardPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.setShrinkAllColumns(true);
		this.setStretchAllColumns(true);
	}
	
	public void setCards(ImmutableList<Card> cards) {
		this.cards = cards;
		
		this.removeAllViews();
		
		TableRow row = new TableRow(getContext());
		for (int i = 0; i < cards.size(); i++) {
			Card c = cards.get(i);
			
			ViewGroup cardLayout = (ViewGroup)inflater.inflate(R.layout.card_layout, null);
			ImageView img = (ImageView)cardLayout.findViewById(R.id.picture);
			ImageView bcg = (ImageView)cardLayout.findViewById(R.id.overlay);
			
			if (c.getCardsIUnique().size() > 0) {
				img.setEnabled(true);
				img.setImageBitmap(c.getCardsIUnique().get(0).getPicture());
				bcg.setClickable(true);
				bcg.setTag(c);
				bcg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Card c = (Card)v.getTag();
						if (c != null)
							CardPage.this.onCardClick(c);
					}
				});
			}
			else {
				img.setEnabled(false);
			}
			
			row.addView(cardLayout, new TableRow.LayoutParams(0, TableRow.LayoutParams.FILL_PARENT, 1.0f));
			
			if (i % COLUMNS_COUNT == COLUMNS_COUNT - 1) {
				this.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 0, 1.0f));
				row = new TableRow(getContext());
			}
		}
		
		while (this.getChildCount() < ROW_COUNT) {
			while (row.getChildCount() < COLUMNS_COUNT) {
				ViewGroup cardLayout = (ViewGroup)inflater.inflate(R.layout.card_layout, null);
				row.addView(cardLayout, new TableRow.LayoutParams(0, TableRow.LayoutParams.FILL_PARENT, 1.0f));
			}
			
			this.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 0, 1.0f));
			row = new TableRow(getContext());
		}
		
//		while (this.getChildCount() < ROW_COUNT) {
//			row = new TableRow(getContext());
//			row.addView(inflater.inflate(R.layout.card_layout, null), new TableRow.LayoutParams(0, TableRow.LayoutParams.FILL_PARENT, 1.0f));
//			row.setVisibility(INVISIBLE);
//			this.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 0, 1.0f));
//		}
	}
	
	public ImmutableList<Card> getCards() {
		return this.cards;
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
