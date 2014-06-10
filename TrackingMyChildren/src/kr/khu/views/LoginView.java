package kr.khu.views;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.android.gcm.GCMRegistrar;

import kr.khu.activity.R;
import kr.khu.gps.GPSTracker;
import kr.khu.utils.Def;
import kr.khu.utils.HttpRequest;
import kr.khu.utils.SharePreferenceData;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class to manage login view
 * 
 * @author QUOC NGUYEN
 * 
 */
public class LoginView extends LinearLayout {
	protected static final String TAG = LoginView.class.getSimpleName();
	private TextView txtChildName;
	private TextView txtEmail;
	private TextView txtParentCode;
	private TextView txtParentName;
	private EditText edtParentCode;
	private EditText edtChildName;
	private EditText edtChildEmail;
	private Button btnRequest;
	private GPSTracker gps;
	public static String childName = "";

	public LoginView(Context context) {
		super(context);
		LayoutInflater inflate = LayoutInflater.from(getContext());
		inflate.inflate(R.layout.login_view, this, true);
		
		txtChildName = (TextView)findViewById(R.id.tv_child_name);
		txtEmail = (TextView)findViewById(R.id.tv_child_email);
		txtParentCode = (TextView)findViewById(R.id.tv_enter_parent_code);
		txtParentName = (TextView)findViewById(R.id.tv_parent_name);
		
		
		edtParentCode = (EditText) findViewById(R.id.edt_parent_code);
		edtChildName = (EditText) findViewById(R.id.edt_child_name);
		edtChildEmail = (EditText) findViewById(R.id.edt_child_email);
		btnRequest = (Button) findViewById(R.id.btn_request_login);

		// check the child has already signed
		String checked = SharePreferenceData.getCheckedRegister(getContext());
		
		txtParentName.setVisibility(View.INVISIBLE);
		
		if(!checked.equalsIgnoreCase("0")) {
			showControl();
			txtParentName.setText("Your parent name: " + checked.split(",")[1].toString());
		}
		
		btnRequest.setOnClickListener(loginListener);
		
		
	}

	/**
	 * Listener for login button
	 */
	OnClickListener loginListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String parentCode = edtParentCode.getText().toString();
			childName = edtChildName.getText().toString();
			// SharePreferenceData.saveChildName(getContext(), childName);
			String childEmail = edtChildEmail.getText().toString();
			String regChildIF = SharePreferenceData.getRegID(getContext());
			try {
				String data = URLEncoder.encode("parent_code", "UTF-8") + "="
						+ URLEncoder.encode(parentCode, "UTF-8");
				data += "&" + URLEncoder.encode("reg_child_id", "UTF-8") + "="
						+ URLEncoder.encode(regChildIF, "UTF-8");
				data += "&" + URLEncoder.encode("child_name", "UTF-8") + "="
						+ URLEncoder.encode(childName, "UTF-8");
				data += "&" + URLEncoder.encode("child_email", "UTF-8") + "="
						+ URLEncoder.encode(childEmail, "UTF-8");
				final String requestData = data;

				Log.d(TAG, "Request to server: " + requestData);

				// request to server
				new AsyncTaskRequest(getContext()).execute(requestData);

				gps = new GPSTracker(getContext());
				// check if GPS enabled
				if (gps.canGetLocation()) {

					//double latitude = gps.getLatitude();
					//double longitude = gps.getLongitude();
					//Toast.makeText(getContext(),"Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
				} else {
					// can't get location
					// GPS or Network is not enabled
					// Ask user to enable GPS/network in settings
					gps.showSettingsAlert();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Asynctask support request to server
	 * 
	 * @author QUOC NGUYEN
	 * 
	 */
	class AsyncTaskRequest extends AsyncTask<String, Void, Void> {
		private String response;
		private Context mContext;
		private AlertDialog alertDialog;
		private ProgressDialog dialog;

		public AsyncTaskRequest(Context context) {
			mContext = context;
			dialog = new ProgressDialog(mContext);
		}

		@Override
		protected Void doInBackground(String... requestData) {
			response = HttpRequest.sendData(Def.HTTP_METHOD_POST, Def.LOGIN_API, requestData[0]);
			Log.d(TAG, "Response from server: " + response);
			return null;
		}

		@Override
		protected void onPreExecute() {
			alertDialog = new AlertDialog.Builder(mContext).create();
			dialog.setMessage("" + "Please wait...");
			dialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			if (response.equalsIgnoreCase("0\n")) {
				alertDialog.setMessage("Parent doesn't exits.");
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				alertDialog.show();
				if (dialog.isShowing()) {
		            dialog.dismiss();
		        }
			} else {
				//response="1,parent_name,regi_child_id"
				SharePreferenceData.saveCheckRegister(mContext,"1" + "," + response.split(",")[1] + "," + response.split(",")[2]);
			    if (dialog.isShowing()) {
		            dialog.dismiss();
		        }
			    showControl();
			    txtParentName.setText("Your parent name: " + response.split(",")[1].toString());
			}
		}

	}
	
	/**
	 * 
	 * @param response
	 */
	public void showControl() {
		btnRequest.setVisibility(View.GONE);
		txtChildName.setVisibility(View.GONE);
		edtChildName.setVisibility(View.GONE);
		txtEmail.setVisibility(View.GONE);
		edtChildEmail.setVisibility(View.GONE);
		txtParentCode.setVisibility(View.GONE);
		edtParentCode.setVisibility(View.GONE);
		txtParentName.setVisibility(View.VISIBLE);
	}

}
