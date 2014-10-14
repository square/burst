/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.robolectric.shadows;


import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that exposes data from a {@link android.database.Cursor Cursor} to a
 * {@link android.widget.ListView ListView} widget. The Cursor must include
 * a column named "_id" or this class will not work.
 */
@Implements(CursorAdapter.class)
public class ShadowCursorAdapter extends ShadowBaseAdapter {
  @RealObject CursorAdapter realCursorAdapter;

  private List<View> views = new ArrayList<View>();

  @Implementation
  public View getView(int position, View convertView, ViewGroup parent) {
    // if the cursor is null OR there are no views to dispense return null
    if (this.mCursor == null || views.size() == 0 ) {
      return null;
    }

    if (convertView != null) {
      return convertView;
    }

    return views.get(position);
  }

  /**
   * Non-Android API.  Set a list of views to be returned for successive
   * calls to getView().
   *
   * @param views
   */
  public void setViews(List<View> views) {
    this.views = views;
  }

  /**
   * This field should be made private, so it is hidden from the SDK.
   * {@hide}
   */
  protected boolean mDataValid;
  /**
   * This field should be made private, so it is hidden from the SDK.
   * {@hide}
   */
  protected boolean mAutoRequery;
  /**
   * This field should be made private, so it is hidden from the SDK.
   * {@hide}
   */
  protected Cursor mCursor;
  /**
   * This field should be made private, so it is hidden from the SDK.
   * {@hide}
   */
  protected Context mContext;
  /**
   * This field should be made private, so it is hidden from the SDK.
   * {@hide}
   */
  protected int mRowIDColumn;
  /**
   * This field should be made private, so it is hidden from the SDK.
   * {@hide}
   */
  protected ChangeObserver mChangeObserver;
  /**
   * This field should be made private, so it is hidden from the SDK.
   * {@hide}
   */
  protected DataSetObserver mDataSetObserver = new MyDataSetObserver();
//    /**
//     * This field should be made private, so it is hidden from the SDK.
//     * {@hide}
//     */
//    protected CursorFilter__FromAndroid mCursorFilter;
  /**
   * This field should be made private, so it is hidden from the SDK.
   * {@hide}
   */
  protected FilterQueryProvider mFilterQueryProvider;

  /**
   * Constructor. The adapter will call requery() on the cursor whenever
   * it changes so that the most recent data is always displayed.
   *
   * @param c       The cursor from which to get the data.
   * @param context The context
   */
  public void __constructor__(Context context, Cursor c) {
    initialize(context, c, true);
  }

  /**
   * Constructor
   *
   * @param c           The cursor from which to get the data.
   * @param context     The context
   * @param autoRequery If true the adapter will call requery() on the
   *                    cursor whenever it changes so the most recent
   *                    data is always displayed.
   */
  public void __constructor__(Context context, Cursor c, boolean autoRequery) {
    initialize(context, c, autoRequery);
  }

  // renamed from Android source so as not to conflict with RobolectricWiringTest
  private void initialize(Context context, Cursor c, boolean autoRequery) {
    boolean cursorPresent = c != null;
    mAutoRequery = autoRequery;
    mCursor = c;
    mDataValid = cursorPresent;
    mContext = context;
    mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
    mChangeObserver = new ChangeObserver();
    if (cursorPresent) {
      c.registerContentObserver(mChangeObserver);
      c.registerDataSetObserver(mDataSetObserver);
    }
  }

  /**
   * Returns the cursor.
   *
   * @return the cursor.
   */
  @Implementation
  public Cursor getCursor() {
    return mCursor;
  }

  /**
   * @see android.widget.ListAdapter#getCount()
   */
  @Implementation
  public int getCount() {
    if (mDataValid && mCursor != null) {
      return mCursor.getCount();
    } else {
      return 0;
    }
  }

  /**
   * @see android.widget.ListAdapter#getItem(int)
   */
  @Implementation
  public Object getItem(int position) {
    if (mDataValid && mCursor != null) {
      mCursor.moveToPosition(position);
      return mCursor;
    } else {
      return null;
    }
  }

  /**
   * @see android.widget.ListAdapter#getItemId(int)
   */
  @Implementation
  public long getItemId(int position) {
    if (mDataValid && mCursor != null) {
      this.mCursor.getColumnIndexOrThrow("_id");
      if (mCursor.moveToPosition(position)) {
        return mCursor.getLong(mRowIDColumn);
      } else {
        return 0;
      }
    } else {
      return 0;
    }
  }

  @Implementation
  public boolean hasStableIds() {
    return true;
  }

//  /**
//   * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
//   */
//  @Implementation
//  public View getView(int position, View convertView, ViewGroup parent) {
//    if (!mDataValid) {
//      throw new IllegalStateException("this should only be called when the cursor is valid");
//    }
//    if (!mCursor.moveToPosition(position)) {
//      throw new IllegalStateException("couldn't move cursor to position " + position);
//    }
//    View v;
//    if (convertView == null) {
//      v = newView(mContext, mCursor, parent);
//    } else {
//      v = convertView;
//    }
//    bindView(v, mContext, mCursor);
//    return v;
//  }
//
//  @Implementation
//  public View getDropDownView(int position, View convertView, ViewGroup parent) {
//    if (mDataValid) {
//      mCursor.moveToPosition(position);
//      View v;
//      if (convertView == null) {
//        v = newDropDownView(mContext, mCursor, parent);
//      } else {
//        v = convertView;
//      }
//      bindView(v, mContext, mCursor);
//      return v;
//    } else {
//      return null;
//    }
//  }
//
//  /**
//   * Makes a new view to hold the data pointed to by cursor.
//   * @param context Interface to application's global information
//   * @param cursor The cursor from which to get the data. The cursor is already
//   * moved to the correct position.
//   * @param parent The parent to which the new view is attached to
//   * @return the newly created view.
//   */
//  public abstract View newView(Context context, Cursor cursor, ViewGroup parent);
//
//  /**
//   * Makes a new drop down view to hold the data pointed to by cursor.
//   * @param context Interface to application's global information
//   * @param cursor The cursor from which to get the data. The cursor is already
//   * moved to the correct position.
//   * @param parent The parent to which the new view is attached to
//   * @return the newly created view.
//   */
//  @Implementation
//  public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
//    return newView(context, cursor, parent);
//  }
//
//  /**
//   * Bind an existing view to the data pointed to by cursor
//   * @param view Existing view, returned earlier by newView
//   * @param context Interface to application's global information
//   * @param cursor The cursor from which to get the data. The cursor is already
//   * moved to the correct position.
//   */
//  public abstract void bindView(View view, Context context, Cursor cursor);

