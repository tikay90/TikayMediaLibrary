/*******************************
 Created by Tenkorang Alex
 1/9/2016
 *******************************/

package com.tikay.medialibrary.models;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Parcel;

public class AlbumModel implements Parcelable
{
	private String albumName; 
	private String albumArtist;
	private String artist_songs;
	private String artPath;
  private long albumId;
	private int nub_of_songs;
	private int type;
	@Override
	public int describeContents() {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		parcel.writeString(albumName);
		parcel.writeString(albumArtist);
		parcel.writeString(artist_songs);
		parcel.writeString(artPath);
		parcel.writeLong(albumId);
		parcel.writeInt(nub_of_songs);
		parcel.writeInt(type);
	}
	
	public AlbumModel(Parcel parcel){
		albumName = parcel.readString();
		albumArtist = parcel.readString();
		artist_songs = parcel.readString();
		artPath = parcel.readString();
		albumId = parcel.readLong();
		nub_of_songs = parcel.readInt();
		type = parcel.readInt();
	}
	
	public static final Parcelable.Creator<AlbumModel> CREATOR = new Parcelable.Creator<AlbumModel>() {
		public AlbumModel createFromParcel(Parcel in) {
			//Log.i("PATIENT", "PATIENT ==> createFromParcel()");
			return new AlbumModel(in);
		}

		public AlbumModel[] newArray(int size) {
			return new AlbumModel[size];

		}
	};

	public AlbumModel(String albumName, String albumArtist, String artist_songs, String artPath, long albumId, int nub_of_songs,int type) {
		this.albumName = albumName;
		this.albumArtist = albumArtist;
		this.artist_songs = artist_songs;
		this.artPath = artPath;
		this.albumId = albumId;
		this.nub_of_songs = nub_of_songs;
		this.type = type;
	}

	public AlbumModel() {
		// empty constructor 
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public void setArtist_songs(String artist_songs) {
		this.artist_songs = artist_songs;
	}

	public String getArtist_songs() {
		return artist_songs;
	}

	public void setArtPath(String artPath) {
		this.artPath = artPath;
	}

	public String getArtPath() {
		return artPath;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setNub_of_songs(int nub_of_songs) {
		this.nub_of_songs = nub_of_songs;
	}

	public int getNub_of_songs() {
		return nub_of_songs;
	}

}
