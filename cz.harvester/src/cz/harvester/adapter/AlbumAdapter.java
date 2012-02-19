package cz.harvester.adapter;

public class AlbumAdapter {}
//
//import com.google.common.collect.ImmutableList;
//
//import cz.harvester.R;
//import cz.harvester.data.Cards.Card;
//import cz.harvester.widget.CardPage;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//
//public class AlbumAdapter extends BaseAdapter {
//	private final Context context;
//	private final LayoutInflater inflater;
//	
//	private ImmutableList<ImmutableList<Card>> items = ImmutableList.of();
//	
//	public AlbumAdapter(Context context) {	
//		this.context = context;
//		this.inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	}
//
//	public void setCards(ImmutableList<Card> cards) {
//		if (items == null) {
//			this.items = ImmutableList.of();
//		}
//		else {
//			int maxIndex = 0;
//			for (int i = 0; i < cards.size(); i++) {
//				maxIndex = Math.max(maxIndex, cards.get(i).getIndex());
//			}
//			
//			CardBase[] tmp = new CardBase[maxIndex + 1];
//			for (int i = 0; i < tmp.length; i++) {
//				tmp[i] = new EmptyCard(i);
//			}
//			
//			for (Card c : cards) {
//				tmp[c.getIndex()] = c;
//			}
//			
//			int elemsCount = CardPage.COLUMNS_COUNT * CardPage.ROW_COUNT;
//			ImmutableList.Builder<CardBase> b = new ImmutableList.Builder<CardBase>();
//			ImmutableList.Builder<ImmutableList<CardBase>> newItems = new ImmutableList.Builder<ImmutableList<CardBase>>();
//			
//			for (int i = 0; i < tmp.length; i++) {
//				b.add(tmp[i]);
//				if (i % elemsCount == elemsCount - 1) {
//					newItems.add(b.build());
//				}
//			}
//			this.items = newItems.build();
//		}
//		this.notifyDataSetChanged();
//	}
//	
//	public void clear() {
//		setCards(null);
//	}
//	
//	public int getCount() {
//		return items.size();
//	}
//
//	public ImmutableList<CardBase> getItem(int position) {
//		return items.get(position);
//	}
//
//	public long getItemId(int position) {
//		return position;
//	}
//	
//	@Override
//	public int getItemViewType(int position) {
//		return 0;
//	}
//	
//	@Override
//	public int getViewTypeCount() {
//		return 1;
//	}
//
//	public View getView(int position, View convertView, ViewGroup parent) {
//		if (convertView == null) {
//			convertView =  new CardPage(context);
//		}
//		
//		CardPage page = (CardPage)convertView;
//		page.setCards(items.get(position));
//		return convertView;
//	}
//}
