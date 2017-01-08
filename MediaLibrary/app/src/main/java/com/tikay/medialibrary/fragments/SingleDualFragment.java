package com.tikay.medialibrary.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.utils.Utilities;
import android.support.annotation.LayoutRes;

public class SingleDualFragment extends Fragment
{
	private String TAG = SingleDualFragment.class.getSimpleName();
	private View v;
	boolean mDualPane;
	int mCurCheckPosition = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utilities.writeLogcatToFile(TAG);
		Log.i(TAG, "IN SingleDualFragment - onCreate() CALLED");
		//setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(getLayoutResId(), container, false);
		//v = inflater.inflate(R.layout.single_dual, container, false);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		try {
			initStuff();

		} catch(Exception e) {
			Log.e(TAG, e.toString());
			Utilities.toastLong(getContext(), "SingleDualFragment <<<>>>  " + e.toString());
		}
		checkOrientation();
	}

	@LayoutRes
	private int getLayoutResId() {
		return R.layout.activity_masterdetail;
	}

	private void checkOrientation() {
		if(v.findViewById(R.id.dual_pane) == null) {
			Utilities.toastShort(getActivity(), "single pane");
		} else {
			Utilities.toastShort(getActivity(), "dual pane");
		}
	}

	private void initStuff() {
		try {
			//View detailsFrame = getActivity().findViewById(R.id.dual_pane);

			// Check that a view exists and is visible
			// A view is visible (0) on the screen; the default value.
			// It can also be invisible and hidden, as if the view had not been
			// added.
			//
			//mDualPane = detailsFrame != null
			//&& detailsFrame.getVisibility() == View.VISIBLE;
			FolderContent folderList = new FolderContent();
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fragment_content, folderList);
			ft.commit();
			/*
			 if(mDualPane) {
			 Utilities.toastShort(getActivity(), "dual pane");
			 } else {
			 Utilities.toastShort(getActivity(), "single pane");
			 }
			 */
		} catch(Exception e) {
			Log.e(TAG, "In " + TAG + ": " + e.toString()); 
		}

	}




}
