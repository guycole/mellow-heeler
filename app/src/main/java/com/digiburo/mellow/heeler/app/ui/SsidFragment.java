package com.digiburo.mellow.heeler.app.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.digiburo.mellow.heeler.R;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.DataBaseTableIf;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scrolling list of SSID
 */
public class SsidFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final Logger LOG = LoggerFactory.getLogger(SsidFragment.class);

  public static final int CONTEXT_ITEM_1 = Menu.FIRST;
  public static final String FRAGMENT_TAG = "TAG_SSID";
  public static final int LOADER_ID = 2718;

  private SsidCursorAdapter adapter;
  private MainListener mainListener;
  private DataBaseFacade dataBaseFacade;

  /**
   * LoaderCallback
   */
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    DataBaseTableIf table = new ObservationTable();

    String[] projection = table.getDefaultProjection();
    String orderBy = table.getDefaultSortOrder();

    String selection = null;
    String[] selectionArgs = null;

    CursorLoader loader = new CursorLoader(getActivity(), ObservationTable.CONTENT_URI, projection, selection, selectionArgs, orderBy);
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
    ObservationModel observationModel = dataBaseFacade.selectObservation(id, getActivity());
    mainListener.displayObservationDetail(observationModel.getObservationUuid());
  }

  /**
   * long click
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
    menu.add(0, CONTEXT_ITEM_1, 0, R.string.menu_context_hot_list);
  }

  /**
   * service context menu
   * @param item
   * @return
   */
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    LOG.debug("on context item select:" + item + ":" + item.getItemId() + ":" + info.id);

    switch(item.getItemId()) {
      case CONTEXT_ITEM_1:
        ObservationModel observationModel = dataBaseFacade.selectObservation(info.id, getActivity());
        mainListener.addHot(observationModel);
        break;
    }

    return super.onContextItemSelected(item);
  }

  /**
   * mandatory empty ctor
   */
  public SsidFragment() {
    //empty
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mainListener = (MainListener) activity;
    dataBaseFacade = new DataBaseFacade(activity);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ObservationTable observationTable = new ObservationTable();
    adapter = new SsidCursorAdapter(getActivity(), observationTable.getDefaultProjection());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_ssid, container, false);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setListAdapter(adapter);
    registerForContextMenu(getListView());

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
