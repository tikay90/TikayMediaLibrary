package com.tikay.medialibrary.adapters;

/*******************************
 Created by Tenkorang Alex
 31/8/2015
 *******************************/

import android.content.Context;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.TracksModel;
import java.util.ArrayList; 

public class CustomAdapter extends BaseAdapter implements Filterable
{

  private ArrayList<TracksModel> songList;
	ArrayList<TracksModel> filterList;
  private Context context;
	ValueFilter valueFilter = new ValueFilter();


	static class ViewHolder
	{
		public ImageView ivThumbnail;
		public TextView songName, artistName,albumName,songDuration;
	}


  public CustomAdapter(Context cntxt, ArrayList<TracksModel> list) {
		context = cntxt;
		songList = list;
		filterList = list;
  }



  @Override
  public int getCount() {
		return songList.size();
  }

  @Override
  public Object getItem(int position) {
		return songList.get(position);
  }

  @Override
  public long getItemId(int position) {
		return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
		//	Utilities utils = new Utilities();
		final ViewHolder viewHolder;
		View view = convertView;

		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.tracks_details, parent, false);

			//set up view holder
			viewHolder = new ViewHolder();
			viewHolder.songName = (TextView)view.findViewById(R.id.tvSong_title);
			viewHolder.albumName = (TextView)view.findViewById(R.id.tvSong_album);
			viewHolder.songDuration = (TextView)view.findViewById(R.id.tvSongDuration);
			viewHolder.artistName = (TextView)view.findViewById(R.id.tvSong_artist);
			viewHolder.ivThumbnail = (ImageView)view.findViewById(R.id.img_list_file);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}


		final TracksModel item = songList.get(position);
		//ThumbnailTask thumb = new ThumbnailTask(position, viewHolder, item.getSongFullPath());

		if (item != null) {
			viewHolder.songName.setText(item.getSongName());
			viewHolder.albumName.setText(item.getSongAlbumName());
			viewHolder.songDuration.setText(item.getSongDuration());
			viewHolder.artistName.setText(songList.get(position).getSongArtist() + " - " +  Formatter.formatFileSize(context, item.getSongSize()));
			//viewHolder.ivThumbnail.setImageURI(item.getSongUri());

			try {
				//Uri uri = item.getSongUri();
				//ImageUtils.loadThumbnails(uri.toString(), viewHolder.ivThumbnail, context);
			} catch (Exception e) {
				e.printStackTrace();
			}


		}
		return view;
  }

  public void setSongsList(ArrayList<TracksModel> list) {
		songList = list;
		this.notifyDataSetChanged();
  }

	@Override
	public Filter getFilter() {
		if (valueFilter == null) {
			valueFilter = new ValueFilter();
		}
		return valueFilter;
	}



	// Filter class
	private class ValueFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

			if (constraint != null && constraint.length() > 0) {
				filterList = new ArrayList<TracksModel>();
				for (int i = 0; i < songList.size(); i++) {

					TracksModel dataNames = songList.get(i);
					constraint = constraint.toString().toLowerCase();

					if (dataNames.getSongName().toString().toLowerCase().contains(constraint)) {
						filterList.add(dataNames);
					}
				}
				results.count = filterList.size();
				results.values = filterList;
			} else {
				results.count = songList.size();
				results.values = songList;
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// Now we have to inform the adapter about the new list filtered
			if (results.count == 0)
        notifyDataSetInvalidated();
			else {
        songList = (ArrayList<TracksModel>) results.values;
				notifyDataSetChanged();
			}
		}

	}

}
