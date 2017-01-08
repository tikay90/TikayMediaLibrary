package com.tikay.medialibrary.recycler_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.tikay.medialibrary.activities.ArtistTracks;
import com.tikay.medialibrary.models.ArtistModel;
import com.tikay.medialibrary.utils.AnimUtils;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class GridArtistAdapter extends RecyclerView.Adapter<GridArtistAdapter.MyViewHolder>
{
	private ArrayList<ArtistModel> artistList = null;
  private ArrayList<ArtistModel> list = null;
  private ArrayList<ArtistModel> arraylist;
  private Context context;
	private String TAG = "GridArtistAdapter";
	private int previousPosition = 0;


	public class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener
	{
		private ImageView ivArtistThumbnail,overflow;
		private TextView artistName,artistTracks,artistAlbums;
		private RelativeLayout rla;
		public MyViewHolder(View view) {
			super(view);
			rla = (RelativeLayout) view.findViewById(R.id.rla);
			artistName = (TextView)view.findViewById(R.id.tv_artist_name);
			artistAlbums = (TextView)view.findViewById(R.id.tv_artist_albums);
			artistTracks = (TextView)view.findViewById(R.id.tv_artist_tracks);
			ivArtistThumbnail = (ImageView)view.findViewById(R.id.iv_artist);
			ivArtistThumbnail.setOnClickListener(this);
			overflow = (ImageView) view.findViewById(R.id.overflow);
			overflow.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			switch(view.getId()) {
				case R.id.iv_artist:
					try {
						long ID = artistList.get(getAdapterPosition()).getArtistID();
						String name = artistList.get(getAdapterPosition()).getArtistName();
						//Utilities.toastShort(getContext(), name + "\n" + ID);
						Intent intent = new Intent(context, ArtistTracks.class);
						intent.putExtra("id", ID).putExtra("name", name);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						//broadcastIntent.putExtra(Constants.SEND, "playlist").putExtra("id", ID);
						//getActivity().sendBroadcast(broadcastIntent);

						context.startActivity(intent);
					} catch(Exception e) {
						Utilities.toastLong(context, e.toString());
						Log.e(TAG, e.toString());
					}
					break;
				case R.id.overflow:
					try {
						showPopupMenu(overflow);
					} catch(Exception e) {
						Utilities.toastLong(context, e.toString());
						Log.e(TAG, e.toString());
					}
					break;
			}
		}


	}


	public GridArtistAdapter(Context cntxt, ArrayList<ArtistModel> artistList) {
		context = cntxt;
		this.list = artistList;
		this.artistList = artistList;

		this.arraylist = new ArrayList<ArtistModel>();
		this.arraylist.addAll(artistList);
  }

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.grid_artist_details, parent, false);

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
		/*Glide
		 .with(context)
		 .load(R.drawable.apaawa)
		 .diskCacheStrategy(DiskCacheStrategy.ALL)
		 .into(holder.ivArtistThumbnail);*/

		Glide
			.with(context)
			.load(R.drawable.apaawa)
			.asBitmap()
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			//.transform(new CircleTransform(mContext))
			//.placeholder(R.drawable.placeholder)
			//.error(R.drawable.music_m_logo)
			.into(new SimpleTarget<Bitmap>() {  
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
					holder.ivArtistThumbnail.setImageBitmap(bitmap);
					try {
						if(bitmap != null) { 
							Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
									@Override
									public void onGenerated(Palette palette) {
										Palette.Swatch textSwatch = palette.getVibrantSwatch();
										int primaryDark = context.getResources().getColor(R.color.colorPrimaryDark);
										int primary = context.getResources().getColor(R.color.colorPrimary);
										int accent = context.getResources().getColor(R.color.colorAccent);
										int defaultColor = android.R.color.white;
										int defaultTextColor = Color.BLACK;
										int vibrant = palette.getVibrantColor(defaultColor);
										int vibrantLight = palette.getLightVibrantColor(defaultColor);
										int vibrantDark = palette.getDarkVibrantColor(defaultColor);
										int muted = palette.getMutedColor(defaultColor);
										int mutedLight = palette.getLightMutedColor(defaultColor);
										int mutedDark = palette.getDarkMutedColor(defaultColor);

										int[] bgColors = {vibrant,vibrantLight,vibrantDark,muted,mutedLight};
										int[] txtColors = {mutedDark,muted,mutedLight};

										holder.rla.setBackgroundColor(bgColors[new Random().nextInt(bgColors.length)]);
										//holder.artistName.setTextColor(defaultTextColor);
										//holder.artistAlbums.setTextColor(defaultTextColor);
										//holder.title.setTextColor(txtColors[new Random().nextInt(txtColors.length)]);
										//holder.count.setTextColor(txtColors[new Random().nextInt(txtColors.length)]);
										//collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
										//collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
										//updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);

										/*if(textSwatch != null) {
										 //Toast.makeText(mContext, "Null swatch :(", Toast.LENGTH_SHORT).show();
										 //holder.rl.setBackgroundColor(textSwatch.getRgb());
										 //holder.title.setTextColor(textSwatch.getTitleTextColor());
										 //holder.count.setTextColor(textSwatch.getBodyTextColor());
										 //holder.rl.setBackgroundColor(vibrant);
										 //holder.title.setTextColor(vibrantLight);
										 //holder.count.setTextColor(vibrantDark);
										 }*/
									}
								});
						}
					} catch(Exception e) {
						Log.e("ADAPTER", "ADAPTER >>> error @  " + e.getMessage());
					}
				}
				@Override
				public void onLoadFailed(Exception e, Drawable d) {
					holder.ivArtistThumbnail.setImageResource(R.drawable.album_default);
					//holder.rl.setBackgroundResource(R.drawable.album_default);
				}
			});

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

	/**
	 * Showing popup menu when tapping on 3 dots
	 */
	private void showPopupMenu(View view) {
		// inflate menu
		try {
			Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
			PopupMenu popup = new PopupMenu(wrapper, view);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.menu_album, popup.getMenu());
			popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
			popup.show();
		} catch(Exception e) {
			Utilities.toastLong(context, e.toString());
			Log.e(TAG, e.toString());
		}
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
					Toast.makeText(context, "Add to favourite", Toast.LENGTH_SHORT).show();
					return true;
				case R.id.action_next:
					Toast.makeText(context, "Play next", Toast.LENGTH_SHORT).show();
					return true;
				default:
			}
			return false;
		}
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
