/*
 * Activity Màn hình khởi động,
 * kiểm tra kết nối internet của thiết bị
 */
package com.longnd.tracuudiemthi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashScreen extends Activity {
	public static int SPLASH_TIME_OUT = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		if (isConnectingToInternet()) {
			//Phương thức sẽ chạy vào void run() sau 1000ms
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(SplashScreen.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				}
			}, SPLASH_TIME_OUT);
		} else {
			Toast.makeText(this, "Thiết bị không kết nối Internet",
					Toast.LENGTH_LONG).show();
		}
	}

	//Phương thức kiếm tra kết nối internet - nếu có kết nối trả về true
	public boolean isConnectingToInternet() {
		//Lấy thông tin các kết nối trên thiết bị
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cManager != null) {
			NetworkInfo[] infos = cManager.getAllNetworkInfo();
			if (infos != null) {
				for (NetworkInfo i : infos) {
					if (i.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
