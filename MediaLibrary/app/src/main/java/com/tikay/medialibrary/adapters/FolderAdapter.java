package com.tikay.medialibrary.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale; 

public class FolderAdapter extends BaseAdapter 
{
  private ArrayList<FolderModel> songList = null;
  private ArrayList<FolderModel> list = null;
  private ArrayList<FolderModel> arraylist;
  private Context context;
  private int pos = 0;

	private String TAG ="FolderAdapter";

  static class ViewHolder
  {
		public ImageView ivThumbnail;
		public TextView folderName, artistName,albumName,songDuration,folderPath;
  }


  public FolderAdapter(Context cntxt, ArrayList<FolderModel> songList) {
		context = cntxt;
		this.list = songList;
		this.songList = songList;

		this.arraylist = new ArrayList<FolderModel>();
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
			view = LayoutInflater.from(context).inflate(R.layout.folders_details, parent, false);

			//set up view holder
			viewHolder = new ViewHolder();
			viewHolder.folderName = (TextView)view.findViewById(R.id.tvFolder_Song_title);
			viewHolder.folderPath = (TextView)view.findViewById(R.id.tvFolderPath);
			viewHolder.ivThumbnail = (ImageView)view.findViewById(R.id.iv_folder_img);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}

		try {

			final FolderModel item = songList.get(position);
			viewHolder.folderName.setText(item.getFolderName());
			viewHolder.folderPath.setText(item.getFolderPath() == null ? "No path" : item.getFolderPath());
			viewHolder.ivThumbnail.setImageBitmap(item.getFolderImg());
			//new FolderAdapterTask(context,item.getFolderPath(),viewHolder.ivThumbnail).execute();
		} catch(Exception e) {
			Log.e(TAG, TAG + "  " + e.toString());
			Utilities.toastLong(context,e.toString());
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
			for(FolderModel item : arraylist) {
				if(item.getFolderName().toLowerCase(Locale.getDefault()).contains(charText)) {
					item.setFolderPosition(pos);
					list.add(item);
				}
			}
		}
		this.notifyDataSetChanged();
  }

  public void setSongsList(ArrayList<FolderModel> list) {
		songList = list;
		this.notifyDataSetChanged();
  }

	@Override
	public String toString() {
		String data = "";
		for(int i = 0; i < getCount(); i++) {
			data += getItem(i).toString() ;
			if(i + 1 < getCount()) {
				data += ", \t \n";
			}
		}
		return  data;
	}


	private class FolderAdapterTask extends AsyncTask <Void,Void,Bitmap>
	{
		Context context;
		String path;
		ImageView iv;

		public FolderAdapterTask(Context context, String path, ImageView iv) {
			this.context = context;
			this.path = path;
			this.iv = iv;
		}

		@Override
		protected Bitmap doInBackground(Void[] p1) {
			// TODO: Implement this method
			return Utilities.getEmbeddedSongArt(context, path);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);

			iv.setImageBitmap(result);
		}



	}





}


