package kr.khu.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class support functions request to server
 * 
 * @author QUOC NGUYEN
 * 
 */
public class HttpRequest {

	private static final String TAG = HttpRequest.class.getSimpleName();
	static String response = null;

	public static StringBuilder createStringBuilder(int option,
			String... params) {
		StringBuilder builder = new StringBuilder();
		switch (option) {
		case Def.HTTP_LOGIN:
			// send parent_code len, server tra ve id cua thang con, lay id do
			// request gps len
			builder.append("parent_code=").append(params[0]);
			// builder.append("&parent_code=").append(params[1]);
			break;
		case Def.HTTP_GPS:
			builder.append("child_id=").append(params[0]);
			builder.append("&location=").append(params[1]);
			break;
		}
		return builder;
	}

	/**
	 * 
	 * @param aJson
	 * @return
	 */
	public static int getStatus(String aJson) {
		try {
			JSONObject jObject = new JSONObject(aJson);
			String status = jObject.getString(Def.STATUS);
			return Integer.valueOf(status);
		} catch (JSONException e) {
			Log.e(TAG, "" + e.toString());
		}
		return Def.REQUEST_INVALID;
	}

	/**
	 * 
	 * @param typeRequest
	 * @param LinkAPI
	 * @param params
	 * @return
	 */
	public static int request(int typeRequest, String LinkAPI, String... params) {
		HttpURLConnection conn = null;
		StringBuilder builder = HttpRequest.createStringBuilder(typeRequest,
				params);
		Log.d(TAG, "Request:" + builder.toString());
		int result = Def.REQUEST_INVALID;
		try {
			conn = setupConnection(LinkAPI, builder, params[0], params[1]);
			Log.d(TAG, "ResponseCode: " + conn.getResponseCode());
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = conn.getInputStream();
				String resultstring = HttpRequest
						.convertinputStreamToString(in);
				Def.JSON = resultstring;
				Log.d(TAG, "Resultstring: " + resultstring);
				result = HttpRequest.getStatus(resultstring);
			}

			builder = null;
			if (conn != null) {
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param aUrl
	 * @param builder
	 * @param params
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private static HttpURLConnection setupConnection(String aUrl,
			StringBuilder builder, String... params)
			throws MalformedURLException, IOException, ProtocolException {
		HttpURLConnection conn;
		URL url = new URL(aUrl);
		conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod(Def.HTTP_METHOD_POST);
		conn.setRequestProperty("Connection", "Keep-Alive");
		// conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		conn.setRequestProperty("Accept-Language", "jp");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// conn.setRequestProperty("Content-Length", "" +
		// Integer.toString(builder.toString().length()));
		// String httpHeader = "user_cd=" + params[0]+
		// "&user_type=driver&access_token=" + params[1];
		// Log.d(TAG, "RequestHeader = " + httpHeader);
		// conn.setRequestProperty("Authentication", httpHeader);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

		byte[] bytes = builder.toString().getBytes();

		for (int i = 0; i < bytes.length; i++) {
			wr.writeByte(bytes[i]);
		}

		wr.flush();
		wr.close();
		return conn;
	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * @params - http request params
	 * */
	public static String makeServiceCall(String url, int method,
			List<NameValuePair> params) {
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;

			// Checking http request method type
			if (method == Def.POST) {
				HttpPost httpPost = new HttpPost(url);
				// adding post params
				if (params != null) {
					httpPost.setEntity(new UrlEncodedFormEntity(params));
				}

				httpResponse = httpClient.execute(httpPost);

			} else if (method == Def.GET) {
				// appending params to url
				if (params != null) {
					String paramString = URLEncodedUtils
							.format(params, "utf-8");
					url += "?" + paramString;
				}
				HttpGet httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);
			}
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;

	}

	/**
	 * 
	 * @param is
	 * @return
	 */
	public static String convertinputStreamToString(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, "" + e.toString());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Log.e(TAG, "" + e.toString());
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param typeMethod
	 * @param URL_API
	 * @param data
	 * @return
	 */
	public static String sendData(String typeMethod, String URL_API, String data) {
		String result = "";
		switch (typeMethod) {
		case "POST":

			BufferedReader reader = null;

			// Send data
			try {
				// Defined URL where to send data
				URL url = new URL(URL_API);
				
				// Send POST data request
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the server response

				reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				// Read Server Response
				while ((line = reader.readLine()) != null) {
					// Append server response in string
					sb.append(line + "\n");
				}

				result = sb.toString();
			} catch (Exception ex) {
				ex.printStackTrace();
			} 
			break;
		case "GET":
			// Create http client object to send request to server
			HttpClient Client = new DefaultHttpClient();
			// Create URL string
			try {
				// Create Request to server and get response

				HttpGet httpget = new HttpGet(URL_API);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				result = Client.execute(httpget, responseHandler);

			} catch (Exception ex) {
			}

		default:
			break;
		}
		return result;
	}
	

}
