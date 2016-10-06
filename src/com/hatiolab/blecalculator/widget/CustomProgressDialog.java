package com.hatiolab.blecalculator.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.hatiolab.blecalculator.R;

public class CustomProgressDialog extends Dialog {
	private static CustomProgressDialog instance = null;
	
	public static CustomProgressDialog initDialog(Context context, int theme, String title, int message) {
		if (instance == null || !instance.isShowing()) {
			instance = new CustomProgressDialog(context, theme, title, message);
		} else if (message == R.string.try_connect) {
			instance.dismiss();
//			backupInstance = instance;
			instance = new CustomProgressDialog(context, theme, title, message);
		}
		
		return instance;
	}
	
	public CustomProgressDialog(Context context) {
		super(context);
	}
	
	public CustomProgressDialog(Context context, int theme, String title, int message) {
        super(context, theme);
        this.setContentView(R.layout.dialog_custom_progress);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        this.setTitle(title);
        this.startProgressDialog(context, message);
    }
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			ImageView imageView = (ImageView) this.findViewById(R.id.iv_loading);
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
			animationDrawable.start();
		}
    }
 
	protected void startProgressDialog(Context context, int message) {
		if (context == null) {
			return;
		}
		try {
			this.setMessage(" " + context.getResources().getString(message));
			this.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
    
	protected void setMessage(String strMessage) {
    	TextView tvMsg = (TextView)this.findViewById(R.id.tv_loadingmsg);
    	
    	if (tvMsg != null) {
    		tvMsg.setText(strMessage);
    	}
    }
    
	@Override
	public void cancel() {
		super.cancel();
		instance = null;
//		if (backupInstance != null) {
//			instance = backupInstance;
//			backupInstance = null;
//		}
	}
	
	@Override
	protected void onStop() {
		if (this.isShowing()) {
			this.cancel();
		}
		super.onStop();
	}
}
