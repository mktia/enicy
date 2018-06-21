package com.mktia.www.enicy;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mktia.www.enicy.data.MyAccountsContract.MyAccountsEntry;

/**
 * Displays list of my accounts that were entered and stored in the app.
 */
public class MyAccountsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MyAccountsActivity.class.getSimpleName();

    public MyAccountsCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_accounts);

        // Setup FAB to open MyAccountEditorActivity
        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccountsActivity.this, MyAccountsEditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView accountListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        accountListView.setEmptyView(emptyView);

        mCursorAdapter = new MyAccountsCursorAdapter(this, null);
        accountListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link MyAccountsEditorActivity}
                Intent intent = new Intent(MyAccountsActivity.this, UserListActivity.class);

                // Form the content URI that represents the specific account that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link MyAccountsEntry#CONTENT_URI}
                // For example, the URI would be "content://com.mktia.www.enicy/myaccounts/2"
                // if the account with ID 2 was clicked on.
                Uri currentAccountUri = ContentUris.withAppendedId(MyAccountsEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentAccountUri);

                // Launch the {@link MyAccountsEditorActivity} to display the data for the current accounts.
                startActivity(intent);
            }
        });

        // Setup the item click listener
        accountListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link MyAccountsEditorActivity}
                Intent intent = new Intent(MyAccountsActivity.this, MyAccountsEditorActivity.class);

                // Form the content URI that represents the specific account that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link MyAccountsEntry#CONTENT_URI}
                // For example, the URI would be "content://com.mktia.www.enicy/myaccounts/2"
                // if the account with ID 2 was clicked on.
                Uri currentAccountUri = ContentUris.withAppendedId(MyAccountsEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentAccountUri);

                // Launch the {@link MyAccountsEditorActivity} to display the data for the current accounts.
                startActivity(intent);

                return true;
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllMyAccounts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllMyAccounts() {
        int rowsDeleted = getContentResolver().delete(MyAccountsEntry.CONTENT_URI, null, null);
        Log.v("MyAccountsActivity", rowsDeleted + " rows deleted from account database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = MyAccountsEntry.CONTENT_URI;
        String[] projection = {
                MyAccountsEntry._ID,
                MyAccountsEntry.COLUMN_USERNAME
        };

        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}