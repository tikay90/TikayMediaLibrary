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
	private String TAG = Genre.class.getSimpleName();
	private String KEY_LIST = "genre_key_list";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utilities.writeLogcatToFile(TAG);
		Log.e(TAG, "onCreate CALLED");
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView CALLED");
		v = inflater.inflate(R.layout.genre, container, false);

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
		if(savedInstanceState == null)
			new GenresTask().execute();
		else
			restoreState(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(KEY_LIST,genres);
		super.onSaveInstanceState(outState);
	}
	

	private void restoreState(Bundle savedInstanceState) {
		genres = savedInstanceState.getParcelableArrayList(KEY_LIST);
		initRecycler(genres);
	}

	private void initRecycler(ArrayList<GenreModel> genres) {
		adapter = new GenreAdapter(getActivity(), genres);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(adapter);
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
				initRecycler(genres);
			} catch(Exception e) {
				Log.e("GenreTask", "GenreTask ERROR ----  " + e.getMessage());
			}
		}

	}

}
