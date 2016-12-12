package com.tikay.medialibrary.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment implements OnTabChangeListener,OnPageChangeListener
{
	private TabHost tabHost;
	private ViewPager viewPager;
	private MyFragPageAdapter myViewPagerAdapter;
	int i = 0;
	View v;
	List<Fragment> fragments;
	int selectedTab;
	int pos = 0;

	private String TAG = "FragmentHome";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(Home.class.getSimpleName(), "IN " + Home.class.getSimpleName() + "  onCreate() CALLED");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.i(Home.class.getSimpleName(), "IN " + Home.class.getSimpleName() + "  onCreateView() CALLED");
		v = inflater.inflate(R.layout.home, container, false);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		try {
			// init ViewPager
			initializeViewPager();
			// init tabhost
			initializeTabHost();

			restorePrefs();
		} catch(Exception e) {
			Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			Log.i(TAG, "In " + Home.class.getSimpleName() + " " + e.getMessage());
		}
	}

	// fake content for tabhost
	private	class FakeContent implements TabContentFactory
	{
		private final Context mContext;
		public FakeContent(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumHeight(0);
			v.setMinimumWidth(0);
			return v;
		}
	}

	@Override
	public void onPause() {
		//Utilities.toastShort(getContext(), "HomeFragment ==> onPause() CALLED");
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		//Utilities.toastShort(getContext(), "HomeFragment  ==> onSaveInstanceState() CALLED");
		super.onSaveInstanceState(outState);
	}

	private void initializeViewPager() {
		try {
			fragments = new ArrayList<Fragment>();
			fragments.add(new Tracks());
			fragments.add(new Albums());
			fragments.add(new Artists());
			//fragments.add(new Folders());
			fragments.add(new Playlists());

			myViewPagerAdapter = new MyFragPageAdapter(getChildFragmentManager(), fragments);
			viewPager = (ViewPager) v.findViewById(R.id.view_pager);
			viewPager.setAdapter(myViewPagerAdapter);
			viewPager.setOnPageChangeListener(this);
		} catch(Exception e) {
			//Utilities.toastLong(getActivity().getApplicationContext(), e.getMessage());
			Log.e("Home", " ERROR ----  " + e.getMessage());
		}

	}


	// initialize tabhost
	private void initializeTabHost() {
		try {
			tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
			tabHost.setup();
			tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Tracks").setContent(new FakeContent(getActivity())));
			tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Albums").setContent(new FakeContent(getActivity())));
			tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Artists").setContent(new FakeContent(getActivity())));
			tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("Folders").setContent(new FakeContent(getActivity())));
			tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator("Playlists").setContent(new FakeContent(getActivity())));

			/*
			 for(int i = 1; i <= fragments.size(); i++) {
			 TabHost.TabSpec tabSpec;
			 tabSpec = tabHost.newTabSpec("Tab " + i);
			 tabSpec.setIndicator("Tab " + i);
			 tabSpec.setContent(new FakeContent(getActivity()));
			 tabHost.addTab(tabSpec);
			 }*/
			//mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Tab1"),Tab1Fragment.class, null);
			tabHost.setOnTabChangedListener(this);
		} catch(Exception e) {
			//Utilities.toastLong(getActivity().getApplicationContext() , e.getMessage());
			Log.e("Home", " ERROR ----  " + e.getMessage());
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		pos = tabHost.getCurrentTab();

		HorizontalScrollView hScrollView = (HorizontalScrollView) v.findViewById(R.id.h_scroll_view);
		View tabView = tabHost.getCurrentTabView();
		int scrollPos = (tabView.getLeft() - (hScrollView.getWidth() - tabView.getWidth())) / 2;
		hScrollView.smoothScrollTo(scrollPos, 0);
		viewPager.setCurrentItem(pos);

		//Utilities.toastShort(getActivity(),"Tab selected = "+pos);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		tabHost.setCurrentTab(position);
		//Utilities.toastShort(getActivity(),"Page selected = "+position);
	}

	@Override
	public void onStop() {
		try {
			//Utilities.toastShort(getContext(), "HomeFragment ==> onStop() CALLED");
			Log.i(Home.class.getSimpleName(), "IN " + Home.class.getSimpleName() + "  onStop() CALLED");

			// Save selected Tab position
			SharedPreferences preferences = getActivity().getSharedPreferences(Home.class.getSimpleName(), getActivity().MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();

			editor.putInt("selectedTab", selectedTab);
			editor.putInt("pos", pos);

			editor.commit();
		} catch(Exception e) {
			Log.e(TAG, "Home " + e.getMessage());
			Utilities.toastLong(getContext(), TAG + " " + e.getMessage());
		}
		super.onStop();
	}

	private void restorePrefs() {
		// Get selected Tab position
    SharedPreferences preferences = getActivity().getSharedPreferences(Home.class.getSimpleName(), getActivity().MODE_PRIVATE);
		selectedTab = preferences.getInt("selectedTab", 0);
		pos = preferences.getInt("pos", 0);
		viewPager.setCurrentItem(pos);
	}


	@Override
	public void onResume() {
		//Utilities.toastShort(getContext(), "HomeFragment ==> onResume() CALLED");
		Log.i(Home.class.getSimpleName(), "IN " + Home.class.getSimpleName() + " onResume() CALLED");
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		Log.i(Home.class.getSimpleName(), "IN " + Home.class.getSimpleName() + " onDestroyView() CALLED");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.i(Home.class.getSimpleName(), "IN " + Home.class.getSimpleName() + " onDestroy() CALLED");
		super.onDestroy();
	}

	private class MyFragPageAdapter extends FragmentPagerAdapter
	{
		List<Fragment> listFrags;

		/** my adapter **/
		MyFragPageAdapter(FragmentManager fm, List<Fragment> listFragments) {
			super(fm);
			this.listFrags = listFragments;
		}

		@Override
		public Fragment getItem(int position) {

			return listFrags.get(position);
		}

		@Override
		public int getCount() {
			// TODO: Implement this method
			return listFrags.size();
		}


	}

}
