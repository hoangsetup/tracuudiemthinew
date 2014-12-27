package com.longnd.tracuudiemthi;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<SinhvienEntity> {
	private int layout;
	private Vector<SinhvienEntity> entities = new Vector<SinhvienEntity>();
	private Context context;

	public MyListAdapter(Context context, int resource,
			Vector<SinhvienEntity> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.layout = resource;
		this.entities = objects;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layout, null);
		}
		TextView sbd = (TextView) convertView.findViewById(R.id.textView_sbd);
		TextView hoten = (TextView) convertView.findViewById(R.id.textView_hoten);
		TextView tongdiem = (TextView) convertView.findViewById(R.id.textView_tongdiem);
		
		SinhvienEntity entity = entities.get(position);
		sbd.setText(entity.getSbd().split(":")[1].trim());
		hoten.setText(entity.getHoten().split(":")[1].trim());
		tongdiem.setText(entity.getTongdiem().split(":")[1].trim());
		
		return convertView;
	}

}