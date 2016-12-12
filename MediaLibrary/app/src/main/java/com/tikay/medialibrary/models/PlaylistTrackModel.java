package com.tikay.medialibrary.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PlaylistTrackModel implements Parcelable
{
	private String name, path,dateAdded,dateModified, duration, artist, album, albumArt;
	private long ID;
	private int position;
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
		parcel.writeString(dateAdded);
		parcel.writeString(dateModified);
		parcel.writeString(duration);
		parcel.writeString(artist);
		parcel.writeString(album);
		parcel.writeString(albumArt);
		parcel.writeLong(ID);		
		parcel.writeInt(position);
		parcel.writeInt(type);
	}

	public PlaylistTrackModel(Parcel parcel){
		// 3-11-16
		name = parcel.readString();
		path = parcel.readString();
		dateAdded = parcel.readString();
		dateModified = parcel.readString();
		duration = parcel.readString();
		artist = parcel.readString();
		album = parcel.readString();
		albumArt = parcel.readString();
		ID = parcel.readLong();
		position = parcel.readInt();
		type = parcel.readInt();
	}

	public static final Parcelable.Creator<PlaylistTrackModel> CREATOR = new Parcelable.Creator<PlaylistTrackModel>() {
		// 3-11-16
		public PlaylistTrackModel createFromParcel(Parcel in) {
			return new PlaylistTrackModel(in);
		}

		public PlaylistTrackModel[] newArray(int size) {
			return new PlaylistTrackModel[size];

		}
	};
	

	public PlaylistTrackModel(String name, String path, String dateAdded, String dateModified, String duration, String artist, String album, String albumArt, long iD, int position,int type) {
		this.name = name;
		this.path = path;
		this.dateAdded = dateAdded;
		this.dateModified = dateModified;
		this.duration = duration;
		this.artist = artist;
		this.album = album;
		this.albumArt = albumArt;
		ID = iD;
		this.position = position;
		this.type = type;
	}

	public PlaylistTrackModel() {
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

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDuration() {
		return duration;
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

	public void setAlbumArt(String albumArt) {
		this.albumArt = albumArt;
	}

	public String getAlbumArt() {
		return albumArt;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getID() {
		return ID;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

		

}
