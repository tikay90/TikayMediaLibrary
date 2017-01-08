package com.tikay.medialibrary.models;
import android.os.Parcel;
import android.os.Parcelable;

public class GenreModel implements Parcelable
{
	private String genreName, genreTracks,genreImg;
	private long genreID;
	private int type;

	@Override
	public int describeContents() {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(genreName);
		parcel.writeString(genreTracks);
		parcel.writeString(genreImg);
		parcel.writeLong(genreID);
		parcel.writeInt(type);
	}


	public GenreModel(Parcel parcel) {
		genreName = parcel.readString();
		genreTracks = parcel.readString();
		genreImg = parcel.readString();
		genreID = parcel.readLong();
		type = parcel.readInt();
	}

	public static final Parcelable.Creator<GenreModel> CREATOR = new Parcelable.Creator<GenreModel>() {
		public GenreModel createFromParcel(Parcel in) {
			//Log.i("PATIENT", "PATIENT ==> createFromParcel()");
			return new GenreModel(in);
		}

		public GenreModel[] newArray(int size) {
			return new GenreModel[size];
		}
	};

	public GenreModel() {

	}

	public GenreModel(String genreName, String genreTracks, String genreImg, long genreID, int type) {
		this.genreName = genreName;
		this.genreTracks = genreTracks;
		this.genreImg = genreImg;
		this.genreID = genreID;
		this.type = type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}

	public String getGenreName() {
		return genreName;
	}

	public void setGenreTracks(String genreTracks) {
		this.genreTracks = genreTracks;
	}

	public String getGenreTracks() {
		return genreTracks;
	}

	public void setGenreImg(String genreImg) {
		this.genreImg = genreImg;
	}

	public String getGenreImg() {
		return genreImg;
	}

	public void setGenreID(long genreID) {
		this.genreID = genreID;
	}

	public long getGenreID() {
		return genreID;
	}

	/*
	 equals and hashCode needs to be overriden in order to 
	 filter objects of this class with Set to remove duplicates
	 */
	@Override
	public boolean equals(Object obj) {

    if(obj instanceof GenreModel) {
			GenreModel temp = (GenreModel) obj;
			if(this.genreName.equalsIgnoreCase(temp.genreName) && this.genreID == temp.genreID)//&& this.genreTracks == temp.genreTracks )
				return true;
    }
		
    return false;

	}


	@Override
	public int hashCode() {
		int hashCode = (int)genreID * 20;
		hashCode += genreName.hashCode();
    //return (this.genreName.hashCode() + this.genreID + this.url.hashCode() + this.description.hashCode());

		return hashCode;
	}
}
