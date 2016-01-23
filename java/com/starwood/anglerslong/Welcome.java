/*
 * USED FOR SINGLETON PATTERN
 */

package com.starwood.anglerslong;

import android.annotation.SuppressLint;
import android.view.View;

public class Welcome {
	
	private static Welcome instance = new Welcome();
	
	private Welcome() {
		
	}
	
	public static Welcome getInstance() {
		return instance;
	}
	
	// Do something else here.
	@SuppressLint("NewApi") 
	public void fadeIn(View v, int duration) {

	    // Set the content view to 0% opacity but visible, so that it is visible
	    // (but fully transparent) during the animation.
	    v.setAlpha(0f);
	    v.setVisibility(View.VISIBLE);

	    // Animate the content view to 100% opacity, and clear any animation
	    // listener set on the view.
	    v.animate()
	            .alpha(1f)
	            .setDuration(duration)
	            .setListener(null);

	    // Animate the loading view to 0% opacity. After the animation ends,
	    // set its visibility to GONE as an optimization step (it won't
	    // participate in layout passes, etc.)
//	    mLoadingView.animate()
//	            .alpha(0f)
//	            .setDuration(mShortAnimationDuration)
//	            .setListener(new AnimatorListenerAdapter() {
//	                @Override
//	                public void onAnimationEnd(Animator animation) {
//	                    mLoadingView.setVisibility(View.GONE);
//	                }
//	            });
	}

    // Do something else here.
    @SuppressLint("NewApi")
    public void fadeOut(View v, int duration) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        v.setAlpha(1f);
        v.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        v.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
//	    mLoadingView.animate()
//	            .alpha(0f)
//	            .setDuration(mShortAnimationDuration)
//	            .setListener(new AnimatorListenerAdapter() {
//	                @Override
//	                public void onAnimationEnd(Animator animation) {
//	                    mLoadingView.setVisibility(View.GONE);
//	                }
//	            });
    }

}
