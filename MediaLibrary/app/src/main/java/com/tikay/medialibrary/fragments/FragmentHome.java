package com.tikay.medialibrary.fragments;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.fragments.Home;
import java.util.List;

public class FragmentHome extends Fragment
{
	private String TAG = "FragmentHome";
	private View v;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(FragmentHome.class.getSimpleName(), "IN " + Home.class.getSimpleName() + "  onCreate() CALLED");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.i(FragmentHome.class.getSimpleName(), "IN " + Home.class.getSimpleName() + "  onCreateView() CALLED");
		v = inflater.inflate(R.layout.frag_home, container, false);

		return v;
	}




	private class MyFragPageAdapter extends FragmentStatePagerAdapter
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
