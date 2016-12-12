package com.tikay.medialibrary.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class RoundImage extends RoundedBitmapDisplayer
{


		public RoundImage() {
				super(0);
		}

		@Override
		public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
				if (!(imageAware instanceof ImageViewAware)) {
						throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
				}

				imageAware.setImageDrawable(new CircleDrawable(bitmap, margin));
		}

		public static class CircleDrawable extends RoundedDrawable
		{

				private Bitmap mBitmap;

				public CircleDrawable(Bitmap bitmap, int margin) {
						super(bitmap, 0, margin);
						this.mBitmap = bitmap;
				}

				@Override
				public void draw(Canvas canvas) {
						int radius = 0;
						if (mBitmap.getWidth() > mBitmap.getHeight()) {
								radius = mBitmap.getHeight() / 2;
						} else {
								radius = mBitmap.getWidth() / 2;
						}
						canvas.drawRoundRect(mRect, radius, radius, paint);
				}
		}


		/*
		 DisplayImageOptions options = new DisplayImageOptions.Builder()
		 .displayer(new RoundImage())
		 .cacheInMemory(true)
		 .cacheOnDisk(true)
		 .build();
		 //ImageLoader.getInstance().displayImage(url, imageView,options);
		 */
}
