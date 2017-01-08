package com.tikay.medialibrary.models;

import android.graphics.Bitmap;
import android.os.Parcelable;
import android.os.Parcel;
import java.util.Comparator;

public class FolderModel implements Parcelable,Comparable<FolderModel>
{

	private String folderName,folderPath,albumArt;
	private String folderShortPath;
	private int folderPosition;
	private int type;

	
	@Override
	public int compareTo(FolderModel model) {
		return this.getFolderName().toLowerCase().compareTo(model.getFolderName().toLowerCase());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		parcel.writeString(folderName);
		parcel.writeString(folderPath);
		parcel.writeString(albumArt);
		parcel.writeInt(folderPosition);
		parcel.writeInt(type);
		parcel.writeString(folderShortPath);
	}

	public FolderModel(Parcel parcel) {
		folderName = parcel.readString();
		folderPath = parcel.readString();
		albumArt = parcel.readString();
		folderPosition = parcel.readInt();
		type = parcel.readInt();
		folderShortPath = parcel.readString();
	}


	public static final Parcelable.Creator<FolderModel> CREATOR = new Parcelable.Creator<FolderModel>() {
		public FolderModel createFromParcel(Parcel in) {

			return new FolderModel(in);
		}

		public FolderModel[] newArray(int size) {
			return new FolderModel[size];
		}
	};

	public FolderModel(String folderName, String folderPath, String albumArt, int folderPosition, Bitmap folderImg, int type) {
		this.folderName = folderName;
		this.folderPath = folderPath;
		this.albumArt = albumArt;
		this.folderPosition = folderPosition;
		//this.folderImg = folderImg;
		this.type = type;
	}

	public FolderModel() {

	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}


	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderShortPath(String folderFullPath) {
		this.folderShortPath = folderFullPath;
	}

	public String getFolderShortPath() {
		return folderShortPath;
	}

	public void setAlbumArt(String albumArt) {
		this.albumArt = albumArt;
	}

	public String getAlbumArt() {
		return albumArt;
	}

	public void setFolderPosition(int folderPosition) {
		this.folderPosition = folderPosition;
	}

	public int getFolderPosition() {
		return folderPosition;
	}

	/*
	 public void setFolderImg(Bitmap folderImg) {
	 this.folderImg = folderImg;
	 }

	 public Bitmap getFolderImg() {
	 return folderImg;
	 }
	 */

}
