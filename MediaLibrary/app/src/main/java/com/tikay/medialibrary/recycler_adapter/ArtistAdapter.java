package com.tikay.medialibrary.recycler_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.ArtistModel;
import com.tikay.medialibrary.recycler_adapter.ArtistAdapter;
import com.tikay.medialibrary.utils.AnimUtils;
import java.util.ArrayList;
import java.util.Locale;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyViewHolder>
{
	private ArrayList<ArtistModel> artistList = null;
  private ArrayList<ArtistModel> list = null;
  private ArrayList<ArtistModel> arraylist;
  private Context context;

	private int previousPosition = 0;


	public class MyViewHolder extends RecyclerView.ViewHolder
	{
		private ImageView ivArtistThumbnail;
		private TextView artistName,artistTracks,artistAlbums;

		public MyViewHolder(View view) {
			super(view);
			artistName = (TextView)view.findViewById(R.id.tv_artist_name);
			artistAlbums = (TextView)view.findViewById(R.id.tv_artist_albums);
			artistTracks = (TextView)view.findViewById(R.id.tv_artist_tracks);
			ivArtistThumbnail = (ImageView)view.findViewById(R.id.iv_artist);
		}
	}


	public ArtistAdapter(Context cntxt, ArrayList<ArtistModel> artistList) {
		context = cntxt;
		this.list = artistList;
		this.artistList = artistList;

		this.arraylist = new ArrayList<ArtistModel>();
		this.arraylist.addAll(artistList);
  }

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.artist_details, parent, false);

		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		ArtistModel item = artistList.get(position);
		//holder.ivArtistThumbnail.setImageResource(R.drawable.apaawa_2);
		holder.artistName.setText(item.getArtistName());
		holder.artistTracks.setText(getNumber(item.getArtistTracks(), item, "Track"));
		holder.artistAlbums.setText(getNumber(item.getArtistAlbums(), item, "Album"));
		//ImageUtils.displayRoundImage(item.getSongAlbumArtPath(), holder.ivThumbnail);
		Glide
			.with(context)
			.load(R.drawable.apaawa)
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.into(holder.ivArtistThumbnail);

		if(position > previousPosition) { //scrolling downwards
			AnimUtils.animateRecyclerView(holder, true);
		} else { //scrolling upwards
			AnimUtils.animateRecyclerView(holder, false);
		}
		previousPosition = position;
	}

	@Override
	public int getItemCount() {
		return artistList.size();
	}

	private String getNumber(String input, ArtistModel item, String name) {
		int a = Integer.parseInt(input);
		String num = "";
		if(a > 0) {
			num = a > 1 ? a + " " + name + "s": a + " " + name;
		} else {
			num = "No " + name;
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
