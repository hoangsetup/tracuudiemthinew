package com.longnd.tracuudiemthi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SubBarActivity extends Activity {
	Button button;
	Spinner spinner;
	
	//Hai mảng string để làm dữ liệu đổ vào và giá trị thực cho spinner chọn môn học
	String[] hienthi, giatri;
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subbar);
		hienthi = getResources().getStringArray(R.array.textHienthi);
		giatri = getResources().getStringArray(R.array.textValue);

		spinner = (Spinner) findViewById(R.id.spinner_monhoc);
		button = (Button) findViewById(R.id.button_xong);
		adapter = new ArrayAdapter<String>(SubBarActivity.this,
				android.R.layout.simple_list_item_1, hienthi);
		spinner.setAdapter(adapter);
		// nút "xong" được click
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//Trả lại thông tin cho BarActivity
				Intent intent = new Intent();
				//mã để truyền vào url lấy thông tin biểu đồ
				intent.putExtra("res",
						giatri[spinner.getSelectedItemPosition()]);
				//Vị trí được chọn, phục vụ lấy tên biểu đồ theo tên hiển thị
				intent.putExtra("int", spinner.getSelectedItemPosition());
				setResult(111, intent);
				finish();
			}
		});
	}
}
