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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.mktia.www.enicy.data.MyAccountsContract.MyAccountsEntry;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class NotFollowBackFragment extends Fragment implements LoaderCallbacks<List<InstagramUserSummary>> {

    private static final int NOT_FOLLOW_BACK_LOADER_ID = 1;

    public static final String LOG_TAG = MutualFriendsFragment.class.getName();

    private ProgressBar mProgressBar;
    private TextView mEmptyStateTextView;
    private UserAdapter mUserAdapter;
    private AdView mAdView;

    // Required empty public constructor
    public NotFollowBackFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_list, container, false);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        final ListView userListView = rootView.findViewById(R.id.list);

        mEmptyStateTextView = rootView.findViewById(R.id.empty_text);
        userListView.setEmptyView(mEmptyStateTextView);
        mProgressBar = rootView.findViewById(R.id.progressBar);

        // Create a new adapter that takes an empty list of users as input
        mUserAdapter = new UserAdapter(getActivity(), new ArrayList<InstagramUserSummary>());
        userListView.setAdapter(mUserAdapter);

        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(NOT_FOLLOW_BACK_LOADER_ID, null, this);
        } else {
            // If not connect to the Internet
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

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
            String userName = cursor.getString(cursor.getColumnIndex("username"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            cursor.close();

            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                return new UserListLoader(getActivity(), userName, password, 1);
            } else {
                Toast.makeText(getContext(), R.string.empty_username_or_password, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        // If cursor is null, do not create loader.
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<InstagramUserSummary>> loader, List<InstagramUserSummary> users) {
        // Clear the adapter of previous data.
        mUserAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (users != null && !users.isEmpty()) {
            mUserAdapter.addAll(users);
        }

        mProgressBar.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_users);
    }

    @Override
    public void onLoaderReset(Loader<List<InstagramUserSummary>> loader) {
        // Loader reset, so we can clear out our existing data.
        mUserAdapter.clear();
    }
}
