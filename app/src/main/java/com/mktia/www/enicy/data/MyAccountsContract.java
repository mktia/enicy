package com.mktia.www.enicy.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MyAccountsContract {

    public static final String CONTENT_AUTHORITY = "com.mktia.www.enicy";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MY_ACCOUNTS = "myaccounts";

    private MyAccountsContract() {}

    public static final class MyAccountsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MY_ACCOUNTS);

        public final static String TABLE_NAME = "myaccounts";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_USERNAME = "username";
        public final static String COLUMN_PASSWORD = "password";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of my accounts.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_ACCOUNTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for my single account.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_ACCOUNTS;
    }
}
