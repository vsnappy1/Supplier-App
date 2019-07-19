package com.vishalkumar.supplier.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vishalkumar.supplier.data.SupplierContract.ProductEntry;

public class SupplierDbHelper extends SQLiteOpenHelper {


    //Database name
    private static final String DATABASE_NAME = "supplier.db";

    //Database Version
    private static final int DATABASE_VERSION = 1;


    public SupplierDbHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {



        //Query to create Table
        String createTable = "CREATE TABLE " +
                SupplierContract.ProductEntry.TABLE_NAME+" ( " +
                ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ProductEntry.COLUMN_PRODUCT_NAME +" TEXT NOT NULL,"+
                ProductEntry.COLUMN_PRODUCT_TP +" REAL NOT NULL, "+
                ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "+
                ProductEntry.COLUMN_PRODUCT_DISCOUNT + " REAL DEFAULT 0, "+
                ProductEntry.COLUMN_PRODUCT_FINAL_RATE +" REAL NOT NULL );";

        //Execute the query
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
