package com.tikay.medialibrary.recycler_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.db.DbConstants;
import com.tikay.medialibrary.models.PlaylistModel;
import com.tikay.medialibrary.utils.AnimUtils;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>
{
	private ArrayList<PlaylistModel> songList = null;
  private ArrayList<PlaylistModel> list = null;
  private ArrayList<PlaylistModel> arraylist;
  private Context context;
	private LayoutInflater inflater = null;
	public static String PLAYLIST_NAME = "";
	OnMenuItemClickListener onMenuItemClickListener;

	private int previousPosition = 0;

	public PlaylistAdapter(Context context, ArrayList<PlaylistModel> songList, OnMenuItemClickListener onMenuItemClickListener) {
		this.context = context;
		this.list = songList;
		this.songList = songList;
		this.onMenuItemClickListener = onMenuItemClickListener;

		this.arraylist = new ArrayList<PlaylistModel>();
		this.arraylist.addAll(songList);
		inflater = LayoutInflater.from(context);
  }

	public class MyViewHolder extends RecyclerView.ViewHolder implements OnCreateContextMenuListener,OnMenuItemClickListener 
	{
		public ImageView ivPlaylistThumbnail;
		public TextView playlistName,playlistTracks;

		public MyViewHolder(View view, int type) {
			super(view);
			if(type == Constants.FIRST_ROW) {
				playlistName = (TextView)view.findViewById(R.id.tv_d_playlist_name);
				playlistTracks = (TextView)view.findViewById(R.id.tv_d_playlist_tracks);
				ivPlaylistThumbnail = (ImageView)view.findViewById(R.id.iv_d_playlist);
			} else if(type == Constants.OTHER_ROW) {
				playlistName = (TextView)view.findViewById(R.id.tv_playlist_name);
				playlistTracks = (TextView)view.findViewById(R.id.tv_playlist_tracks);
				ivPlaylistThumbnail = (ImageView)view.findViewById(R.id.iv_playlist);
			}
			view.setOnCreateContextMenuListener(this);
		}

		private String[] menuItems = {"Rename","Delete","Details"};
		private String[] menuItems2 = {"Info"};
		private String[] defaultPlaylist = DbConstants.DEFAULT_PLAYLIST;



		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			try {
				MenuItem menuItem = null;
				String name = songList.get(getAdapterPosition()).getName();
				PLAYLIST_NAME = name;
				menu.setHeaderTitle(name);
				//Utilities.toastShort(context, PlaylistUtils.getPlaylistID(context,name)+"");
				//Utilities.toastShort(context, name);

				if(name.equals(defaultPlaylist[0]) || name.equals(defaultPlaylist[1]) || name.equals(defaultPlaylist[2]) || name.equals(defaultPlaylist[3])) {
					for(int i = 0; i < menuItems2.length; i++) {
						menuItem = menu.add(Menu.NONE, i, i, menuItems2[i]);
						menuItem.setOnMenuItemClickListener(onMenuItemClickListener);
					}
				} else {
					for(int i = 0; i < menuItems.length; i++) {
						menuItem = menu.add(Menu.NONE, i, i, menuItems[i]);
						menuItem.setOnMenuItemClickListener(onMenuItemClickListener);//onMenuItemClickListener);
					}
				}
			} catch(Exception e) {
				Utilities.toastLong(context, e.toString());
				e.printStackTrace();
			}
		}


		@Override
    public boolean onMenuItemClick(MenuItem item) {
			String name = "";
			String pName = songList.get(getAdapterPosition()).getName();
			switch(item.getOrder()) {
				case 0:
					name = item.getTitle().toString();
					break;

				case 1:
					name = item.getTitle().toString();
					break;

				case 2:
					name = item.getTitle().toString();
					break;
			}

			Utilities.toastShort(context, name);
			Utilities.toastShort(context, pName);
      return true;
    }


	}


	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = null;
		switch(viewType) {
			case Constants.FIRST_ROW:
				itemView =	inflater.inflate(R.layout.default_playlist_details, parent, false);
				break;
			case Constants.OTHER_ROW:
				itemView =	inflater.inflate(R.layout.playlist_details, parent, false);
				break;
		}

		return new MyViewHolder(itemView, viewType);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		final PlaylistModel item = songList.get(position);
		String tracksNo = item.getTracks() == null ?"No Tracks": item.getTracks() + " Tracks";
		holder.playlistName.setText(item.getName());
		holder.playlistTracks.setText(tracksNo);
		//ImageUtils.displayRoundImage(item.getSongAlbumArtPath(), holder.ivThumbnail);
		Glide
			.with(context)
			.load(R.drawable.ic_launcher)
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.into(holder.ivPlaylistThumbnail);

		if(position > previousPosition) { //scrolling downwards
			AnimUtils.animateRecyclerView(holder, true);
		} else { //scrolling upwards
			AnimUtils.animateRecyclerView(holder, false);
		}
		previousPosition = position;
	}

	@Override
	public int getItemCount() {
		return songList.size();
	}

	@Override
	public int getItemViewType(int position) {

		PlaylistModel item = songList.get(position);

		if(item != null)
			return item.getType();

		return super.getItemViewType(position);
	}


  // Filter method
  public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		songList.clear();
		if(charText.length() == 0) {
			songList.addAll(arraylist);
		} else {
			for(PlaylistModel sl : arraylist) {
				if(sl.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
					list.add(sl);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<PlaylistModel> list) {
		songList = list;
		this.notifyDataSetChanged();
  }




}
