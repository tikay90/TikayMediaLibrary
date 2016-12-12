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
import com.tikay.medialibrary.models.ArtistModel;
import java.util.ArrayList;
import java.util.Locale; 

public class ArtistAdapter extends BaseAdapter 
{
  private ArrayList<ArtistModel> artistList = null;
  private ArrayList<ArtistModel> list = null;
  private ArrayList<ArtistModel> arraylist;
  private Context context;
  private int pos = 0;

  static class ViewHolder
  {
		private ImageView ivArtistThumbnail;
		private TextView artistName,artistTracks,artistAlbums;
  }


  public ArtistAdapter(Context cntxt, ArrayList<ArtistModel> artistList) {
		context = cntxt;
		this.list = artistList;
		this.artistList = artistList;

		this.arraylist = new ArrayList<ArtistModel>();
		this.arraylist.addAll(artistList);
  }

  @Override
  public boolean hasStableIds() {
		// TODO: Implement this method
		return true;
  }


  @Override
  public int getCount() {
		return artistList.size();
  }


  @Override // here changes
  public Object getItem(int position) {
		//songList.get(position).setRealPosition(position);
		return artistList.get(position);
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
			view = LayoutInflater.from(context).inflate(R.layout.artist_details, parent, false);

			//set up view holder
			viewHolder = new ViewHolder();
			viewHolder.artistName = (TextView)view.findViewById(R.id.tv_artist_name);
			viewHolder.artistAlbums = (TextView)view.findViewById(R.id.tv_artist_albums);
			viewHolder.artistTracks = (TextView)view.findViewById(R.id.tv_artist_tracks);
			viewHolder.ivArtistThumbnail = (ImageView)view.findViewById(R.id.iv_artist);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}

		final ArtistModel item = artistList.get(position);
		
		String s = item.getArtistTracks();
		int a = Integer.parseInt(s);
		String num = "";
		if(a > 0) {
			num = a > 1 ? a + " Tracks": a + " Track";
		} else {
			num = "No Tracks";
		}
		
		try {
			//Bitmap bmp = item.getAlbumImg();
			//Bitmap bmp2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
			//viewHolder.ivArtistThumbnail.setImageResource(R.drawable.blue_music);
			viewHolder.artistName.setText(item.getArtistName());
			viewHolder.artistTracks.setText(getNumber(item.getArtistTracks(),item,"Track"));
			viewHolder.artistAlbums.setText(getNumber(item.getArtistAlbums(),item,"Album"));
			Glide
				.with(context)
				.load(R.drawable.blue_music)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(viewHolder.ivArtistThumbnail);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return view;
  }

	private String getNumber(String input, ArtistModel item,String name) {
		int a = Integer.parseInt(input);
		String num = "";
		if(a > 0) {
			num = a > 1 ? a + " "+name+"s": a + " "+name;
		} else {
			num = "No "+name;
		}
		return num;
	}


  // Filter method
  public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		artistList.clear();
		if(charText.length() == 0) {
			artistList.addAll(arraylist);
		} else {
			for(ArtistModel sl : arraylist) {
				if(sl.getArtistName().toLowerCase(Locale.getDefault()).contains(charText)) {
					//sl.setID(pos);
					list.add(sl);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<ArtistModel> list) {
		artistList = list;
		this.notifyDataSetChanged();
  }

}


