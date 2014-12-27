package com.longnd.tracuudiemthi;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StatisticActivity extends Activity {
	// Linh url de truyen vao tham so luachon lay ve du lieu ve bieu do
	public static String URL_DATA = "http://long.nvttest.com/stile/pages/dulieubieudo.jsp?luachon=";
	// public static String URL_DATA =
	// "http://10.0.3.2:8080/tracuudiem/pages/dulieubieudo.jsp?luachon="; //
	// dung cho localhost
	private int margins[] = { 50, 50, 50, 50 };
	// Khai báo Mảng giá trị đầu vào
	private int[] arrsosinhvien = new int[1500];
	private float[] arrdiemthi = new float[1500];
	private String param = "";
	private static String title = "";

	LinearLayout layout;
	// Tạo XYSeriesRenderer để tùy chỉnh các giá trị
	private XYSeriesRenderer BarRenderer, LineRenderer;
	// Khởi tạo một đối tượng XYMultipleSeriesRenderer để tùy chỉnh biểu đồ theo
	// ý muốn
	private XYMultipleSeriesRenderer multiRenderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic);
		layout = (LinearLayout) findViewById(R.id.chart);
		// Khởi tạo hộp thoại lựa chọn tiêu chí vẽ bd ngay lúc khởi tạo
		Intent intent = new Intent(StatisticActivity.this, SubBarActivity.class);
		startActivityForResult(intent, 100);
	}

	// Bắt sự kiện khi hộp thoại lựa chọn bị đóng lại (SubActivity)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// Lấy thông tin từ hộp thoại gửi về
		if (resultCode != 111)// Bị đóng mà không phải nhấn nút xong
		{
			Log.d("CC_trace", "111");
			return;
		}
		// Hộp thoại được đóng với thông tin truyền trở lại
		param = data.getStringExtra("res");
		int index = data.getIntExtra("int", 0);
		if (index == 0) {
			title = "Biểu đồ: Phân bố điểm các môn thi";
		} else if (index == 1) {
			title = "Biểu đồ: Phân bố tổng điểm theo số thí sinh";
		} else {
			title = "Biểu đồ: Phân bố điểm môn "
					+ getResources().getStringArray(R.array.textHienthi)[index]
					+ " theo số thí sinh";
		}
		// Khởi chạy tiến trình lấy thông tin cho biểu đồ
		arrdiemthi = new float[1500];
		arrsosinhvien = new int[1500];
		layout.removeAllViews();
		new drawChartThread().execute(URL_DATA + param);
	}

	public class GraphCombination {

		private Context context;

		public GraphCombination(Context context) {
			this.context = context;
		}

		public void drawChart() {
			// Khởi tạo XYSeries là số điểm trên đồ thị
			XYSeries lowestTempSeries = new XYSeries("");

			int count = 0;
			for (int i : arrsosinhvien) {
				if (i != 0) {
					count++;// dem tu mang phan tu arrsosinhvien nhung mang co
							// gia tri > 0
				}
			}

			for (int i = 0; i < count; i++) {
				lowestTempSeries.add(i, arrsosinhvien[i]); // Thêm dữ liệu vào
															// số điểm trên đồ
															// thị
			}
			// Khởi tạo 1 dataset để quản lý tất cả các giá trị
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			// Thêm tất cả thông tin số thí sinh vào dataset
			dataset.addSeries(lowestTempSeries);
			dataset.addSeries(lowestTempSeries);
			// tuy chinh cac thanh
			setLowestTempBarRenderer();
			// tuy chinh thuoc tinh cac duong
			setLowestTempLineRenderer();
			setMultiRenderer();

			for (int i = 0; i < count; i++) {
				multiRenderer.addXTextLabel(i, arrdiemthi[i] + "");
			}
			// Thêm thanh and đường vào multipleRenderer
			multiRenderer.addSeriesRenderer(BarRenderer);
			multiRenderer.addSeriesRenderer(LineRenderer);

			// Tạo biểu đồ
			GraphicalView view = ChartFactory.getCombinedXYChartView(context,
					dataset, multiRenderer, new String[] { BarChart.TYPE,
							LineChart.TYPE });

			layout.removeAllViews();
			// Add cái biểu đồ này vào LinearLayout của xml
			layout.addView(view, 0, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

		}

		// để tùy chỉnh các giá trị cot bieu do
		private void setLowestTempBarRenderer() {

			BarRenderer = new XYSeriesRenderer();
			BarRenderer.setColor(Color.parseColor("#FF7700"));// set mau cam
			BarRenderer.setFillPoints(true);// Đổ đầy chấm
			BarRenderer.setLineWidth(1);// Độ rộng dòng
			BarRenderer.setChartValuesTextAlign(Align.RIGHT);// hien thi text ve
																// can phai
			BarRenderer.setChartValuesTextSize(25f);// co chu text
			BarRenderer.setDisplayChartValues(true);// Cho phép hiển thị giá trị
		}

		private void setMultiRenderer() {

			multiRenderer = new XYMultipleSeriesRenderer();
			multiRenderer.setChartTitle(title);// Thiết lập title
			multiRenderer.setXTitle("Điểm số");// Title trục X
			multiRenderer.setYTitle("Số sinh viên");// Title trục Y

			multiRenderer.setXAxisMin(-1);// thiết lập với giá trị min của trục
											// x

			int c = 0, max = arrsosinhvien[0];
			for (int i : arrsosinhvien) {
				if (i != 0) {
					c++;
				}
				max = (i > max) ? i : max;
			}

			multiRenderer.setXAxisMax(c);// thiết lập với giá trị max của trục x
			multiRenderer.setYAxisMin(0);// thiết lập với giá trị mim của trục y
			multiRenderer.setYAxisMax((max > 1) ? max + (max / 2) : 2);// thiết
																		// lập
																		// với
																		// giá
																		// trị
			// max của trục y
			// set co chu cho cac label, cot, ..
			multiRenderer.setLabelsTextSize(25f);
			multiRenderer.setLegendTextSize(20f);
			multiRenderer.setAxisTitleTextSize(25f);
			multiRenderer.setMargins(margins);
			multiRenderer.setChartTitleTextSize(30f);

			multiRenderer.setApplyBackgroundColor(true);// cho hien thi thuoc
														// tinh mau chu
			multiRenderer.setBackgroundColor(Color.WHITE);// set mau chu
			multiRenderer.setYLabelsAlign(Align.RIGHT);// Chữ nằm về phía bên
														// phải của cột
			multiRenderer.setBarSpacing(1);// khoang cach bar
			multiRenderer.setZoomButtonsVisible(false);// khong cho zoom
			multiRenderer.setPanEnabled(false);
			multiRenderer.setXLabels(0);

		}

		// gia tri duong noi cac diem
		private void setLowestTempLineRenderer() {
			LineRenderer = new XYSeriesRenderer();
			LineRenderer.setColor(Color.parseColor("#0400FF"));// set mau xanh
																// noi cac diem
			LineRenderer.setFillPoints(true);// Đổ đầy chấm
			LineRenderer.setLineWidth(4);// Độ rộng dòng
			LineRenderer.setChartValuesTextAlign(Align.CENTER);// hien thi text
																// can giua
			LineRenderer.setChartValuesTextSize(15f);// set co chu
			LineRenderer.setDisplayChartValues(false);// khong Cho phép hiển thị
														// giá trị
		}
	}

	public class drawChartThread extends AsyncTask<String, Void, String> {
		// dialog hiện lên trong quá trình lấy tin
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// Khởi tạo vào show dialog
			dialog = new ProgressDialog(StatisticActivity.this);
			dialog.setMessage("Đang tải...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				// Lấy dữ liệu
				Document document = Jsoup.connect(arg0[0]).timeout(10000).get();
				String res = document.text().trim();
				return res;
			} catch (Exception ex) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			try {
				// Lấy dữ liệu thành công
				if (result != null && !result.equals("Khong co du lieu")) {
					if (param.endsWith("mon")) {
						// Tách string theo ký tự |
						String[] infoTongHop = result.split("@");
						// String dulieudiem = infoTongHop[0];
						// String dulieubieudo = infoTongHop[1].toString();

						// Hien thi bieu do muon ve
						// (new GraphCombination(StatisticActivity.this))
						// .drawChartDSMon(dulieudiem, dulieubieudo);
						drawLineMon(result);
					} else {
						// Tách string theo ký tự |
						String[] info = result.replace('|', '-').split("-");

						int i = 0;
						for (String s : info) {
							// số người đạt điểm
							int songuoi = Integer.parseInt(s.split(",")[0]
									.trim());
							// Điểm
							String diem = s.split(",")[1].trim();
							arrdiemthi[i] = Float.parseFloat(diem);
							arrsosinhvien[i] = songuoi;
							i++;
						}
						// Hien thi bieu do muon ve
						(new GraphCombination(StatisticActivity.this))
								.drawChart();
					}
				} else {
					Toast.makeText(StatisticActivity.this, "Không có dữ liệu",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception ex) {
				Toast.makeText(StatisticActivity.this, ex.toString(),
						Toast.LENGTH_LONG).show();
				ex.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.chart_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.action_bar) {
			Intent intent = new Intent(StatisticActivity.this,
					SubBarActivity.class);
			startActivityForResult(intent, 100);
		}
		return super.onOptionsItemSelected(item);
	}

	// ///////////////////26-12-2014
	public void drawLineMon(String dataString) {
		XYMultipleSeriesDataset mDataset = getDemoDataset(dataString);

		// Lấy số môn(số dòng cần vẽ)
		int countLine = dataString.split("@")[1].split("-").length;

		XYMultipleSeriesRenderer mRenderer = getMonRenderer(countLine,
				dataString);
		GraphicalView mChartView;
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setAxisTitleTextSize(16f);
		mRenderer.setChartTitleTextSize(20f);
		mRenderer.setLabelsTextSize(15f);
		mRenderer.setLegendTextSize(15f);
		mRenderer.setMargins(margins);
		// mRenderer.setZoomButtonsVisible(true);
		mRenderer.setPointSize(10);

		mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
		layout.addView(mChartView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}

	private XYMultipleSeriesDataset getDemoDataset(String dataString) {
		// Lấy các điểm thi
		String[] diemthis = dataString.split("-")[0].replace("|", "-").split(
				"-");
		Log.d("-----------", diemthis.length + "");
		// Lấy ra thông tin các môn + điểm + số người
		String[] diemthiSonguoi = dataString.split("@")[1].split("-");

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		for (int i = 0; i < diemthiSonguoi.length; i++) {
			Log.d("ádhf", diemthiSonguoi[i]);
			// Mon học Tên môn + điểm Dia/1,7|1,10|
			String Mon = diemthiSonguoi[i];
			// Lấy tên môn
			XYSeries firstSeries = new XYSeries(Mon.split("/")[0]);
			// {"1,7","1,10"}
			String[] diemmon = Mon.split("/")[1].replace("|", "-").split("-");
			// Chứa thông tin điểm - người
			HashMap<Float, Integer> map1 = new HashMap<Float, Integer>();
			for (int j = 0; j < diemmon.length; j++) {
				map1.put(Float.parseFloat(diemmon[j].split(",")[1].trim()),
						Integer.parseInt(diemmon[j].split(",")[0].trim()));
			}
			for (int k = 0; k < diemthis.length; k++) {
				firstSeries.add(
						Float.parseFloat(diemthis[k]),
						map1.containsKey(Float.parseFloat(diemthis[k])) ? map1
								.get(Float.parseFloat(diemthis[k])) : 0);
			}
			dataset.addSeries(firstSeries);
		}

		Log.d("Solkuong", dataset.getSeries().length + "");
		return dataset;
	}

	private XYMultipleSeriesRenderer getMonRenderer(int count, String dataString) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		// Lấy các điểm thi
		// String[] diemthis = dataString.split("-")[0].replace("|", "-").split(
		// "-");
		XYSeriesRenderer r = new XYSeriesRenderer();
		Random random = new Random();
		// Count là số môn thi
		for (int i = 0; i < count; i++) {
			int rb = random.nextInt(255);
			int b = random.nextInt(255);
			int g = random.nextInt(255);
			renderer.setMargins(margins);
			r = new XYSeriesRenderer();
			r.setPointStyle(PointStyle.CIRCLE);
			r.setColor(Color.rgb(rb, b, g));
			r.setFillPoints(true);
			r.setLineWidth(2);
			r.setChartValuesTextSize(25f);
			r.setDisplayChartValues(true);
			renderer.addSeriesRenderer(r);

		}
		for (int i = 0; i < 10; i++) {
			renderer.addXTextLabel(i + 0.5, (i + 0.5) + "");
		}
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setLabelsColor(Color.LTGRAY);

		renderer.setChartTitle("Biểu đồ môn thi");// Thiết lập title
		renderer.setXTitle("Điểm số");// Title trục X
		renderer.setYTitle("Số thí sinh");// Title trục Y
		renderer.setYAxisAlign(Align.LEFT, 0);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setXAxisMax(10.5f);
		renderer.setLabelsTextSize(25f);
		renderer.setLegendTextSize(20f);
		renderer.setAxisTitleTextSize(25f);
		renderer.setMargins(margins);
		renderer.setChartTitleTextSize(30f);
		return renderer;
	}
}
