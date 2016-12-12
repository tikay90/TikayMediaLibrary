package com.tikay.medialibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tikay.medialibrary.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils
{
	private static final String TAG = "ImageUtils";
	private static MediaMetadataRetriever metaRetriver;
	private static byte[] art = null;
	private static Bitmap bmp = null;

	public static void loadThumbnails(String path, ImageView iv, Context context) {
		ImageLoader imageloader = ImageLoader.getInstance();
		imageloader.init(ImageLoaderConfiguration.createDefault(context));

		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.resetViewBeforeLoading(false)
			.showImageForEmptyUri(R.drawable.music_m_logo)
			.showImageOnFail(R.drawable.music_m_logo)
			.showImageOnLoading(R.drawable.music_m_logo)
			.build();
		try {
			imageloader.displayImage(path, iv, options);
		} catch(Exception e) {

		}

	}

	public static void loadRoundedThumbnails(String path, ImageView iv, Context context) {
		ImageLoader imageloader = ImageLoader.getInstance();
		imageloader.init(ImageLoaderConfiguration.createDefault(context));
		try {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.resetViewBeforeLoading(false)
				.displayer(new RoundImage())
				.showImageForEmptyUri(R.drawable.music_m_logo)
				.showImageOnFail(R.drawable.music_m_logo)
				.showImageOnLoading(R.drawable.music_m_logo)
				.build();

			imageloader.displayImage(path, iv, options);
		} catch(Exception e) {		
			System.out.println(e.getMessage());
		}
	}

	public static void displayRoundImage(String path, ImageView iv) {
		// imageloader is already initialized in MyApp class i.e the application class
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(path,iv);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 1000;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}


	//-- 18/12/15
	public static Bitmap getThumbnails(String path) {
		try {
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 2;

			metaRetriver = new MediaMetadataRetriever();
			metaRetriver.setDataSource(path);
			try {
				art = metaRetriver.getEmbeddedPicture();
				bmp = BitmapFactory.decodeByteArray(art, 0, art.length, bmOptions);

				if(bmp != null) {
					return bmp;
				}
				metaRetriver.release();
				return null;
			} catch(Exception e) {
				//Log.e("UTILITIES", "IN UTILITIES: " + e.getCause() + ", " + e.getStackTrace() + ", " + e.getMessage());
				//e.printStackTrace();
			}
		} catch(Exception e) {
			//e.printStackTrace();
		}
		return null;
	}



	/**
	 * Stores an image on the storage
	 * 
	 * @param image
	 *            the image to store.
	 * @param pictureFile
	 *            the file in which it must be stored
	 */
	public static void storeImage(Bitmap image, File pictureFile) {
		if(pictureFile == null) {
			Log.d(TAG, "Error creating media file, check storage permissions: ");
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch(FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch(IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}

	/**
	 * Get the screen height.
	 * 
	 * @param context
	 * @return the screen height
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenHeight(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.y;
		}
		return display.getHeight();
	}

	/**
	 * Get the screen width.
	 * 
	 * @param context
	 * @return the screen width
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenWidth(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}
		return display.getWidth();
	}


}
