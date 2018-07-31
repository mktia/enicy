package com.mktia.www.enicy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.mktia.www.enicy.data.MyAccountsContract.MyAccountsEntry;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class NotFollowBackFragment extends Fragment implements LoaderCallbacks<List<InstagramUserSummary>> {

    private static final int NOT_FOLLOW_BACK_LOADER_ID = 1;

    public static final String LOG_TAG = MutualFriendsFragment.class.getName();

    private NetworkInfo mNetworkInfo;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mProgressBar;
    private TextView mEmptyStateTextView;
    private LinearLayout mUpdateInformation;
    private TextView mLengthOfList;
    private TextView mUpdateDateTime;
    private UserAdapter mUserAdapter;
    private AdView mAdView;

    private String mUserName;
    private String mPassword;

    // Required empty public constructor
    public NotFollowBackFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_list, container, false);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        mNetworkInfo = connectivityManager.getActiveNetworkInfo();

        final ListView userListView = rootView.findViewById(R.id.list);

        mEmptyStateTextView = rootView.findViewById(R.id.empty_text);
        userListView.setEmptyView(mEmptyStateTextView);

        // Create progressBar
        mProgressBar = rootView.findViewById(R.id.progressBar);

        // Create LinearLayout
        mUpdateInformation = rootView.findViewById(R.id.update_information);
        mUpdateInformation.setVisibility(View.INVISIBLE);

        // Create lengthOfList
        mLengthOfList = rootView.findViewById(R.id.length_of_list);

        // Create updateDateTime
        mUpdateDateTime = rootView.findViewById(R.id.update_datetime);

        // Create SwipeRefreshLayout
        mSwipeRefresh = rootView.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent);

        // Create a new adapter that takes an empty list of users as input
        mUserAdapter = new UserAdapter(getActivity(), new ArrayList<InstagramUserSummary>());
        userListView.setAdapter(mUserAdapter);

        loadData(mNetworkInfo, true);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(mNetworkInfo, false);
            }
        });

        final PackageManager packageManager = getContext().getPackageManager();
        final String instagramPackage = "com.instagram.android";

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InstagramUserSummary user = (InstagramUserSummary) userListView.getItemAtPosition(i);

                try {
                    if (packageManager.getPackageInfo(instagramPackage, 0) != null) {
                        Uri uri = Uri.parse("https://instagram.com/_u/" + user.getUsername());

                        Intent intentToInstagram = new Intent(Intent.ACTION_VIEW, uri);
                        intentToInstagram.setPackage(instagramPackage);

                        startActivity(intentToInstagram);
                    } else {
                        // not working
                        Toast.makeText(getContext(), R.string.instagram_is_not_installed, Toast.LENGTH_SHORT).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        MobileAds.initialize(getContext(), "ca-app-pub-3718490269566520~4782774815");
        mAdView = rootView.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return rootView;
    }

    @Override
    public Loader<List<InstagramUserSummary>> onCreateLoader(int id, Bundle args) {
        if (mUserName == null && mPassword == null) {
            Intent intent = getActivity().getIntent();
            Uri currentAccountUri = intent.getData();

            if (currentAccountUri == null) {
                return null;
            }

            String[] projection = {
                    MyAccountsEntry._ID,
                    MyAccountsEntry.COLUMN_USERNAME,
                    MyAccountsEntry.COLUMN_PASSWORD
            };

            Cursor cursor = getContext().getContentResolver().query(currentAccountUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                mUserName = cursor.getString(cursor.getColumnIndex("username"));
                mPassword = cursor.getString(cursor.getColumnIndex("password"));
                cursor.close();
            } else {
                // If cursor is null, cannot create loader.
                return null;
            }
        }

        // Already get data
        if (!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPassword)) {
            return new UserListLoader(getActivity(), mUserName, mPassword, 1);
        } else {
            Toast.makeText(getContext(), R.string.empty_username_or_password, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MyAccountsActivity.class));
            // If cursor is null, cannot create loader.
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<InstagramUserSummary>> loader, List<InstagramUserSummary> users) {
        // Clear the adapter of previous data.
        mUserAdapter.clear();

        String message = "";
        if (users != null && !users.isEmpty()) {

            // Check login status and display the reason why the user is failed to login
            long checkErrorStatus = users.get(0).getPk();
            if (checkErrorStatus < 0) {
                if (checkErrorStatus == -1) {
                    Toast.makeText(getContext(), R.string.password_is_incorrect, Toast.LENGTH_SHORT).show();
                } else if (checkErrorStatus == -2) {
                    Toast.makeText(getContext(), R.string.username_is_not_found, Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(getContext(), MyAccountsActivity.class));
            }

            mUserAdapter.addAll(users);

            // Display the number of users in the list
            int length = users.size();
            if (length == 1) {
                message = "1 " + getText(R.string.person);
            } else {
                message = String.valueOf(length) + " " + getText(R.string.people);
            }
        }
        mLengthOfList.setText(message);

        mUserAdapter.notifyDataSetChanged();

        // Display current time
        Calendar calendar = Calendar.getInstance();
        String currentDateTime = DateFormat.getDateTimeInstance().format(calendar.getTime());
        mUpdateDateTime.setText(currentDateTime);

        // After getting currentDataTime, display update information
        mUpdateInformation.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.GONE);

        mSwipeRefresh.setRefreshing(false);

        mEmptyStateTextView.setText(R.string.no_users);

        getLoaderManager().destroyLoader(NOT_FOLLOW_BACK_LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<List<InstagramUserSummary>> loader) {
        // Loader reset, so we can clear out our existing data.
        mUserAdapter.clear();
    }

    private void loadData(NetworkInfo networkInfo, boolean isFirst) {
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            if (isFirst) {
                loaderManager.initLoader(NOT_FOLLOW_BACK_LOADER_ID, null, this);
            } else {
                loaderManager.restartLoader(NOT_FOLLOW_BACK_LOADER_ID, null, this);
            }
        } else {
            // If not connect to the Internet
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }
}
