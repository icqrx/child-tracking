package kr.khu.views;

import kr.khu.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageView extends LinearLayout {

	private TextView tvMessage;
	public MessageView(Context context) {
		super(context);
		LayoutInflater inflate = LayoutInflater.from(getContext());
		inflate.inflate(R.layout.message_view, this, true);
		
		tvMessage = (TextView)findViewById(R.id.tv_message);
	}
	
	/**
	 * set data
	 */
	public void setMessageData(String message) {
		tvMessage.append(message + "\n");
	}

}
