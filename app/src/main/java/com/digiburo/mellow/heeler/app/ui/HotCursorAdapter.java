package com.digiburo.mellow.heeler.app.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.database.HotModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;

/**
 * @author gsc
 */
public class HotCursorAdapter extends SimpleCursorAdapter {
  private Context context;

  /**
   *
   * @param context
   */
  public HotCursorAdapter(Context context, String[] projection) {
    super(context, R.layout.row_hot, null, projection, null, 0);
    this.context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final HotModel currentModel = readFromCursor(position);
    if (currentModel == null) {
      throw new IllegalArgumentException("readFromCursor:" + position + ":failure noted");
    }

    View view;
    ViewHolder holder;

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.row_hot, null);

      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      view = convertView;
      holder = (ViewHolder) view.getTag();
    }

    holder.tvSsid.setText(currentModel.getSsid());
    holder.tvBssid.setText(currentModel.getBssid());

    return view;
  }

  private HotModel readFromCursor(int position) {
    Cursor cursor = getCursor();
    if (!cursor.moveToPosition(position)) {
      return null;
    }

    HotModel result = new HotModel();
    result.setDefault();

    try {
      result.fromCursor(cursor);
    } catch(Exception exception) {
      return null;
    }

    return result;
  }

  class ViewHolder {
    ViewHolder(View view) {
      tvSsid = (TextView) view.findViewById(R.id.textSsid);
      tvBssid = (TextView) view.findViewById(R.id.textBssid);
    }

    private TextView tvSsid;
    private TextView tvBssid;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on Jun 22, 2014 by gsc
 */