package com.vishalkumar.supplier.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.widget.Toast;
import com.vishalkumar.supplier.data.SupplierContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    /** URI matcher code for the content URI for the pets table */
    private static final int PRODUCT = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PRODUCT_ID = 101;

    //UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher =new UriMatcher(UriMatcher.NO_MATCH);

    // This is run the first time anything called from this class
    static {

        // This Uri is used to provide to access to multiple rows of the product Table
        sUriMatcher.addURI(SupplierContract.CONTENT_AUTHORITY, SupplierContract.PRODUCT_PATH, PRODUCT);

        // This Uri is used to provide to access single row of the product Table
        sUriMatcher.addURI(SupplierContract.CONTENT_AUTHORITY, SupplierContract.PRODUCT_PATH + "/#", PRODUCT_ID);
    }

    //Initialize SupplierDbHelper Instance Global
    SupplierDbHelper dbHelper;

    @Override
    public boolean onCreate() {

        //Create or open database
        dbHelper = new SupplierDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,  String[] columns,  String selection,  String[] selectionArgs,  String sortOrder) {

        //Get Readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //This cursor will hold the result
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                //When Whole list of data is needed
                cursor = database.query(ProductEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:

                //Extract the id from Uri and then use it to find that particular row in table
                selection = ProductEntry._ID + " =?";  //Where claue
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))}; //Values of Where Clause
                cursor = database.query(ProductEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
                // In order to update data when there is any change in database,
                // such as insertion, update, or delete of product(s), we set NotificationUri on the cursor
                //If any data at this uri changes than we know we need to update cursor
                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                // Return Cursor
                return cursor;
    }

    @Override
    public Uri insert(Uri uri,  ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match){

            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("failed to insert in db");
        }



    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        // Get Writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Insert in database, where id is the _ID of newly added row
        long id = database.insert(ProductEntry.TABLE_NAME, null, contentValues);

        if(id == -1){
            throw new IllegalArgumentException("failed to insert row for " + uri);
        }

        // Get notification, to update the cursor if data changes at uri
        getContext().getContentResolver().notifyChange(uri,null);

        //Return the uri of newly added row
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri,  String selection,  String[] selectionArgs) {

        // Get Writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsDeleted= 0 ;
        switch (match){
            case PRODUCT:
                //If whole table is to delete
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PRODUCT_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("failed to delete from db "+uri);
        }

        // Get notification, to update the cursor if data changes at uri
        getContext().getContentResolver().notifyChange(uri,null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri,  ContentValues contentValues,  String selection,  String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);

            case PRODUCT_ID:
                // Get the id of product and then update
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                return updateProduct(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("failed to update in db "+uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        //Get Writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowAffected = database.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs );

        // Get notification, to update the cursor if data changes at uri
        getContext().getContentResolver().notifyChange(uri,null);

        // Return the number of rows affected
        return rowAffected;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

}
