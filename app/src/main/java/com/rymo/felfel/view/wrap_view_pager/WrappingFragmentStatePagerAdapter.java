package com.rymo.felfel.view.wrap_view_pager;


import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * FragmentStatePagerAdapter that automatically works with WrappingViewPager, if you don't want to
 * implement the necessary logic in the adapter yourself.
 * <p>
 * Make sure you only use this adapter with a {@link WrappingViewPager}, otherwise your app
 * will crash!
 *
 * @author Santeri Elo
 * @since 14-06-2016
 */
public abstract class WrappingFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private int mCurrentPosition = -1;

    public WrappingFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (!(container instanceof WrappingViewPager)) {
            throw new UnsupportedOperationException("ViewPager is not a WrappingViewPager");
        }

        Fragment fragment = (Fragment) object;
        WrappingViewPager pager = (WrappingViewPager) container;
        if (fragment != null && fragment.getView() != null) {
            if (position != mCurrentPosition) {
                mCurrentPosition = position;
            }
            pager.onPageChanged(fragment.getView());
        }
    }
}
