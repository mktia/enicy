package com.mktia.www.enicy;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowingRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetUserFollowersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class UserListLoader extends AsyncTaskLoader<List<InstagramUserSummary>> {

    public static final String LOG_TAG = UserListLoader.class.getSimpleName();

    private String mUserName;
    private String mPassword;
    private int mIndex;

    private boolean mIsStartedOne;
    private boolean mIsStartedTwo;
    private boolean mIsStartedThree;

    public UserListLoader(Context context, String userName, String password, int index) {
        super(context);
        mUserName = userName;
        mPassword = password;
        mIndex = index;

        switch (mIndex) {
            case 1:
                mIsStartedOne = true;
                break;
            case 2:
                mIsStartedTwo = true;
                break;
            case 3:
                mIsStartedThree = true;
                break;
        }
    }

    @Override
    protected void onStartLoading() {
        if ((mIndex == 1 && !mIsStartedOne) || (mIndex == 2 && !mIsStartedTwo) || (mIndex == 3 && !mIsStartedThree)) {
            return;
        }

        forceLoad();
    }

    @Override
    public List<InstagramUserSummary> loadInBackground() {
        switch (mIndex) {
            case 1:
                mIsStartedOne = false;
                break;
            case 2:
                mIsStartedTwo = false;
                break;
            case 3:
                mIsStartedThree = false;
                break;
        }

        Log.d(LOG_TAG, "loadInBackground: Start setup");

        Instagram4Android instagram = Instagram4Android.builder().username(mUserName).password(mPassword).build();
        instagram.setup();

        Log.d(LOG_TAG, "loadInBackground: Start login");
        InstagramLoginResult loginResult = new InstagramLoginResult();

        try {
            loginResult = instagram.login();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String loginStatus = loginResult.getMessage();
        if (loginStatus != null) {
            if (loginStatus.contains("password")) {
                UserListActivity.mErrorCausedBy = "password";
            } else if (loginStatus.contains("username")) {
                UserListActivity.mErrorCausedBy = "username";
            }
            Log.d(LOG_TAG, "loadInBackground: " + loginStatus);
            return null;
        } else {
            Log.d(LOG_TAG, "loadInBackground: succeeded login");
        }

        long userId = instagram.getUserId();
        if (userId == 0) {
            return null;
        }

//        List<InstagramUserSummary> followingList = new ArrayList<>();
//        List<InstagramUserSummary> followersList = new ArrayList<>();
//
//        try {
//            followingList = getFollowing(instagram, userId);
//            followersList = getFollowers(instagram, userId);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        List<InstagramUserSummary> followingList = getFollowing(instagram, userId);
        List<InstagramUserSummary> followersList = getFollowers(instagram, userId);

        Log.d(LOG_TAG, "loadInBackground: start analyse");
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
//            throw e;
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
            Log.d(LOG_TAG, "getFollowing: error");
            e.printStackTrace();
//            throw e;
        }

        Log.d(LOG_TAG, "getFollowing: Return following list");
        return returnList;
    }

    private List<InstagramUserSummary> analyse(List<InstagramUserSummary> followingList, List<InstagramUserSummary> followersList, int index) {
        List<InstagramUserSummary> onlyFollowingList = new ArrayList<>();
        List<InstagramUserSummary> mutualList = new ArrayList<>();

        for (InstagramUserSummary following: followingList) {
            int count = 0;
            for (InstagramUserSummary followers: followersList) {
                count++;
                if (following.equals(followers)) {
                    mutualList.add(following);
                    followersList.remove(followers);
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
                return followersList;
            // mutual
            case 3:
                return mutualList;
            default:
                return null;
        }
    }
}
