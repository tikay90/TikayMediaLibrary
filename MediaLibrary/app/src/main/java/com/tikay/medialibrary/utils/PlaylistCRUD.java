package com.tikay.medialibrary.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.utils.PlaylistUtils;
import com.tikay.medialibrary.utils.Utilities;
import com.tikay.medialibrary.fragments.Playlists;

public class PlaylistCRUD
{
	private static String TAG="PlaylistCRUD";
	static Context context;
	
	
	
	public static void createPlaylistDialog(Context mContext) {
		context = mContext;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("New Playlist");
		//builder.setMessage("Enter Playlist Name");
		final EditText etInput = new EditText(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
		etInput.setLayoutParams(lp);
		etInput.setHint("Enter Playlist Name");
		etInput.setHintTextColor(Color.BLUE);
		builder.setView(etInput);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface p1, int p2) {
					try {
						String playlistName = etInput.getText().toString().trim();
						//createPlaylist(playlistName);
						if(playlistName.trim().length()>0) {
							PlaylistUtils.createPlaylists(context.getApplicationContext(), playlistName);
							Log.i(TAG, "added " + playlistName);

							//new PlaylistTask().execute();
							//Playlists pl = new Playlists();
							//pl.new PlaylistTask().execute();
							Playlists.PlaylistTask plt = new Playlists().new PlaylistTask();
							plt.execute();
						}else{

						}
					} catch(Exception e) {
						Log.e(TAG, e.toString());
						Utilities.toastLong(context.getApplicationContext(), e.toString());
						e.printStackTrace();
					}
				}
			});

		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					// TODO: Implement this method
				}
			});

		builder.show();
	}
	
}
