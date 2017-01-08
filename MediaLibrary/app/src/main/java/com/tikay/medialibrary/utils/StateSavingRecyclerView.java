package com.tikay.medialibrary.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Class {@link StatefulRecyclerView} extends {@link RecyclerView} and adds position management on configuration changes.
 *
 * @author FrantisekGazo
 * @version 2016-03-15
 */
public final class StateSavingRecyclerView extends RecyclerView {

	private static final String SAVED_SUPER_STATE = "super-state";
	private static final String SAVED_LAYOUT_MANAGER = "layout-manager-state";

	private Parcelable mLayoutManagerSavedState;

	private String LOG = "StateSavingRecyclerView";

	public StateSavingRecyclerView(Context context) {
		super(context);
	}

	public StateSavingRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public StateSavingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Log.i(LOG,"In StateSavingRecyclerView: onSaveInstanceState() CALLED");
		Bundle bundle = new Bundle();
		bundle.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState());
		bundle.putParcelable(SAVED_LAYOUT_MANAGER, this.getLayoutManager().onSaveInstanceState());
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			mLayoutManagerSavedState = bundle.getParcelable(SAVED_LAYOUT_MANAGER);
			state = bundle.getParcelable(SAVED_SUPER_STATE);
		}
		super.onRestoreInstanceState(state);
		Log.i(LOG,"In StateSavingRecyclerView: onRestoreInstanceState() CALLED");
	}

	/**
	 * Restores scroll position after configuration change.
	 * <p>
	 * <b>NOTE:</b> Must be called after adapter has been set.
	 */
	private void restorePosition() {
		if (mLayoutManagerSavedState != null) {
			this.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
			mLayoutManagerSavedState = null;
		}
	}

	@Override
	public void setAdapter(Adapter adapter) {
		super.setAdapter(adapter);
		restorePosition();
	}
}
