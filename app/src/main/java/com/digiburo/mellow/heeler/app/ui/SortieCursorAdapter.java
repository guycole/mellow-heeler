package com.digiburo.mellow.heeler.app.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.database.SortieModel;

/**
 * @author gsc
 */
public class SortieCursorAdapter extends SimpleCursorAdapter {
  private Context context;

  /**
   *
   * @param context
   */
  public SortieCursorAdapter(Context context, String[] projection) {
    super(context, R.layout.row_sortie, null, projection, null, 0);
    this.context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final SortieModel currentModel = readFromCursor(position);
    if (currentModel == null) {
      throw new IllegalArgumentException("readFromCursor:" + position + ":failure noted");
    }

    View view;
    ViewHolder holder;

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.row_sortie, null);

      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      view = convertView;
      holder = (ViewHolder) view.getTag();
    }

    holder.tvName.setText(currentModel.getSortieName());
    holder.tvTime.setText(currentModel.getTimeStamp());

    return view;
  }

  private SortieModel readFromCursor(int position) {
    Cursor cursor = getCursor();
    if (!cursor.moveToPosition(position)) {
      return null;
    }

    SortieModel result = new SortieModel();
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
      tvName = (TextView) view.findViewById(R.id.textSortieName01);
      tvTime = (TextView) view.findViewById(R.id.textSortieTime01);
    }

    private TextView tvName;
    private TextView tvTime;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 24, 2014 by gsc
 */