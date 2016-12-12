package com.tikay.medialibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistModel implements Parcelable
{
	private String artistName, artistAlbums, artistTracks,artistImg;
	private long artistID;
	private int type;

	@Override
	public int describeContents() {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(artistName);
		parcel.writeString(artistAlbums);
		parcel.writeString(artistTracks);
		parcel.writeString(artistImg);
		parcel.writeLong(artistID);
		parcel.writeLong(type);
	}


	public ArtistModel(Parcel parcel) {
		artistName = parcel.readString();
		artistAlbums = parcel.readString();
		artistTracks = parcel.readString();
		artistImg = parcel.readString();
		artistID = parcel.readLong();
		type = parcel.readInt();
	}
	
	public static final Parcelable.Creator<ArtistModel> CREATOR = new Parcelable.Creator<ArtistModel>() {
		public ArtistModel createFromParcel(Parcel in) {
			//Log.i("PATIENT", "PATIENT ==> createFromParcel()");
			return new ArtistModel(in);
		}

		public ArtistModel[] newArray(int size) {
			return new ArtistModel[size];

		}
	};
	
	
	public ArtistModel(String artistName, String artistAlbums, String artistTracks, long artistID, String artistImg,int type) {
		this.artistName = artistName;
		this.artistAlbums = artistAlbums;
		this.artistTracks = artistTracks;
		this.artistID = artistID;
		this.artistImg = artistImg;
		this.type = type;
	}
	
	public ArtistModel(){
		
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistAlbums(String artistAlbums) {
		this.artistAlbums = artistAlbums;
	}

	public String getArtistAlbums() {
		return artistAlbums;
	}

	public void setArtistTracks(String artistTracks) {
		this.artistTracks = artistTracks;
	}

	public String getArtistTracks() {
		return artistTracks;
	}

	public void setArtistID(long artistID) {
		this.artistID = artistID;
	}

	public long getArtistID() {
		return artistID;
	}

	public void setArtistImg(String artistImg) {
		this.artistImg = artistImg;
	}

	public String getArtistImg() {
		return artistImg;
	}

	
	
}
