/*
 * Activity chính
 */
package com.longnd.tracuudiemthi;

import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	// Đường dẫn tìm thông tin thí sinh
	public static String URL_SEARCH_SBD = "http://long.nvttest.com/stile/pages/getdiem.jsp?sbd=";
	public static String URL_SEARCH_HOTEN = "http://long.nvttest.com/stile/pages/getdiem.jsp?hoten=";
	// public static String URL_SEARCH_SBD =
	// "http://10.0.3.2:8080/tracuudiem/pages/getdiem.jsp?sbd="; // dung cho
	// localhost
	// public static String URL_SEARCH_HOTEN =
	// "http://10.0.3.2:8080/tracuudiem/pages/getdiem.jsp?hoten=";// dung cho
	// localhost
	// Các đối tượng trên View
	// TextView textViewKq;
	EditText editText;
	Spinner spinner;
	Button button;
	ListView listViewMain;

	// Lựa chọn tìm theo tên || số báo danh
	ArrayAdapter<String> adapterSpinner = null;
	//
	// adapter cho main lisst
	MyListAdapter adapterMain = null;

	Vector<SinhvienEntity> sinhvienEntities = new Vector<SinhvienEntity>();
	SinhvienEntity sinhvienEntity = new SinhvienEntity();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle) Phương thức khởi
	 * tạo app bắt buộc cho mỗi ac
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_main);

		// Liên kết với file xml
		// textViewKq = (TextView) findViewById(R.id.textView_kq);
		spinner = (Spinner) findViewById(R.id.spinner);
		editText = (EditText) findViewById(R.id.editText_keyword);
		button = (Button) findViewById(R.id.button_search);
		listViewMain = (ListView) findViewById(R.id.listView_main);
		// lieen keets dữ liệu cho vào listmain
		adapterMain = new MyListAdapter(this, R.layout.item_listview,
				sinhvienEntities);
		listViewMain.setAdapter(adapterMain);

		// Khởi tạo dữ liệu cho vào spinner
		adapterSpinner = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.spinner_item_list, R.id.textView, new String[] {
						"Tìm kiếm theo số báo danh", "Tìm kiếm theo tên" });
		spinner.setAdapter(adapterSpinner);

		// Bắt sự kiện lựa chọn 1 dòng của spinner
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// arg2 là vị trí của item trong spinner được chọn
				if (arg2 == 0) {
					editText.setHint("Nhập số báo danh");
				} else {
					editText.setHint("Nhập họ tên thí sinh");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// Bắt sự kiện nút xem được click
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(editText.getText())) {
					// nếu thông tin ô text bị trống thì thông báo lỗi
					editText.setError("Thông tin này không được bỏ trống!");
					return;
				}
				// Thực hiện tiến trình con lấy thông tin trên internet
				new searchDiemThi().execute(editText.getText().toString());
			}
		});

		listViewMain
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						sinhvienEntity = sinhvienEntities.get(arg2);
						AlertDialog.Builder builder = new Builder(
								MainActivity.this);
						builder.setTitle("Thông tin chi tiết");
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append(sinhvienEntity.getSbd() + "\n"
								+ sinhvienEntity.getHoten()+"\n");
						for (String s : sinhvienEntity.getDiems()) {
							if(TextUtils.isEmpty(s))
								continue;
							stringBuilder.append("Đ" + s + "\n");
						}
						stringBuilder.append(sinhvienEntity.getTongdiem());
						builder.setMessage(stringBuilder);
						builder.setPositiveButton("OK", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								arg0.cancel();
							}
						});
						builder.create().show();
					}
				});
	}

	/*
	 * Tiến trình con, lấy thông tin trả về từ internet Phải đưa vào 1 tiến
	 * trình riêng nếu không sẽ bị trôi, vì việc lấy thông tin từ internet mất
	 * thời gian quá 1 nhịp lệnh
	 */
	public class searchDiemThi extends AsyncTask<String, Void, String> {
		// Dialog hiện lên trong quá trình chờ
		private ProgressDialog dialog;
		private boolean flag = false;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			// Khởi tạo dialog và show()
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("Đang tải...");
			dialog.setCancelable(false);
			dialog.show();
		}

		// Công việc chính của tiến trình
		@Override
		protected String doInBackground(String... params) {
			// Lựa chọn url theo item spinner được chọn
			// chọn dòng 0 thì tìm theo sbd, 1 theo họ tên
			String stringUrl = ((spinner.getSelectedItemPosition() == 0) ? MainActivity.URL_SEARCH_SBD
					: MainActivity.URL_SEARCH_HOTEN)
					+ params[0];
			try {
				// Thư viện kết nối và lấy data trả về từ request GET tới 1 url
				Document document = Jsoup.connect(stringUrl).timeout(10000)
						.get();
				// Lấy đoạn text trong thẻ body
				String res = document.text().trim();
				// Nếu không có dữ liệu trả về null
				if (res.trim().equals("Khong co du lieu")) {
					flag = false;
					return null;
				}// Nếu có dữ liệu
				flag = true;
				// Trả lại phương thức onPostExecute
				return res;
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// result là string được trả lại từ phương thức
			super.onPostExecute(result);
			// ẩn dialog
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (flag == false) {
				Toast.makeText(getApplicationContext(),
						"Không tìm thấy thông tin!", Toast.LENGTH_SHORT).show();
				sinhvienEntities.clear();
				adapterMain.notifyDataSetChanged();
			}
			if (result != null) {
				// Hiện thị kết quả lên textView
				sinhvienEntities.clear();
				if (spinner.getSelectedItemPosition() == 1) {
					String[] thisinhString = result.replace("|", "-")
							.split("-");
					for (String thisinh : thisinhString) {
						if(TextUtils.isEmpty(thisinh))
							continue;
						Log.d("thisnh", thisinh);
						String[] arrStr = thisinh.split(",");
						Log.d("hoten", arrStr[1]);
						SinhvienEntity entity = new SinhvienEntity();
						entity.setSbd(thisinh.split(",")[0].trim());
						entity.setHoten(thisinh.split(",")[1].trim());
						entity.setTongdiem(thisinh.split(",")[3].trim());
						entity.setDiems(thisinh.split(",")[2].split(" Đ"));
						sinhvienEntities.add(entity);
					}
					adapterMain.notifyDataSetChanged();
				} else {// tim theo sbd
					SinhvienEntity entity = new SinhvienEntity();
					entity.setSbd(result.split(",")[0].trim());
					entity.setHoten(result.split(",")[1].trim());
					entity.setTongdiem(result.split(",")[3].trim());
					entity.setDiems(result.split(",")[2].split(" Đ"));
					sinhvienEntities.add(entity);
					adapterMain.notifyDataSetChanged();
				}
				// textViewKq.setText(result.replace(", ", "\n").trim()
				// .replace("|", "\n").trim().replace(" Đ", "\nĐ"));
			}
		}
	}

	// Khởi tạo menu có nut để chuyển vào activity bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Khởi tạo actionBar menu, mẫu trong file res/menu/main.xml
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Xử lý khi có 1 menu được click
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// Item xem biểu đồ được lựa chọn
		if (item.getItemId() == R.id.action_bar) {
			// Khởi chạy activity BarActivity để tạo biểu đồ
			// Intent intent = new Intent(MainActivity.this, BarActivity.class);
			Intent intent = new Intent(MainActivity.this,
					StatisticActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
