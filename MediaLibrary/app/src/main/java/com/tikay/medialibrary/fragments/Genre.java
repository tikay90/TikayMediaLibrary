package com.tikay.medialibrary.fragments;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.GenreModel;
import com.tikay.medialibrary.recycler_adapter.GenreAdapter;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.util.ArrayList;

public class Genre extends Fragment
{
	private RecyclerView recyclerView;
	private GenreAdapter adapter;
	private View v;
	private ArrayList<GenreModel> genres = new ArrayList<GenreModel>();

	private String TAG = "Genre";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		writeLogcatToFile();
		Log.e(TAG, "onCreate CALLED");
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView CALLED");
		v = inflater.inflate(R.layout.genre,container,false);
		
		initWidgets();
		
		return v;
	}

	private void initWidgets() {
		recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e(TAG, "onActivityCreated CALLED");
		
		new GenresTask().execute();
	}
	
	private void writeLogcatToFile() {
		Log.e(TAG, ">>>>>>>  printLogcat() <<<<<<<");
		String root_sd = Environment.getExternalStorageDirectory().toString();
		File mylogs = new File(root_sd, ".TikayLogs");
		String name = "Genres.log";
		
		if(!mylogs.exists()) {
			if(Utilities.isSdCardPresent()) {
				mylogs.mkdir();
			}
		}
		final long maxFileSize = 1024 * 100;  // 100KB
		File logFile = new File(mylogs, name);
		if(logFile.length() >= maxFileSize) {
			logFile.delete();
			Log.e(TAG, logFile.getName() + " deleted");
			//Utilities.toastShort(this, logFile.getName()+" deleted");
			logFile = new File(mylogs, name);
		}
		try {
			Process process = Runtime.getRuntime().exec("logcat -c");
			process = Runtime.getRuntime().exec("logcat -v time -f "  + /*mylogs.getAbsolutePath()+"/tikay_log.log"*/logFile.getAbsolutePath());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private class GenresTask extends AsyncTask<Void,Integer,ArrayList<GenreModel>>
	{
		@Override
		protected ArrayList<GenreModel> doInBackground(Void... p1) {
			return MyMediaQuery.getGenry(getContext());
		}

		@Override
		protected void onPostExecute(ArrayList<GenreModel> result) {
			super.onPostExecute(result);

			try {
				genres = result;
				adapter = new GenreAdapter(getActivity(), genres);
				//SharedPreferences preferences = getActivity().getSharedPreferences(Constants.Genre, getActivity().MODE_PRIVATE);
				//int index = preferences.getInt("albumPosition", 0);
				//int top = preferences.getInt("up", 0);
				recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
				recyclerView.setItemAnimator(new DefaultItemAnimator());
				recyclerView.setAdapter(adapter);

			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}

	}
	
}
