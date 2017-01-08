package com.tikay.medialibrary.utils;
import android.support.v7.widget.RecyclerView;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

public class AnimUtils
{
	public static void animateRecyclerView(RecyclerView.ViewHolder holder,boolean goesDown){
		AnimatorSet animatorSet = new AnimatorSet();
		
		ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView,"translationY",goesDown==true?150:-150,0);
		animatorTranslateY.setDuration(600);
		
		animatorSet.playTogether(animatorTranslateY);
		animatorSet.start();
		
		
		
	}
}
