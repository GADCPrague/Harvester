package cz.harvester.widget;

import com.google.common.collect.ImmutableList;

import cz.harvester.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionBar extends LinearLayout {
	private final ImageView logoImageView;
	private final ImageButton homeButton;
	private final ImageView separatorToLeftOfText;
	private final TextView text;
	private final FrameLayout customContentViewHolder;
	private final ImmutableList<ActionBarButton> buttons;
	
	private ActionBarType actionBarType = ActionBarType.DASHBOARD;
	private OnActionBarHomeClickListener onActionBarHomeClickListener;
	private View customContentView;
	
	private OnActionBarButtonClickListener onActionBarButtonClickListener;
	
	public ActionBar(Context context) {
		this(context, null);
	}

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.action_bar, this);
		
		this.logoImageView = (ImageView)layout.findViewById(R.id.action_bar_logo);
		this.homeButton = (ImageButton)layout.findViewById(R.id.action_bar_home_button);
		this.homeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onActionBarHomeClick();
			}
		});
		this.separatorToLeftOfText = (ImageView)layout.findViewById(R.id.action_bar_separator_to_left_of_text);
		this.text = (TextView)layout.findViewById(R.id.action_bar_text);
		this.customContentViewHolder = (FrameLayout)layout.findViewById(R.id.action_bar_custom_content_view_holder);
		
		ImmutableList.Builder<ActionBarButton> buttons = new ImmutableList.Builder<ActionBar.ActionBarButton>();
		buttons.add(new ActionBarButton((ImageButton)layout.findViewById(R.id.action_bar_button1),
				(ImageView)layout.findViewById(R.id.action_bar_separator1), 0));
		buttons.add(new ActionBarButton((ImageButton)layout.findViewById(R.id.action_bar_button2), 
				(ImageView)layout.findViewById(R.id.action_bar_separator2), 1));
		buttons.add(new ActionBarButton((ImageButton)layout.findViewById(R.id.action_bar_button3), 
				(ImageView)layout.findViewById(R.id.action_bar_separator3), 2));
		this.buttons = buttons.build();
		
		for (ActionBarButton b : this.buttons) {
			b.getButton().setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					onActionBarButtonClick(((ActionBarButton)v.getTag()).getIndex());					
				}
			});
		}
	}
	
	
	public ActionBarType getActionBarType() {
		return actionBarType;
	}
	
	public void setActionBarType(ActionBarType value) {
		this.actionBarType = value;
		switch (value) {
		case DASHBOARD: {
			this.logoImageView.setVisibility(VISIBLE);
			this.homeButton.setVisibility(GONE);
			this.separatorToLeftOfText.setVisibility(GONE);
			break;
		}
		case NORMAL: {
			this.logoImageView.setVisibility(GONE);
			this.homeButton.setVisibility(VISIBLE);
			//this.separatorToLeftOfText.setVisibility(VISIBLE);
			this.separatorToLeftOfText.setVisibility(INVISIBLE);
			break;
		}
		case NOTHING_ON_LEFT: {
			this.logoImageView.setVisibility(GONE);
			this.homeButton.setVisibility(GONE);
			this.separatorToLeftOfText.setVisibility(GONE);
			break;
		}
		default: throw new RuntimeException();
		}
	}
	
	public void setTextNarrowPaddings(boolean value) {
		int padding = getContext().getResources().getDimensionPixelOffset(
			value ? R.dimen.action_bar_text_padding_left_right_narrow : R.dimen.action_bar_text_padding_left_right);
		text.setPadding(padding, 0, padding, 0);
	}
	
	protected void onActionBarHomeClick() {
		if (this.onActionBarHomeClickListener != null)
			this.onActionBarHomeClickListener.onClick();
	}
	
	public void setOnActionBarHomeClickListener(OnActionBarHomeClickListener listener) {
		this.onActionBarHomeClickListener = listener;
	}
	
	
	public String getActionBarText() {
		return this.text.getText().toString();
	}
	
	public void setActionBarText(String value) {
		this.text.setText(value);
	}
	
	
	public View getCustomContentView() {
		return this.customContentView;
	}
	
	public void setCustomContentView(View customContentView) {
		if (this.customContentView != customContentView) {
			this.customContentView = customContentView;
			
			if (customContentView == null) {
				this.customContentViewHolder.removeAllViews();
				this.customContentViewHolder.setVisibility(GONE);
				this.text.setVisibility(VISIBLE);
			}
			else {
				this.customContentViewHolder.addView(customContentView, 
					new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
				this.customContentViewHolder.setVisibility(VISIBLE);
				this.text.setVisibility(GONE);
			}
		}
	}
	
	
	public int getButtonsCapacity() {
		return buttons.size();
	}
	
	public ActionBarButtonType getActionBarButtonType(int buttonIndex) {
		return this.buttons.get(buttonIndex).getActionBarButtonType();
	}
	
	public void setActionBarButtonType(int buttonIndex, ActionBarButtonType value) {
		this.buttons.get(buttonIndex).setActionBarButtonType(value);
	}
	
	protected void onActionBarButtonClick(int buttonIndex) {
		if (onActionBarButtonClickListener != null)
			onActionBarButtonClickListener.onClick(buttonIndex);
	}
	
	public void setOnActionBarButtonClickListener(OnActionBarButtonClickListener listener) {
		this.onActionBarButtonClickListener = listener;
	}
		
	
	public enum ActionBarType {
		DASHBOARD, 
		NORMAL,
		NOTHING_ON_LEFT
	}
	
	public enum ActionBarButtonType {
		NONE,
		EMPTY,
		SEARCH,
		QUIT,
		LOGIN,
		LOCK,
		CLOSE,
		LOGOUT,
		ARROW_RIGHT,
		ARROW_LEFT
	}
	
	public interface OnActionBarButtonClickListener {
		void onClick(int buttonIndex);
	}
	
	public interface OnActionBarHomeClickListener {
		void onClick();
	}
	
	private class ActionBarButton {
		private final ImageButton button;
		private final ImageView separator;
		private final int index;
		private ActionBarButtonType type = ActionBarButtonType.NONE;
		
		public ActionBarButton(ImageButton button, ImageView separator, int index) {
			this.button = button;
			this.separator = separator;
			this.index = index;
			
			this.button.setTag(this);
		}
		
		public ImageButton getButton() {
			return button;
		}
		
		public int getIndex() {
			return index;
		}
		
		public ActionBarButtonType getActionBarButtonType() {
			return type;
		}
		
		public void setActionBarButtonType(ActionBarButtonType value) {
			this.type = value;
			int id;
			
			switch (this.type) {
			case NONE: button.setVisibility(GONE); separator.setVisibility(GONE); return;
			case EMPTY: button.setVisibility(INVISIBLE); separator.setVisibility(VISIBLE); return;
			case SEARCH: id = R.drawable.ic_action_bar_search; break;
			case QUIT: id = R.drawable.ic_action_bar_quit; break;
			case LOGIN: id = R.drawable.ic_action_bar_login; break;
			case LOCK: id = R.drawable.ic_action_bar_lock; break;
			case CLOSE: id = R.drawable.ic_action_bar_close; break;
			case LOGOUT: id = R.drawable.btn_logout_normal; break;
			case ARROW_RIGHT: id = R.drawable.ic_action_bar_arrow_right; break;
			case ARROW_LEFT: id = R.drawable.btn_sync_normal; break;
			
//			case MAP: id = R.drawable.ic_action_bar_map; break;
//			case REVERSE: id = R.drawable.ic_action_bar_reverse; break;
//			case DEPARTURES: id = R.drawable.ic_action_bar_departures; break;
//			case MY_LOCATION: id = R.drawable.ic_action_bar_mylocation; break;
//			case GOTO_START: id = R.drawable.ic_action_bar_goto_start; break;
//			case GOTO_END: id = R.drawable.ic_action_bar_goto_end; break;
			default: throw new RuntimeException();
			}
			
			separator.setVisibility(VISIBLE);
			button.setVisibility(VISIBLE);	
			button.setImageResource(id);
		}
	}
}
