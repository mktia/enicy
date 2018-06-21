package com.mktia.www.enicy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UserPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    /**
     * Create a new {@link UserPagerAdapter} object.
     *
     * @param context is the context of the app
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public UserPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NotFollowBackFragment();
            case 1:
                return new NotFollowingFragment();
            case 2:
                return new MutualFriendsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.user_list_only_following);
            case 1:
                return mContext.getString(R.string.user_list_only_followers);
            case 2:
                return mContext.getString(R.string.user_list_mulual_friends);
            default:
                return null;
        }
    }
}

