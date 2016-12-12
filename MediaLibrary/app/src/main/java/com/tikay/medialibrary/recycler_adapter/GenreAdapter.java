package com.tikay.medialibrary.recycler_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.GenreModel;
import java.util.ArrayList;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import com.tikay.medialibrary.utils.Utilities;
import android.content.Intent;
import com.tikay.medialibrary.activities.GenreTracks;
import android.util.Log;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.MyViewHolder>
{
	private LayoutInflater inflater;
	ArrayList<GenreModel>  genreList;
	Context context;

	public GenreAdapter(Context context, ArrayList<GenreModel> genreList) {
		inflater = LayoutInflater.from(context);
		this.genreList = genreList;
		this.context = context;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.genre_details, parent, false);
		MyViewHolder viewHolder = new MyViewHolder(v);

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(MyViewHolder viewHolder, int position) {
		GenreModel item = genreList.get(position);
		viewHolder.tvName.setText(item.getGenreName());
		//viewHolder.iv.setImageResource(7);
		getName(position);
	}

	@Override
	public int getItemCount() {
		// TODO: Implement this method
		return genreList.size();
	}

	private String getName(int position) {
		GenreModel item = genreList.get(position);
		return	item.getGenreName();
	}

	private long getId(int position) {
		GenreModel item = genreList.get(position);
		return	item.getGenreID();
	}


	class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener
	{
		TextView tvName;
		ImageView iv;
		LinearLayout ll;

		public MyViewHolder(View view) {
			super(view);

			tvName = (TextView) view.findViewById(R.id.tv_genreName);
			iv = (ImageView) view.findViewById(R.id.iv_genre);
			ll = (LinearLayout) view.findViewById(R.id.ll_genre);
			ll.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			try {
				//Utilities.toastShort(context, getName(getPosition())+"\n"+getId(getPosition()));
				Intent intent = new Intent(context, GenreTracks.class);
				intent.putExtra("name", getName(getPosition())).putExtra("id", getId(getPosition()));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} catch(Exception e) {
				Log.e("GenreAdapter", "GenreAdapter - onClick() >>> error @  " + e.getMessage());
				Utilities.toastLong(context, e.getMessage());
			}
		}

	}


}