  /**
   * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
   * closed.
   *
   * @param cursor the new cursor to be used
   */
  @Implementation
  public void changeCursor(Cursor cursor) {
    if (cursor == mCursor) {
      return;
    }
    if (mCursor != null) {
      mCursor.unregisterContentObserver(mChangeObserver);
      mCursor.unregisterDataSetObserver(mDataSetObserver);
      mCursor.close();
    }
    mCursor = cursor;
    if (cursor != null) {
      cursor.registerContentObserver(mChangeObserver);
      cursor.registerDataSetObserver(mDataSetObserver);
      mRowIDColumn = cursor.getColumnIndexOrThrow("_id");
      mDataValid = true;
      // notify the observers about the new cursor
      realCursorAdapter.notifyDataSetChanged();
    } else {
      mRowIDColumn = -1;
      mDataValid = false;
      // notify the observers about the lack of a data set
      realCursorAdapter.notifyDataSetInvalidated();
    }
  }

  /**
   * <p>Converts the cursor into a CharSequence. Subclasses should override this
   * method to convert their results. The default implementation returns an
   * empty String for null values or the default String representation of
   * the value.</p>
   *
   * @param cursor the cursor to convert to a CharSequence
   * @return a CharSequence representing the value
   */
  @Implementation
  public CharSequence convertToString(Cursor cursor) {
    return cursor == null ? "" : cursor.toString();
  }

  /**
   * Runs a query with the specified constraint. This query is requested
   * by the filter attached to this adapter.
   * <p/>
   * The query is provided by a
   * {@link android.widget.FilterQueryProvider}.
   * If no provider is specified, the current cursor is not filtered and returned.
   * <p/>
   * After this method returns the resulting cursor is passed to {@link #changeCursor(Cursor)}
   * and the previous cursor is closed.
   * <p/>
   * This method is always executed on a background thread, not on the
   * application's main thread (or UI thread.)
   * <p/>
   * Contract: when constraint is null or empty, the original results,
   * prior to any filtering, must be returned.
   *
   * @param constraint the constraint with which the query must be filtered
   * @return a Cursor representing the results of the new query
   * @see #getFilter()
   * @see #getFilterQueryProvider()
   * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
   */
  @Implementation
  public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
    if (mFilterQueryProvider != null) {
      return mFilterQueryProvider.runQuery(constraint);
    }

    return mCursor;
  }

//    @Implementation
//    public Filter getFilter() {
//        if (mCursorFilter == null) {
//            mCursorFilter = new CursorFilter__FromAndroid(this);
//        }
//        return mCursorFilter;
//    }

  /**
   * Returns the query filter provider used for filtering. When the
   * provider is null, no filtering occurs.
   *
   * @return the current filter query provider or null if it does not exist
   * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
   * @see #runQueryOnBackgroundThread(CharSequence)
   */
  @Implementation
  public FilterQueryProvider getFilterQueryProvider() {
    return mFilterQueryProvider;
  }

  /**
   * Sets the query filter provider used to filter the current Cursor.
   * The provider's
   * {@link android.widget.FilterQueryProvider#runQuery(CharSequence)}
   * method is invoked when filtering is requested by a client of
   * this adapter.
   *
   * @param filterQueryProvider the filter query provider or null to remove it
   * @see #getFilterQueryProvider()
   * @see #runQueryOnBackgroundThread(CharSequence)
   */
  @Implementation
  public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
    mFilterQueryProvider = filterQueryProvider;
  }

  /**
   * Called when the {@link ContentObserver} on the cursor receives a change notification.
   * The default implementation provides the auto-requery logic, but may be overridden by
   * sub classes.
   *
   * @see ContentObserver#onChange(boolean)
   */
  // renamed from Android source so as not to conflict with RobolectricWiringTest
  protected void onContentChangedInternal() {
    if (mAutoRequery && mCursor != null && !mCursor.isClosed()) {
      if (Config.LOGV) Log.v("Cursor", "Auto requerying " + mCursor + " due to update");
      mDataValid = mCursor.requery();
    }
  }

  private class ChangeObserver extends ContentObserver {
    public ChangeObserver() {
      super(new Handler());
    }

    @Override
    public boolean deliverSelfNotifications() {
      return true;
    }

    @Override
    public void onChange(boolean selfChange) {
      onContentChangedInternal();
    }
  }

  private class MyDataSetObserver extends DataSetObserver {
    @Override
    public void onChanged() {
      mDataValid = true;
      realCursorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInvalidated() {
      mDataValid = false;
      realCursorAdapter.notifyDataSetInvalidated();
    }
  }

}