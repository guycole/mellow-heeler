package com.digiburo.mellow.heeler.app;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.DataBaseTableIf;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.database.SortieTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scrolling list of sorties.
 * Short press display sortie track on google map
 * Long press supports sortie deletion
 */
public class SortieFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final Logger LOG = LoggerFactory.getLogger(SortieFragment.class);
  public static final String FRAGMENT_TAG = "TAG_SORTIE";
  public static final int LOADER_ID = 314156;

  private SortieCursorAdapter adapter;
  private MainListener mainListener;

  /**
   * LoaderCallback
   */
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    DataBaseTableIf table = new SortieTable();

    String[] projection = table.getDefaultProjection();
    String orderBy = table.getDefaultSortOrder();

    String selection = null;
    String[] selectionArgs = null;

    CursorLoader loader = new CursorLoader(getActivity(), SortieTable.CONTENT_URI, projection, selection, selectionArgs, orderBy);
    return loader;
  }

  /**
   * LoaderCallback
   */
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    adapter.swapCursor(data);
    adapter.notifyDataSetChanged();
  }

  /**
   * LoaderCallback
   */
  public void onLoaderReset(Loader<Cursor> loader) {
    adapter.swapCursor(null);
  }

  /**
   * row selection
   */
  @Override
  public void onListItemClick(ListView listView, View view, int position, long id) {
    LOG.debug("click:" + position + ":" + id);

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
    SortieModel sortieModel = dataBaseFacade.selectSortie(id, getActivity());
    mainListener.displaySortieDetail(sortieModel.getSortieUuid());
  }

  /**
   * mandatory empty ctor
   */
  public SortieFragment() {
    //empty
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mainListener = (MainListener) activity;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SortieTable sortieTable = new SortieTable();
    adapter = new SortieCursorAdapter(getActivity(), sortieTable.getDefaultProjection());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_sortie, container, false);
    return (view);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setHasOptionsMenu(false);
    setListAdapter(adapter);

    getLoaderManager().initLoader(LOADER_ID, null, this);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    setListAdapter(null);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mainListener = null;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */