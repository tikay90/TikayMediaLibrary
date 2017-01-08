package com.tikay.medialibrary.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tikay.medialibrary.R;

public abstract class AbstractMasterFolder extends Fragment
{

	private View v;
	public abstract Fragment createFragment();
	public abstract int getLayoutResId();
	public abstract void getOrientation();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(getLayoutResId(), container, false);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initManager();
	}

	private	void initManager() {
		FragmentManager fm = getChildFragmentManager();//getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Fragment Fragment = fm.findFragmentById(R.id.fragment_content);
		if(Fragment == null) {
			ft = fm.beginTransaction();
			ft.add(R.id.fragment_content, createFragment());
			ft.commit();
		}

	}


}
