package kr.khu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 
 * @author QUOC NGUYEN
 *
 */
public class SharePreferenceData {
	/**
	 * 
	 * @param context
	 * @param childName
	 */
	public static void saveChildName(Context context, String childName) {
		SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putString(Def.CHILD_NAME, childName);
		editor.commit();
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getChildName(Context context) {
		SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return app_preferences.getString(Def.CHILD_NAME, "");
	}
}
