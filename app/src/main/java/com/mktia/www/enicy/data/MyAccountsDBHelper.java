package com.mktia.www.enicy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mktia.www.enicy.data.MyAccountsContract.MyAccountsEntry;

public class MyAccountsDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MyAccountsDBHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "myaccounts.db";

    public MyAccountsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + MyAccountsEntry.TABLE_NAME + " ("
                + MyAccountsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MyAccountsEntry.COLUMN_USERNAME + " TEXT NOT NULL, "
                + MyAccountsEntry.COLUMN_PASSWORD + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
