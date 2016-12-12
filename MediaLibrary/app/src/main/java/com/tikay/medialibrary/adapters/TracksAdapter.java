package com.tikay.medialibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.text.format.Formatter;
import android.util.Log;
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
import com.tikay.medialibrary.models.TracksModel;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import com.tikay.medialibrary.utils.ImageUtils; 

public class TracksAdapter extends BaseAdapter 
{
  private ArrayList<TracksModel> songList = null;
  private ArrayList<TracksModel> list = null;
  private ArrayList<TracksModel> arraylist;
  private Context context;
  private int pos = 0;

  static class ViewHolder
  {
		public ImageView ivThumbnail;
		public CircleImageView civ;
		public TextView songName, artistName,albumName,songDuration;
  }


  public TracksAdapter(Context cntxt, ArrayList<TracksModel> songList) {
		context = cntxt;
		this.list = songList;
		this.songList = songList;

		this.arraylist = new ArrayList<TracksModel>();
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

		viewHolder.songName.setText(item.getSongName());
		viewHolder.albumName.setText(item.getSongAlbumName());
		viewHolder.songDuration.setText(item.getSongDuration());
		viewHolder.artistName.setText(item.getSongArtist() + " - " +  Formatter.formatFileSize(context, item.getSongSize()));
		/*
		Glide
			.with(context)
			.load(item.getSongAlbumArtPath())
			.asBitmap()
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			//.transform(new CircleTransform(mContext))
			.placeholder(R.drawable.music_m_logo)
			.error(R.drawable.music_m_logo)
			.into(new SimpleTarget<Bitmap>() {  
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
					// do something with the bitmap
					// for demonstration purposes, let's just set it to an ImageView
					viewHolder.ivThumbnail.setImageBitmap(bitmap);
					try {
						if(bitmap != null) { 
							Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
									@Override
									public void onGenerated(Palette palette) {
										Palette.Swatch textSwatch = palette.getVibrantSwatch();
										int defaultColor = 0x000000;
										int vibrant = palette.getVibrantColor(defaultColor);
										int vibrantLight = palette.getLightVibrantColor(defaultColor);
										int vibrantDark = palette.getDarkVibrantColor(defaultColor);
										int muted = palette.getMutedColor(defaultColor);
										int mutedLight = palette.getLightMutedColor(defaultColor);
										int mutedDark = palette.getDarkMutedColor(defaultColor);

										if(textSwatch != null) {
											//Toast.makeText(mContext, "Null swatch :(", Toast.LENGTH_SHORT).show();
											//holder.rl.setBackgroundColor(textSwatch.getRgb());
											//holder.title.setTextColor(textSwatch.getTitleTextColor());
											//holder.count.setTextColor(textSwatch.getBodyTextColor());
											//holder.rl.setBackgroundColor(vibrant);
											//holder.title.setTextColor(vibrantLight);
											//holder.count.setTextColor(vibrantDark);
										}
									}
								});
						}
					} catch(Exception e) {
						Log.e("ADAPTER", "ADAPTER >>> error @  " + e.getMessage());
					}
				}
			});*/

		try {
			//ImageUtils.loadRoundedThumbnails(item.getSongAlbumArtPath(), viewHolder.ivThumbnail, context);
			ImageUtils.displayRoundImage(item.getSongAlbumArtPath(), viewHolder.ivThumbnail);
			//new AdapterTask(Uri.parse(item.getSongAlbumArtPath()),viewHolder.ivThumbnail,context); not working
		} catch(Exception e) {
			//e.printStackTrace();
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
			for(TracksModel sl : arraylist) {
				if(sl.getSongName().toLowerCase(Locale.getDefault()).contains(charText)) {
					sl.setSongId(pos);
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


	class AdapterTask extends AsyncTask<Void,Integer,Bitmap>
	{
		private Uri uri;
		private ImageView iv;
		Context context;

		public AdapterTask(Uri uri, ImageView iv, Context context) {
			this.uri = uri;
			this.iv = iv;
			this.context = context;
		}




		@Override
		protected void onPreExecute() {
			// TODO: Implement this method
			super.onPreExecute();
		}


		@Override
		protected Bitmap doInBackground(Void[] p1) {
			InputStream image_stream = null;
			try {
				image_stream = context.getContentResolver().openInputStream(uri);
				Bitmap bitmap= BitmapFactory.decodeStream(image_stream);
				image_stream.close();
				if(bitmap != null) {
					return bitmap;
				}
			} catch(FileNotFoundException e) {} catch(IOException e) {}


			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if(result != null)
				iv.setImageBitmap(result);
		}



	}


}


