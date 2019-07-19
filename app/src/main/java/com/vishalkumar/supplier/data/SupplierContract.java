package com.vishalkumar.supplier.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// This class will contain constants for the app and Database
public class SupplierContract {


    //Authorities
    public static final String CONTENT_AUTHORITY ="com.vishalkumar.supplier.provider";

    //Content Authority
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Table Path
    public static final String PRODUCT_PATH = "products";


    // This will contain constants for Database
    public static final class ProductEntry implements BaseColumns {

        //Table Name
        public static final String TABLE_NAME = "products";

        //unique id number for product
        //type INTEGER
        public static final String _ID = BaseColumns._ID;

        //Product Name
        //type TEXT
        public static final String COLUMN_PRODUCT_NAME = "name";

        //Product Trade Price(TP)
        //type REAL
        public static final String COLUMN_PRODUCT_TP = "tp";

        //Product Quantity(QTY)
        //type INTEGER
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";

        //Product Discount
        //type REAL
        public static final String COLUMN_PRODUCT_DISCOUNT = "discount";

        //Product Final Rate after discount
        //type REAL
        public static final String COLUMN_PRODUCT_FINAL_RATE = "final_rate";


        //Content Uri to access product data in provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PRODUCT_PATH);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCT_PATH;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCT_PATH;






    }

}
