package com.tikay.medialibrary.recycler_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.tikay.medialibrary.utils.AnimUtils;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class FolderSPAdapter extends RecyclerView.Adapter<FolderSPAdapter.MyViewHolder>
{
	private ArrayList<FolderModel> folderList = null;
  private ArrayList<FolderModel> list = null;
  private ArrayList<FolderModel> arraylist;
	private Intent broadcastIntent;
  private Context context;
	private String TAG ="FolderAdapter";
	private int previousPosition = 0;

	public FolderSPAdapter(Context cntxt, ArrayList<FolderModel> folderList,Intent intent) {
		context = cntxt;
		this.list = folderList;
		this.folderList = folderList;
		broadcastIntent = intent;

		this.arraylist = new ArrayList<FolderModel>();
		this.arraylist.addAll(folderList);
  }

	public class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener
	{
		public ImageView ivThumbnail;
		public TextView folderName, artistName,albumName,songDuration,folderPath;

		public MyViewHolder(View view) {
			super(view);
			view.setOnClickListener(this);
			folderName = (TextView)view.findViewById(R.id.tvFolder_Song_title);
			folderPath = (TextView)view.findViewById(R.id.tvFolderPath);
			ivThumbnail = (ImageView)view.findViewById(R.id.iv_folder_img);
		}

		@Override
		public void onClick(View v) {
			try {
				File musicFile = new File(folderList.get(getAdapterPosition()).getFolderPath());  
				//Utilities.toastShort(getActivity(), musicFile.getAbsolutePath().toString());

				try {
					Constants.CURRENT_FOLDER_TRACKS.clear();
					Constants.CURRENT_FOLDER_TRACKS.addAll(Constants.FOLDER_TRACKS);
					//Log.e("Folder", "recentFolderTracks --- number : " + Constants.CURRENT_FOLDER_TRACKS.size());
					broadcastIntent.putExtra(Constants.SEND, "folder_track_position")
						.putExtra("folder_position", Constants.CURRENT_FOLDER_TRACKS.get(getAdapterPosition()).getFolderPosition());
					//Utilities.toastShort(getActivity(), "position: " + pos);
					String name1 = musicFile.getName();
					String name = name1.substring(0, name1.lastIndexOf('.'));
					//Log.i("In onItemClick - Name : " + "File Playing", name1);
					Utilities.toastShort(context,  name);
					context.sendBroadcast(broadcastIntent);
					//String bitrate = Utilities.getBitrate(pathToSong);
					//Utilities.toastShort(getActivity(), "" + bitrate + " KBPS  mp3");
				} catch(Exception e) {
					Utilities.toastShort(context, "Invalid file  " + e.toString());
				}

			} catch(Exception e2) {
				Utilities.toastShort(context, "In onItemClick(): " + e2.toString());
				Log.e("In FolderPort: onItemClick - Exception : Error message", e2.getMessage());
			}
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
		holder.folderPath.setText(item.getFolderShortPath() == null ? "No path" : item.getFolderShortPath());
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

		if(position > previousPosition) { //scrolling downwards
			AnimUtils.animateRecyclerView(holder, true);
		} else { //scrolling upwards
			AnimUtils.animateRecyclerView(holder, false);
		}
		previousPosition = position;
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

	public void swap(ArrayList<FolderModel> newList){
		if (folderList != null) {
			folderList.clear();
			folderList.addAll(newList);
		}
		else {
			folderList = newList;
		}
		notifyDataSetChanged();
		Utilities.toastShort(context,"recyclerview swapped");
	}



}
