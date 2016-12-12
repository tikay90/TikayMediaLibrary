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
import com.tikay.medialibrary.utils.Utilities;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Tenkorang Alex on 10/11/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder>
{
	private Context mContext;
	private ArrayList<AlbumModel> albumList;
	private ArrayList<AlbumModel> list = null;
  private ArrayList<AlbumModel> arraylist;
	private String TAG = AlbumsAdapter.class.getSimpleName();

	public class MyViewHolder extends RecyclerView.ViewHolder
	{
		public TextView title, count;
		public ImageView thumbnail, overflow;
		public CircleImageView civ;
		public RelativeLayout rl;
		public CardView cv;

		public MyViewHolder(View view) {
			super(view);
			rl = (RelativeLayout) view.findViewById(R.id.rl);
			title = (TextView) view.findViewById(R.id.title);
			count = (TextView) view.findViewById(R.id.count);
			thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
			//civ = (CircleImageView) view.findViewById(R.id.ivProfile);
			overflow = (ImageView) view.findViewById(R.id.overflow);
		}
	}


	public AlbumsAdapter(Context mContext, ArrayList<AlbumModel> albumList) {
		this.mContext = mContext;
		this.albumList = albumList;

		this.list = albumList;
		this.arraylist = new ArrayList<AlbumModel>();
		this.arraylist.addAll(albumList);
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.album_card_details, parent, false);

		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		AlbumModel album = albumList.get(position);
		holder.title.setText(album.getAlbumName());
		String s = album.getArtist_songs();// + a>1?"Tracks":"Track";
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
		holder.count.setText(s);
		//holder.civ.setVisibility(View.GONE);
		//holder.thumbnail.setVisibility(View.VISIBLE);
		// loading album cover using Glide library
		//Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
		Glide
			.with(mContext)
			.load(album.getArtPath())
			.asBitmap()
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			//.transform(new CircleTransform(mContext))
			//.placeholder(R.drawable.placeholder)
			//.error(R.drawable.music_m_logo)
			.into(new SimpleTarget<Bitmap>() {  
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
					holder.thumbnail.setImageBitmap(bitmap);
					try {
						if(bitmap != null) { 
							Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
									@Override
									public void onGenerated(Palette palette) {
										Palette.Swatch textSwatch = palette.getVibrantSwatch();
										int primaryDark = mContext.getResources().getColor(R.color.colorPrimaryDark);
										int primary = mContext.getResources().getColor(R.color.colorPrimary);
										int accent = mContext.getResources().getColor(R.color.colorAccent);
										int defaultColor = android.R.color.white;
										int defaultTextColor = Color.BLACK;
										int vibrant = palette.getVibrantColor(defaultColor);
										int vibrantLight = palette.getLightVibrantColor(defaultColor);
										int vibrantDark = palette.getDarkVibrantColor(defaultColor);
										int muted = palette.getMutedColor(defaultColor);
										int mutedLight = palette.getLightMutedColor(defaultColor);
										int mutedDark = palette.getDarkMutedColor(defaultColor);

										int[] bgColors = {vibrant,vibrantLight};
										int[] txtColors = {mutedDark,muted,mutedLight};

										holder.rl.setBackgroundColor(bgColors[new Random().nextInt(bgColors.length)]);
										holder.title.setTextColor(defaultTextColor);
										holder.count.setTextColor(defaultTextColor);
										//holder.title.setTextColor(txtColors[new Random().nextInt(txtColors.length)]);
										//holder.count.setTextColor(txtColors[new Random().nextInt(txtColors.length)]);
										//collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
										//collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
										//updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);

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
				@Override
				public void onLoadFailed(Exception e, Drawable d){
					holder.thumbnail.setImageResource(R.drawable.album_default);
					//holder.rl.setBackgroundResource(R.drawable.album_default);
				}
			});

		holder.overflow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						showPopupMenu(holder.overflow); 
					} catch(Exception e) {
						Log.e(TAG, e.getMessage());
						Utilities.toastShort(mContext,e.getMessage()+"\n"+e.toString());
					}
				}
			});

	}

	/**
	 * Showing popup menu when tapping on 3 dots
	 */
	private void showPopupMenu(View view) {
		// inflate menu
		PopupMenu popup = new PopupMenu(mContext, view);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.menu_album, popup.getMenu());
		popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
		popup.show();
	}

	/**
	 * Click listener for popup menu items
	 */
	class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener
	{

		public MyMenuItemClickListener() {
		}

		@Override
		public boolean onMenuItemClick(MenuItem menuItem) {
			switch(menuItem.getItemId()) {
				case R.id.action_favourite:
					Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
					return true;
				case R.id.action_next:
					Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
					return true;
				default:
			}
			return false;
		}
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
