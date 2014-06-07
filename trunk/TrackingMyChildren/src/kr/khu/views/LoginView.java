package kr.khu.views;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kr.khu.activity.R;
import kr.khu.gps.GPSTracker;
import kr.khu.utils.Def;
import kr.khu.utils.HttpRequest;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * This class to manage login view
 * 
 * @author QUOC NGUYEN
 * 
 */
public class LoginView extends LinearLayout {
	protected static final String TAG = LoginView.class.getSimpleName();
	private EditText edtParentCode;
	private Button btnRequest;
    private GPSTracker gps;
	public LoginView(Context context) {
		super(context);
		LayoutInflater inflate = LayoutInflater.from(getContext());
		inflate.inflate(R.layout.login_view, this, true);

		edtParentCode = (EditText) findViewById(R.id.edt_parent_code);
		btnRequest = (Button) findViewById(R.id.btn_request_login);

		btnRequest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String parentCode =  edtParentCode.getText().toString();
				if (parentCode != null) {
					// HttpRequest.request(Def.HTTP_LOGIN, Def.LOGIN_API,parentCode);
					try {
						final String data = URLEncoder.encode("parent_code", "UTF-8") + "=" + URLEncoder.encode(parentCode, "UTF-8");
						Log.d(TAG, "Request to server: " + parentCode);
						
						
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								String response = HttpRequest.sendData(Def.HTTP_METHOD_POST, Def.LOGIN_API, data);
								Log.d(TAG, "Response from server: " + response);
							}
						}).start();
						
						
						gps = new GPSTracker(getContext());
						 // check if GPS enabled     
		                if(gps.canGetLocation()){
		                     
		                    double latitude = gps.getLatitude();
		                    double longitude = gps.getLongitude();
		                    // \n is for new line
		                    Toast.makeText(getContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
		                }else{
		                    // can't get location
		                    // GPS or Network is not enabled
		                    // Ask user to enable GPS/network in settings
		                    gps.showSettingsAlert();
		                }
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		});
	}
	
	/*
	 * 
	 */
	public void setData() {

	}
}
