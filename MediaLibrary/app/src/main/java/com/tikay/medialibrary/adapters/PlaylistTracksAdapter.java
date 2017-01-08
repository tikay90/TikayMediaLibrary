package com.tikay.medialibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.PlaylistTrackModel;
import com.tikay.medialibrary.utils.ImageUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.Locale; 

public class PlaylistTracksAdapter extends BaseAdapter 
{
  private ArrayList<PlaylistTrackModel> songList = null;
  private ArrayList<PlaylistTrackModel> list = null;
  private ArrayList<PlaylistTrackModel> arraylist;
  private Context context;
  private int pos = 0;
	//CircleImageView civ;


  static class ViewHolder
  {
		private ImageView ivPlaylistThumbnail;
		private TextView name,artist,duration,album;
		private CircleImageView civ;

  }


  public PlaylistTracksAdapter(Context cntxt, ArrayList<PlaylistTrackModel> songList) {
		context = cntxt;
		this.list = songList;
		this.songList = songList;

		this.arraylist = new ArrayList<PlaylistTrackModel>();
		this.arraylist.addAll(songList);
		//civ = new CircleImageView(context);
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
			view = LayoutInflater.from(context).inflate(R.layout.playlist_tracks_details, parent, false);

			//set up view holder
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView)view.findViewById(R.id.tv_ptd_title);
			viewHolder.artist = (TextView)view.findViewById(R.id.tv_ptd_artist);
			viewHolder.duration = (TextView)view.findViewById(R.id.tv_ptd_duration);
			viewHolder.album = (TextView)view.findViewById(R.id.tv_ptd_album);
			viewHolder.ivPlaylistThumbnail = (ImageView)view.findViewById(R.id.iv_ptd);
			//viewHolder.civ = (CircleImageView)view.findViewById(R.id.civ_ptd);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}


		final PlaylistTrackModel item = songList.get(position);

		viewHolder.name.setText(item.getName());
		viewHolder.artist.setText(item.getArtist());
		viewHolder.album.setText(item.getAlbum());
		viewHolder.duration.setText(item.getDuration());
		Glide
			.with(context)
			.load(item.getAlbumArt())
			.asBitmap()
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.into(new SimpleTarget<Bitmap>() {  
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
					viewHolder.ivPlaylistThumbnail.setImageBitmap(bitmap);
				}
				@Override
				public void onLoadFailed(Exception e, Drawable d) {
					viewHolder.ivPlaylistThumbnail.setImageResource(R.drawable.music_m_logo);
					//holder.rl.setBackgroundResource(R.drawable.album_default);
				}
			});
		//Bitmap bmp2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.new_york);
		/*
		try {
			ImageUtils.displayRoundImage(item.getAlbumArt(), viewHolder.ivPlaylistThumbnail);
		} catch(Exception e) {
			e.printStackTrace();
		}
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
			for(PlaylistTrackModel sl : arraylist) {
				if(sl.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
					//sl.setID(pos);
					list.add(sl);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<PlaylistTrackModel> list) {
		songList = list;
		this.notifyDataSetChanged();
  }

}


