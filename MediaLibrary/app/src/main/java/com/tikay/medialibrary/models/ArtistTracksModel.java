package com.tikay.medialibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistTracksModel implements Parcelable
{
	private String name, path, duration, artist, album, albumArt;
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
		parcel.writeString(name);
		parcel.writeString(path);
		parcel.writeString(duration);
		parcel.writeString(artist);
		parcel.writeString(album);
		parcel.writeString(albumArt);
		parcel.writeLong(ID);
		parcel.writeInt(position);
		parcel.writeInt(type);
	}


	public ArtistTracksModel(Parcel parcel) {
		name = parcel.readString();
		path = parcel.readString();
		duration = parcel.readString();
		artist = parcel.readString();
		album = parcel.readString();
		albumArt = parcel.readString();
		ID = parcel.readLong();
		position = parcel.readInt();
		type = parcel.readInt();
	}

	public static final Parcelable.Creator<ArtistTracksModel> CREATOR = new Parcelable.Creator<ArtistTracksModel>() {
		public ArtistTracksModel createFromParcel(Parcel in) {
			//Log.i("PATIENT", "PATIENT ==> createFromParcel()");
			return new ArtistTracksModel(in);
		}

		public ArtistTracksModel[] newArray(int size) {
			return new ArtistTracksModel[size];

		}
	};

	public ArtistTracksModel(String name, String path, String duration, String artist, String album, String albumArt, long iD, int position,int type) {
		this.name = name;
		this.path = path;
		this.duration = duration;
		this.artist = artist;
		this.album = album;
		this.albumArt = albumArt;
		this.ID = iD;
		this.position = position;
		this.type = type;
	}
	
	public ArtistTracksModel(){
		
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
