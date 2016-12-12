package com.tikay.medialibrary.recycler_adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.MyViewHolder>
{
	private ArrayList<TracksModel> songList = null;
  private ArrayList<TracksModel> list = null;
  private ArrayList<TracksModel> arraylist;
  private Context context;
	OnMenuItemClickListener onMenuItemClickListener;
	private String TRACK_NAME;
	View v;
	private LayoutInflater inflater = null;

	public class MyViewHolder extends RecyclerView.ViewHolder implements OnCreateContextMenuListener,OnMenuItemClickListener
	{
		public ImageView ivThumbnail;
		public TextView songName, artistName,albumName,songDuration;

		public MyViewHolder(View view) {
			super(view);
			v = view;
			view.setOnCreateContextMenuListener(this);
			
			songName = (TextView)view.findViewById(R.id.tvSong_title);
			albumName = (TextView)view.findViewById(R.id.tvSong_album);
			songDuration = (TextView)view.findViewById(R.id.tvSongDuration);
			artistName = (TextView)view.findViewById(R.id.tvSong_artist);
			ivThumbnail = (ImageView)view.findViewById(R.id.img_list_file);
		}

		private String[] menuItems = {"Add To Playlist","Rename","Delete","Details"};
		private String[] menuItems2 = {"Default","Favourite","HipHop","Gospel"};

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			try {
				MenuItem menuItem = null;
				String name = songList.get(getAdapterPosition()).getSongName();
				TRACK_NAME = name;
				menu.setHeaderTitle(name);
				//Utilities.toastShort(context, PlaylistUtils.getPlaylistID(context,name)+"");
				//Utilities.toastShort(context, name);
				for(int i = 0; i < menuItems.length; i++) {
					menuItem = menu.add(Menu.NONE, i, i, menuItems[i]);
					menuItem.setOnMenuItemClickListener(this);//onMenuItemClickListener);
				}

			} catch(Exception e) {
				Utilities.toastLong(context, e.toString());
				e.printStackTrace();
			}
		}

		@Override
    public boolean onMenuItemClick(MenuItem item) {
			String name = "";
			switch(item.getOrder()) {
				case 0:
					try{
					showPopup(context,v);
					}catch(Exception e){
						Log.e("Error....",e.toString());
					}
					name = item.getTitle().toString();
					break;

				case 1:
					name = item.getTitle().toString();
					break;

				case 2:
					name = item.getTitle().toString();
					break;

				case 3:
					name = item.getTitle().toString();
					break;
			}

			Utilities.toastShort(context, name);
      return true;
    }

		/*
		private void displayPopupWindow(Context context) {
			PopupWindow popup = new PopupWindow(DemoWindowActivity.this);
			View layout = context.getLayoutInflater().inflate(R.layout.popup_content, null);
			popup.setContentView(layout);
			// Set content width and height
			popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
			popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
			// Closes the popup window when touch outside of it - when looses focus
			popup.setOutsideTouchable(true);
			popup.setFocusable(true);
			// Show anchored to button
			popup.setBackgroundDrawable(new BitmapDrawable());
			popup.showAsDropDown(anchorView);
		}
		*/
		
		private void showPopup(Context context, View v) {
			PopupMenu menu = new PopupMenu(context, v);
			menu.getMenu().add(Menu.NONE, 1, 1, "Share");
			menu.getMenu().add(Menu.NONE, 2, 2, "Comment");
			menu.show();

			menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {

						int i = item.getItemId();
						if(i == 1) {
							//handle share
							return true;
						} else if(i == 2) {
							//handle comment
							return true;
						} else {
							return false;
						}
					} 

				});
		}
		
	}

	public TracksAdapter(Context context, ArrayList<TracksModel> songList) {
		this.context = context;
		this.list = songList;
		this.songList = songList;
		inflater = LayoutInflater.from(context);
		this.arraylist = new ArrayList<TracksModel>();
		this.arraylist.addAll(songList);
  }

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = inflater.inflate(R.layout.tracks_details, parent, false);

		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		final TracksModel item = songList.get(position);
		holder.songName.setText(item.getSongName());
		//holder.songName.set
		holder.albumName.setText(item.getSongAlbumName());
		holder.songDuration.setText(item.getSongDuration());
		holder.artistName.setText(item.getSongArtist() + " - " +  Formatter.formatFileSize(context, item.getSongSize()));
		//ImageUtils.displayRoundImage(item.getSongAlbumArtPath(), holder.ivThumbnail);
		Glide
			.with(context)
			.load(item.getSongAlbumArtPath())
			.asBitmap()
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.into(new SimpleTarget<Bitmap>() {  
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
					holder.ivThumbnail.setImageBitmap(bitmap);
				}
				@Override
				public void onLoadFailed(Exception e, Drawable d) {
					holder.ivThumbnail.setImageResource(R.drawable.music_m_logo);
					//holder.rl.setBackgroundResource(R.drawable.album_default);
				}
			});
	}

	@Override
	public int getItemCount() {
		return songList.size();
	}



  // Filter method
  public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		songList.clear();
		if(charText.length() == 0) {
			songList.addAll(arraylist);
		} else {
			for(TracksModel sl : arraylist) {
				if(sl.getSongName().toLowerCase(Locale.getDefault()).contains(charText)) {
					list.add(sl);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<TracksModel> list) {
		songList = list;
		this.notifyDataSetChanged();
  }


}
