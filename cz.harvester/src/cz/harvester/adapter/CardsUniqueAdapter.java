package cz.harvester.adapter;

import com.google.common.collect.ImmutableList;

import cz.harvester.R;
import cz.harvester.data.Cards.CardUnique;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CardsUniqueAdapter extends BaseAdapter {
	private final Context context;
	private final LayoutInflater inflater;
	
	private ImmutableList<CardUnique> items = ImmutableList.of();
	
	public CardsUniqueAdapter(Context context) {	
		this.context = context;
		this.inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCards(ImmutableList<CardUnique> cards) {
		this.items = cards == null ? ImmutableList.<CardUnique>of() : cards;
		this.notifyDataSetChanged();
	}
	
	public void clear() {
		setCards(null);
	}
	
	public int getCount() {
		return items.size();
	}

	public CardUnique getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		return 0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.card_unique_item, parent, false);
			holder = new ViewHolder(
					(ImageView)convertView.findViewById(R.id.picture),
					(TextView)convertView.findViewById(R.id.text1), 
					(TextView)convertView.findViewById(R.id.text2));
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		CardUnique item = items.get(position);
		holder.getPicture().setImageBitmap(item.getPicture());
		holder.getText1().setText(item.getName());
		holder.getText2().setText(item.getGotFrom());
		
		return convertView;
	}
	
	public static class ViewHolder {
		private final ImageView picture;
		private final TextView text1;
		private final TextView text2;
		
		public ViewHolder(ImageView picture, TextView text1, TextView text2) {
			this.picture = picture;
			this.text1 = text1;
			this.text2 = text2;
		}
		
		public ImageView getPicture() {
			return this.picture;
		}
		
		public TextView getText1() {
			return this.text1;
		}
		
		public TextView getText2() {
			return this.text2;
		}
	}
}
