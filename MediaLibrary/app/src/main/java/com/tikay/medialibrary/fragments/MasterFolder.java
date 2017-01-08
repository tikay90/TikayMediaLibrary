package com.tikay.medialibrary.fragments;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.utils.Utilities;

public class MasterFolder extends AbstractMasterFolder
{

	@Override
	public void getOrientation() {
		//checkOrientation();
	}

	

	@Override
	public Fragment createFragment() {
		
		return new FolderContent();
	}

	
	@Override
	@LayoutRes
	public int getLayoutResId() {
		// TODO: Implement this method
		return R.layout.activity_masterdetail;
	}
	
	private void checkOrientation() {
		if(getView().findViewById(R.id.dual_pane) == null) {
			Utilities.toastShort(getActivity(), "single pane");
		} else {
			Utilities.toastShort(getActivity(), "dual pane");
		}
	}

}
