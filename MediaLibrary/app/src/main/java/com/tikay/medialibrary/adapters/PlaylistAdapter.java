package com.tikay.medialibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.PlaylistModel;
import java.util.ArrayList;
import java.util.Locale; 

public class PlaylistAdapter extends BaseAdapter 
{
  private ArrayList<PlaylistModel> songList = null;
  private ArrayList<PlaylistModel> list = null;
  private ArrayList<PlaylistModel> arraylist;
  private Context context;
  private int pos = 0;

  static class ViewHolder
  {
		public ImageView ivPlaylistThumbnail;
		public TextView playlistName,playlistTracks;
  }


  public PlaylistAdapter(Context cntxt, ArrayList<PlaylistModel> songList) {
		context = cntxt;
		this.list = songList;
		this.songList = songList;

		this.arraylist = new ArrayList<PlaylistModel>();
		this.arraylist.addAll(songList);
  }

  @Override
  public boolean hasStableIds() {
		// TODO: Implement this method
		return true;
  }


  @Override
  public int getCount() {
		return songList.size();
  }


  @Override // here changes
  public Object getItem(int position) {
		//songList.get(position).setRealPosition(position);
		return songList.get(position);
  }


  @Override
  public long getItemId(int position) {
		return position;
  }


  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		View view = convertView;
		pos = position;

		if(view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.playlist_details, parent, false);

			//set up view holder
			viewHolder = new ViewHolder();
			viewHolder.playlistName = (TextView)view.findViewById(R.id.tv_playlist_name);
			viewHolder.playlistTracks = (TextView)view.findViewById(R.id.tv_playlist_tracks);
			viewHolder.ivPlaylistThumbnail = (ImageView)view.findViewById(R.id.iv_playlist);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}


		final PlaylistModel item = songList.get(position);

		viewHolder.playlistName.setText(item.getName());
		//viewHolder.playlistTracks.setText(item.getTracks()+"\n"+item.getDateAdded()+"\n"+item.getDateModified()+"\n"+item.getPath());
		String s = item.getTracks();// + a>1?"Tracks":"Track";
		if(s != null) {
			int a = Integer.parseInt(s);
			if(a > 1) {
				s = s + " Tracks";
			} else {
				s = s + " Track";
			}
		}else{
			s= "No Tracks";
		}
		viewHolder.playlistTracks.setText(s);
		try {
			//Bitmap bmp = item.getAlbumImg();
			//Bitmap bmp2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
			viewHolder.ivPlaylistThumbnail.setImageResource(R.drawable.ic_launcher);
		} catch(Exception e) {
			e.printStackTrace();
		}

		/*
		view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Toast.makeText(context, item.getID() + "   " + item.getName(), Toast.LENGTH_SHORT).show();

				}
			});
			*/

		return view;
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
					//sl.setID(pos);
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


