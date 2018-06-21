package com.mktia.www.enicy;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowingRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetUserFollowersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class UserListLoader extends AsyncTaskLoader<List<InstagramUserSummary>> {

    public static final String LOG_TAG = UserListLoader.class.getSimpleName();

    private String mUserName;
    private String mPassword;
    private int mIndex;

    public UserListLoader(Context context, String userName, String password, int index) {
        super(context);
        mUserName = userName;
        mPassword = password;
        mIndex = index;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<InstagramUserSummary> loadInBackground() {
        Instagram4Android instagram = Instagram4Android.builder().username(mUserName).password(mPassword).build();
        instagram.setup();

        try {
            instagram.login();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long userId = instagram.getUserId();
        if (userId == 0) {
            return null;
        }

        List<InstagramUserSummary> followingList = getFollowing(instagram, userId);
        List<InstagramUserSummary> followersList = getFollowers(instagram, userId);

        return analyse(followingList, followersList, mIndex);
    }

    private List<InstagramUserSummary> getFollowers(Instagram4Android instagram, long userId) {
        List<InstagramUserSummary> returnList = new ArrayList<>();
        List<InstagramUserSummary> tmpList;

        try {
            InstagramGetUserFollowersResult result;
            String maxId = null;

            while (true) {
                result = instagram.sendRequest(new InstagramGetUserFollowersRequest(userId, maxId));
                tmpList = result.getUsers();

                // If the list is big, get the next max id
                maxId = result.getNext_max_id();

                returnList.addAll(tmpList);
                if (!result.isBig_list()) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    private List<InstagramUserSummary> getFollowing(Instagram4Android instagram, long userId) {
        List<InstagramUserSummary> returnList = new ArrayList<>();
        List<InstagramUserSummary> tmpList;

        try {
            InstagramGetUserFollowersResult result;
            String maxId = null;

            while (true) {
                result = instagram.sendRequest(new InstagramGetUserFollowingRequest(userId, maxId));
                tmpList = result.getUsers();

                // If the list is big, get the next max id
                maxId = result.getNext_max_id();

                returnList.addAll(tmpList);
                if (!result.isBig_list()) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    private List<InstagramUserSummary> analyse(List<InstagramUserSummary> followingList, List<InstagramUserSummary> followersList, int index) {
        List<InstagramUserSummary> onlyFollowingList = new ArrayList<>();
        List<InstagramUserSummary> onlyFollowedList = followersList;
        List<InstagramUserSummary> mutualList = new ArrayList<>();

        for (InstagramUserSummary following: followingList) {
            int count = 0;
            for (InstagramUserSummary followers: onlyFollowedList) {
                count++;
                if (following.equals(followers)) {
                    mutualList.add(following);
                    onlyFollowedList.remove(followers);
                    break;
                }
                if (followersList.size() == count) {
                    onlyFollowingList.add(following);
                }
            }
        }

        switch (index) {
            // not followback
            case 1:
                return onlyFollowingList;
            // not following
            case 2:
                return onlyFollowedList;
            // mutual
            case 3:
                return mutualList;
            default:
                return null;
        }
    }
}
