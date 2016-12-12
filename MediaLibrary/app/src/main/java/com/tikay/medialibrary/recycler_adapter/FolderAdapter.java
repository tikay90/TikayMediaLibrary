package com.tikay.medialibrary.recycler_adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.ArtistModel;
import com.tikay.medialibrary.models.FolderModel;
import java.util.ArrayList;
import java.util.Locale;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder>
{
	private ArrayList<FolderModel> folderList = null;
  private ArrayList<FolderModel> list = null;
  private ArrayList<FolderModel> arraylist;
  private Context context;
	private String TAG ="FolderAdapter";
	
	public FolderAdapter(Context cntxt, ArrayList<FolderModel> songList) {
		context = cntxt;
		this.list = songList;
		this.folderList = songList;

		this.arraylist = new ArrayList<FolderModel>();
		this.arraylist.addAll(songList);
  }
	
	public class MyViewHolder extends RecyclerView.ViewHolder
	{
		public ImageView ivThumbnail;
		public TextView folderName, artistName,albumName,songDuration,folderPath;

		public MyViewHolder(View view) {
			super(view);
			folderName = (TextView)view.findViewById(R.id.tvFolder_Song_title);
			folderPath = (TextView)view.findViewById(R.id.tvFolderPath);
			ivThumbnail = (ImageView)view.findViewById(R.id.iv_folder_img);
		}
	}
	
	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.folders_details, parent, false);

		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		FolderModel item = folderList.get(position);
		//holder.ivArtistThumbnail.setImageResource(R.drawable.apaawa_2);
		holder.folderName.setText(item.getFolderName());
		holder.folderPath.setText(item.getFolderPath() == null ? "No path" : item.getFolderPath());
		Glide
			.with(context)
			.load(item.getAlbumArt())
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
		//holder.ivThumbnail.setImageBitmap(item.getFolderImg());
		/*Glide
			.with(context)
			.load(item.getFolderImg())
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.into(holder.ivThumbnail);*/
	}

	@Override
	public int getItemCount() {
		return folderList.size();
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
		folderList.clear();
		if(charText.length() == 0) {
			folderList.addAll(arraylist);
		} else {
			for(FolderModel sl : arraylist) {
				if(sl.getFolderName().toLowerCase(Locale.getDefault()).contains(charText)) {
					list.add(sl);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<FolderModel> list) {
		folderList = list;
		this.notifyDataSetChanged();
  }
	
}
