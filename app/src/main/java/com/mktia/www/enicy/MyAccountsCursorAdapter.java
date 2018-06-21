package com.mktia.www.enicy;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mktia.www.enicy.data.MyAccountsContract.MyAccountsEntry;

/**
 * {@link MyAccountsCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of account data as its data source. This adapter knows
 * how to create list items for each row of account data in the {@link Cursor}.
 */
public class MyAccountsCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link MyAccountsCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public MyAccountsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.my_account_item, parent, false);
    }

    /**
     * This method binds the account data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current account can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView usernameTextView = (TextView) view.findViewById(R.id.username);

        // Find the columns of my account attributes that you're interested in
        int usernameColumnIndex = cursor.getColumnIndex(MyAccountsEntry.COLUMN_USERNAME);

        // Read the account attributes from the Cursor for the current account
        String userName = cursor.getString(usernameColumnIndex);

        // Update the TextViews with the attributes for the current account
        usernameTextView.setText(userName);
    }
}
