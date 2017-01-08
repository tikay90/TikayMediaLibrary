package com.tikay.medialibrary.utils;

/**
 * Click listener for popup menu items
 */
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.Toast;
import com.tikay.medialibrary.R;
import android.content.Context;

class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener
{
	static Context context;
	static MyMenuItemClickListener myMenuItemClickListener = null;
	
	private MyMenuItemClickListener(Context contxt) {
		this.context = contxt;
	}

	public static MyMenuItemClickListener getInstance(Context ctx){
		if(myMenuItemClickListener==null){
			myMenuItemClickListener = new MyMenuItemClickListener(ctx);
		}
		
		return myMenuItemClickListener;
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem menuItem) {
		switch(menuItem.getItemId()) {
			case R.id.action_favourite:
				Toast.makeText(context, "Add to favourite", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.action_next:
				Toast.makeText(context, "Play next", Toast.LENGTH_SHORT).show();
				return true;
			default:
		}
		return false;
	}
}

