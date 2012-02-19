package cz.harvester.widget;

import cz.harvester.R;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LoginForm extends LinearLayout {
	private final EditText nickEditText;
	private final EditText passwordEditText;
	private final Button registerButton;
	private final Button loginButton;
	
	private OnLoginButtonClickListener onLoginButtonClickListener;
	private OnRegisterButtonClickListener onRegisterButtonClickListener;

	public LoginForm(Context context) {
		this(context, null);
	}

	public LoginForm(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.setOrientation(VERTICAL);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.login_form, this, true);
		
		this.nickEditText = (EditText)this.findViewById(R.id.nick_edit_text);
		this.passwordEditText = (EditText)this.findViewById(R.id.password_edit_text);
		this.registerButton = (Button)this.findViewById(R.id.register_button);
		this.loginButton = (Button)this.findViewById(R.id.login_button);
		
		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				registerButton.setEnabled(nickEditText.getText().length() > 0 && passwordEditText.getText().length() > 0);
				loginButton.setEnabled(nickEditText.getText().length() > 0 && passwordEditText.getText().length() > 0);
			}
		};
		
		this.nickEditText.addTextChangedListener(textWatcher);
		this.passwordEditText.addTextChangedListener(textWatcher);
		this.loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String nick = nickEditText.getText().toString();
					String password = passwordEditText.getText().toString();
					passwordEditText.setText("");
					// chci schovat soft klavesnici
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(passwordEditText.getApplicationWindowToken(), 0);
					onLoginButtonClick(nick, password);
				}
				catch (Exception ex) {}
			}
		});
		
		this.registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String nick = nickEditText.getText().toString();
					String password = passwordEditText.getText().toString();
					passwordEditText.setText("");
					// chci schovat soft klavesnici
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(passwordEditText.getApplicationWindowToken(), 0);
					onRegisterButtonClick(nick, password);
				}
				catch (Exception ex) {}
			}
		});
	}
	
	public void setOnLoginButtonClickListener(OnLoginButtonClickListener l) {
		this.onLoginButtonClickListener = l;
	}
	
	protected void onLoginButtonClick(String nick, String password) {
		if (this.onLoginButtonClickListener != null) {
			this.onLoginButtonClickListener.onLoginButtonClick(nick, password);
		}
	}

	public interface OnLoginButtonClickListener {
		void onLoginButtonClick(String nick, String password);
	}
	
	
	public void setOnRegisterButtonClickListener(OnRegisterButtonClickListener l) {
		this.onRegisterButtonClickListener = l;
	}
	
	protected void onRegisterButtonClick(String nick, String password) {
		if (this.onRegisterButtonClickListener != null) {
			this.onRegisterButtonClickListener.onRegisterButtonClick(nick, password);
		}
	}

	public interface OnRegisterButtonClickListener {
		void onRegisterButtonClick(String nick, String password);
	}
}
