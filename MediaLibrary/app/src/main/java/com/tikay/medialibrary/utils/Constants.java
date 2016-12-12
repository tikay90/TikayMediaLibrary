package com.tikay.medialibrary.utils;
import com.tikay.medialibrary.models.AlbumTracksModel;
import com.tikay.medialibrary.models.ArtistTracksModel;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.models.PlaylistModel;
import com.tikay.medialibrary.models.PlaylistTrackModel;
import com.tikay.medialibrary.models.TracksModel;
import java.util.ArrayList;

public class Constants
{
	public final static int FIRST_ROW = 0;
	public final static int OTHER_ROW = 1;
	//public static int TOTAL_TRACKS = 0;

	public final static String ALBUMS = "Albums";
	public final static String Playlists = "Playlists";
	public final static String Artists = "Artists";
	public final static String Folders = "Folders";
	public final static String Genre = "Genre";
	public final static String Tracks = "Tracks";
	public final static String ArtistTracks = "ArtistTracks";
	public final static String AlbumTracks = "AlbumTracks";
	public static final int audioSessionId = 123;

	public static final String BROADCAST_ALBUMS = "com.tikay.musiclibrary.ALBUMS";
	public static final String BROADCAST_TRACKS = "com.tikay.musiclibrary.TRACKS";
	public static final String BROADCAST_PLAYLISTS = "com.tikay.musiclibrary.PLAYLISTS";
	public static final String BROADCAST_FOLDERS = "com.tikay.musiclibrary.FOLDERS";
	public static final String BROADCAST_UNIVERSAL = "com.tikay.musiclibrary.UNIVERAL";
	public static final String BROADCAST_RECEIVED = "com.tikay.musiclibrary.RECEIVED";

	public static final String SEND = "SEND";
	public static final String RECEIVE = "RECEIVE";

	public static ArrayList<FolderModel> FOLDER_TRACKS = new ArrayList<FolderModel>();
	public static ArrayList<FolderModel> CURRENT_FOLDER_TRACKS = new ArrayList<FolderModel>();

	public static ArrayList<AlbumTracksModel> ALBUM_TRACKS = new ArrayList<AlbumTracksModel>();
	public static ArrayList<AlbumTracksModel> CURRENT_ALBUM_TRACKS = new ArrayList<AlbumTracksModel>();

	public static ArrayList<ArtistTracksModel> ARTIST_TRACKS = new ArrayList<ArtistTracksModel>();
	public static ArrayList<ArtistTracksModel> CURRENT_ARTIST_TRACKS = new ArrayList<ArtistTracksModel>();

	public static ArrayList<PlaylistTrackModel> PLAYLIST_TRACKS = new ArrayList<PlaylistTrackModel>();
	public static ArrayList<PlaylistTrackModel> CURRENT_PLAYLIST_TRACKS = new ArrayList<PlaylistTrackModel>();
	public static ArrayList<PlaylistModel> ALL_PLAYLIST = new ArrayList<PlaylistModel>() ;
	
	public static ArrayList<TracksModel> TRACKS = new ArrayList<TracksModel>();
	public static ArrayList<TracksModel> CURRENT_TRACKS = new ArrayList<TracksModel>();

	public static ArrayList<TracksModel> GENRE_TRACKS = new ArrayList<TracksModel>();
	public static ArrayList<TracksModel> CURRENT_GENRE_TRACKS = new ArrayList<TracksModel>();




}
