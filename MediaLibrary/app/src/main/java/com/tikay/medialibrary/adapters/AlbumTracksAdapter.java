package com.tikay.medialibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.AlbumTracksModel;
import com.tikay.medialibrary.utils.ImageUtils;
import java.util.ArrayList;
import java.util.Locale; 

public class AlbumTracksAdapter extends BaseAdapter 
{
  private ArrayList<AlbumTracksModel> songList = null;
  private ArrayList<AlbumTracksModel> list = null;
  private ArrayList<AlbumTracksModel> arraylist;
  private Context context;
  private int pos = 0;
	//CircleImageView civ;


  static class ViewHolder
  {
		private ImageView ivAlbumTracksThumbnail;
		private TextView name,artist,duration,album;

  }


  public AlbumTracksAdapter(Context cntxt, ArrayList<AlbumTracksModel> songList) {
		context = cntxt;
		this.list = songList;
		this.songList = songList;

		this.arraylist = new ArrayList<AlbumTracksModel>();
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
			view = LayoutInflater.from(context).inflate(R.layout.album_tracks_details, parent, false);

			//set up view holder
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView)view.findViewById(R.id.tv_atd_title);
			viewHolder.artist = (TextView)view.findViewById(R.id.tv_atd_artist);
			viewHolder.duration = (TextView)view.findViewById(R.id.tv_atd_duration);
			viewHolder.album = (TextView)view.findViewById(R.id.tv_atd_album);
			viewHolder.ivAlbumTracksThumbnail = (ImageView)view.findViewById(R.id.iv_atd);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}


		final AlbumTracksModel item = songList.get(position);

		viewHolder.name.setText(item.getName());
		viewHolder.artist.setText(item.getArtist());
		viewHolder.album.setText(item.getAlbum());
		viewHolder.duration.setText(item.getDuration());
		//Bitmap bmp2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.new_york);

		try {
			//viewHolder.ivPlaylistThumbnail.setImageURI(item.getArtUri());
			ImageUtils.displayRoundImage(item.getAlbumArt(), viewHolder.ivAlbumTracksThumbnail);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return view;
  }


  // Filter method
  public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		songList.clear();
		if(charText.length() == 0) {
			songList.addAll(arraylist);
		} else {
			for(AlbumTracksModel sl : arraylist) {
				if(sl.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
					//sl.setID(pos);
					list.add(sl);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<AlbumTracksModel> list) {
		songList = list;
		this.notifyDataSetChanged();
  }

}


