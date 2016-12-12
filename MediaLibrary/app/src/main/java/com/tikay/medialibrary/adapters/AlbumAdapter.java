package com.tikay.medialibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.tikay.medialibrary.models.AlbumModel;
import java.util.ArrayList;
import java.util.Locale; 

public class AlbumAdapter extends BaseAdapter 
{
  private ArrayList<AlbumModel> songList = null;
  private ArrayList<AlbumModel> list = null;
  private ArrayList<AlbumModel> arraylist;
  private Context context;
  private int pos = 0;

	private String TAG = "AlbumAdapter";

  static class ViewHolder
  {
		public ImageView ivThumbnail;
		public TextView albumArtistName,albumName,albumNumber;
  }


  public AlbumAdapter(Context cntxt, ArrayList<AlbumModel> songList) {
		context = cntxt;
		this.list = songList;
		this.songList = songList;

		this.arraylist = new ArrayList<AlbumModel>();
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
			view = LayoutInflater.from(context).inflate(R.layout.album_details, parent, false);

			//set up view holder
			viewHolder = new ViewHolder();
			viewHolder.albumName = (TextView)view.findViewById(R.id.tv_Album_Name);
			viewHolder.albumArtistName = (TextView)view.findViewById(R.id.tv_Album_Artist_Name);
			viewHolder.albumNumber = (TextView)view.findViewById(R.id.tv_albums_number);
			viewHolder.ivThumbnail = (ImageView)view.findViewById(R.id.iv_Album);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}


		final AlbumModel item = songList.get(position);

		viewHolder.albumName.setText(item.getAlbumName());
		viewHolder.albumArtistName.setText(item.getAlbumArtist());


		String s = item.getArtist_songs();// + a>1?"Tracks":"Track";
		if(s != null) {
			int a = Integer.parseInt(s);
			if(a > 1) {
				s = s + " Songs";
			} else {
				s = s + " Song";
			}
		} else {
			s = "No Songs";
		}
		//viewHolder.albumNumber.setText(item.getArtist_songs() + " Track(s)");
		viewHolder.albumNumber.setText(s);
		try {
			Glide
			.with(context)
			.load(item.getArtPath())
			.asBitmap()
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.into(new SimpleTarget<Bitmap>() {  
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
					viewHolder.ivThumbnail.setImageBitmap(bitmap);
				}
				@Override
				public void onLoadFailed(Exception e, Drawable d) {
					viewHolder.ivThumbnail.setImageResource(R.drawable.music_m_logo);
					//holder.rl.setBackgroundResource(R.drawable.album_default);
				}
			});
			/*
			Bitmap bmp = BitmapFactory.decodeFile(item.getArtPath());
			Bitmap bmp2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_logo6_ori);
			viewHolder.ivThumbnail.setImageBitmap(bmp == null ?  bmp2 : bmp);
			//ImageUtils.displayRoundImage(item.getArtPath(),viewHolder.ivThumbnail);
			//new AdapterTask(viewHolder.ivThumbnail,item.getArtPath(),context).execute();
			*/
		} catch(Exception e) {
			//e.printStackTrace();
			Log.e(TAG,TAG+ ">>>>  "+e.getMessage());
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
			for(AlbumModel sl : arraylist) {
				if(sl.getAlbumName().toLowerCase(Locale.getDefault()).contains(charText)) {
					sl.setAlbumId(pos);
					list.add(sl);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<AlbumModel> list) {
		songList = list;
		this.notifyDataSetChanged();
  }
	
	
	
	
	
	class AdapterTask extends AsyncTask<Void,Integer,Bitmap>
	{
		
		private ImageView iv;
		private String path;
		private Context c;

		public AdapterTask(ImageView iv, String path, Context c) {
			this.iv = iv;
			this.path = path;
			this.c = c;
		}

		@Override
		protected void onPreExecute() {
			// TODO: Implement this method
			super.onPreExecute();
		}


		@Override
		protected Bitmap doInBackground(Void[] p1) {
			Bitmap bitmap = null;
			Bitmap bmp2 = null;
			try {
				bitmap= BitmapFactory.decodeFile(path);
				bmp2 = BitmapFactory.decodeResource(c.getResources(), R.drawable.music);
			} catch(Exception e) {}

			if(bitmap != null) {
				return bitmap;
			}
			return bmp2;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if(result!=null)
				iv.setImageBitmap(result);
		}



	}

}


