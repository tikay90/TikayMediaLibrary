package com.tikay.medialibrary.recycler_adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.AlbumModel;
import com.tikay.medialibrary.recycler_adapter.AlbumsAdapter;
import com.tikay.medialibrary.utils.Utilities;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>
{
	private Context mContext;
	private ArrayList<AlbumModel> albumList;
	private ArrayList<AlbumModel> list = null;
  private ArrayList<AlbumModel> arraylist;
	private String TAG = AlbumsAdapter.class.getSimpleName();

	public class MyViewHolder extends RecyclerView.ViewHolder
	{
		public ImageView ivThumbnail;
		public TextView albumArtistName,albumName,albumNumber;

		public MyViewHolder(View view) {
			super(view);
			albumName = (TextView)view.findViewById(R.id.tv_Album_Name);
			albumArtistName = (TextView)view.findViewById(R.id.tv_Album_Artist_Name);
			albumNumber = (TextView)view.findViewById(R.id.tv_albums_number);
			ivThumbnail = (ImageView)view.findViewById(R.id.iv_Album);
		}
	}


	public AlbumAdapter(Context mContext, ArrayList<AlbumModel> albumList) {
		this.mContext = mContext;
		this.albumList = albumList;

		this.list = albumList;
		this.arraylist = new ArrayList<AlbumModel>();
		this.arraylist.addAll(albumList);
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.album_details, parent, false);

		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		AlbumModel item = albumList.get(position);
		holder.albumName.setText(item.getAlbumName());
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
		holder.albumArtistName.setText(item.getAlbumArtist());
		holder.albumNumber.setText(s);
		//Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
		Glide
			.with(mContext)
			.load(item.getArtPath())
			.asBitmap()
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			//.transform(new CircleTransform(mContext))
			//.placeholder(R.drawable.placeholder)
			//.error(R.drawable.music_m_logo)
			.into(new SimpleTarget<Bitmap>() {  
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
					holder.ivThumbnail.setImageBitmap(bitmap);
				}
				@Override
				public void onLoadFailed(Exception e, Drawable d){
					holder.ivThumbnail.setImageResource(R.drawable.album_default);
					//holder.rl.setBackgroundResource(R.drawable.album_default);
				}
			});
	}
	
	@Override
	public int getItemCount() {
		return albumList.size();
	}



  // Filter method
  public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		albumList.clear();
		if(charText.length() == 0) {
			albumList.addAll(arraylist);
		} else {
			for(AlbumModel sl : arraylist) {
				if(sl.getAlbumName().toLowerCase(Locale.getDefault()).contains(charText)) {
					list.add(sl);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<AlbumModel> list) {
		albumList = list;
		this.notifyDataSetChanged();
  }
}
