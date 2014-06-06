package kr.khu.views;

import kr.khu.activity.R;
import kr.khu.utils.Def;
import kr.khu.utils.HttpRequest;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * This class to manage login view
 * @author QUOC NGUYEN
 *
 */
public class LoginView extends LinearLayout {
	private EditText edtParentCode;
	private Button btnRequest;
	
	public LoginView(Context context) {
		super(context);
		LayoutInflater inflate = LayoutInflater.from(getContext());
		inflate.inflate(R.layout.login_view, this, true);
		
		edtParentCode = (EditText)findViewById(R.id.edt_parent_code);
		btnRequest = (Button)findViewById(R.id.btn_request_login);
		
		btnRequest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String parentCode = edtParentCode.getText().toString();
				if (parentCode != null)
					HttpRequest.request(Def.HTTP_LOGIN, Def.LOGIN_API, parentCode);
			}
		});
		
	}
	public void setData() {
		
	}
}
