package com.tikay.medialibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaylistModel implements Parcelable
{
	private String name, path,tracks,dateAdded,dateModified;
	private String artist, album;
	private long ID;
	private int type;
	
	@Override
	public int describeContents() {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// 3-11-16
		parcel.writeString(name);
		parcel.writeString(path);
		parcel.writeString(tracks);
		parcel.writeString(dateAdded);
		parcel.writeString(dateModified);
		parcel.writeString(artist);
		parcel.writeString(album);
		parcel.writeLong(ID);		
		parcel.writeInt(type);
	}
	
	public PlaylistModel(Parcel parcel){
		// 3-11-16
		name = parcel.readString();
		path = parcel.readString();
		tracks = parcel.readString();
		dateAdded = parcel.readString();
		dateModified = parcel.readString();
		artist = parcel.readString();
		album = parcel.readString();
		ID = parcel.readLong();
		type = parcel.readInt();
	}
	
	public static final Parcelable.Creator<PlaylistModel> CREATOR = new Parcelable.Creator<PlaylistModel>() {
		// 3-11-16
		public PlaylistModel createFromParcel(Parcel in) {
			return new PlaylistModel(in);
		}

		public PlaylistModel[] newArray(int size) {
			return new PlaylistModel[size];

		}
	};

	public PlaylistModel(String name, String path, String tracks, String dateAdded, String dateModified, String artist, String album, long iD,int type) {
		this.name = name;
		this.path = path;
		this.tracks = tracks;
		this.dateAdded = dateAdded;
		this.dateModified = dateModified;
		this.artist = artist;
		this.album = album;
		this.ID = iD;
		this.type = type;
	}

	public PlaylistModel(){
		// empty constructor
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setTracks(String tracks) {
		this.tracks = tracks;
	}

	public String getTracks() {
		return tracks;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}

	public String getDateModified() {
		return dateModified;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getArtist() {
		return artist;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getAlbum() {
		return album;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getID() {
		return ID;
	}

	
	

	
}
