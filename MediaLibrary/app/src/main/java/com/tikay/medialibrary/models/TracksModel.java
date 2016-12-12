package com.tikay.medialibrary.models;
/*******************************
 Created by Tenkorang Alex
 31/8/2015
 *******************************/


import android.net.*;
import android.os.Parcelable;
import android.os.Parcel;

public class TracksModel implements Parcelable
{
	private String songName; 
	private String songAlbumName;
	private String songFullPath;
	private String songDuration;
	private String songArtist;
	private String songComposer;  
  private String songAlbumArtPath;
  private int songId;
  private long songSize;
	private int realPosition;
	private int type;
	
	@Override
	public int describeContents() {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		parcel.writeString(songName);
		parcel.writeString(songAlbumName);
		parcel.writeString(songFullPath);
		parcel.writeString(songDuration);
		parcel.writeString(songArtist);
		parcel.writeString(songComposer);
		parcel.writeString(songAlbumArtPath);
		parcel.writeInt(songId);
		parcel.writeLong(songSize);
		parcel.writeInt(realPosition);
		parcel.writeInt(type);
	}
	
	public TracksModel(Parcel parcel){
		songName = parcel.readString();
		songAlbumName = parcel.readString();
		songFullPath = parcel.readString();
		songDuration = parcel.readString();
		songArtist = parcel.readString();
		songComposer = parcel.readString();
		songAlbumArtPath = parcel.readString();
		songId = parcel.readInt();
		songSize = parcel.readLong();
		realPosition = parcel.readInt();
		type = parcel.readInt();
	}

  public static final Parcelable.Creator<TracksModel> CREATOR = new Parcelable.Creator<TracksModel>() {
		public TracksModel createFromParcel(Parcel in) {
			//Log.i("PATIENT", "PATIENT ==> createFromParcel()");
			return new TracksModel(in);
		}

		public TracksModel[] newArray(int size) {
			return new TracksModel[size];

		}
	};

	public TracksModel(String songName, String songAlbumName, String songFullPath, String songDuration, String songArtist, String songComposer, String songAlbumArtPath, int songId, long songSize, int realPosition,int type) {
		this.songName = songName;
		this.songAlbumName = songAlbumName;
		this.songFullPath = songFullPath;
		this.songDuration = songDuration;
		this.songArtist = songArtist;
		this.songComposer = songComposer;
		this.songAlbumArtPath = songAlbumArtPath;
		this.songId = songId;
		this.songSize = songSize;
		this.realPosition = realPosition;
		this.type = type;
	}

	


  public TracksModel() {
		// empty constructor
  }

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongAlbumName(String songAlbumName) {
		this.songAlbumName = songAlbumName;
	}

	public String getSongAlbumName() {
		return songAlbumName;
	}

	public void setSongFullPath(String songFullPath) {
		this.songFullPath = songFullPath;
	}

	public String getSongFullPath() {
		return songFullPath;
	}

	public void setSongDuration(String songDuration) {
		this.songDuration = songDuration;
	}

	public String getSongDuration() {
		return songDuration;
	}

	public void setSongArtist(String songArtist) {
		this.songArtist = songArtist;
	}

	public String getSongArtist() {
		return songArtist;
	}

	public void setSongComposer(String songComposer) {
		this.songComposer = songComposer;
	}

	public String getSongComposer() {
		return songComposer;
	}

	public void setSongAlbumArtPath(String songAlbumArtPath) {
		this.songAlbumArtPath = songAlbumArtPath;
	}

	public String getSongAlbumArtPath() {
		return songAlbumArtPath;
	}

	public void setSongId(int songId) {
		this.songId = songId;
	}

	public int getSongId() {
		return songId;
	}

	public void setSongSize(long songSize) {
		this.songSize = songSize;
	}

	public long getSongSize() {
		return songSize;
	}

	public void setRealPosition(int realPosition) {
		this.realPosition = realPosition;
	}

	public int getRealPosition() {
		return realPosition;
	}




}
