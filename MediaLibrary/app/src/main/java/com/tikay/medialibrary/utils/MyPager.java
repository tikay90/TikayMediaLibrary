package com.tikay.medialibrary.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;

public class MyPager extends FragmentStatePagerAdapter
{
	List<Fragment> listFrags;
	/** my adapter **/
	MyPager(FragmentManager fm, List<Fragment> listFragments) {
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
