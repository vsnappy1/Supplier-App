package com.vishalkumar.supplier;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.vishalkumar.supplier.data.SupplierContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_item_product, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        //Find All text Views by their id's
        TextView productName = (TextView) view.findViewById(R.id.textViewProductName);
        TextView productTP = (TextView) view.findViewById(R.id.textViewProductTP);
        TextView productQuantity = (TextView) view.findViewById(R.id.textViewProductQuantity);
        TextView productDiscount = (TextView) view.findViewById(R.id.textViewProductDiscount);
        TextView prodcutFinalRate = (TextView) view.findViewById(R.id.textViewFinalRate);

        //Get the indexes of columns
        int nameIndex       = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int tpIndex         = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TP);
        int quantityIndex   = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int discountIndex   = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DISCOUNT);
        int finalRateIndex  = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_FINAL_RATE);

        //Get the data/values from cursor
        String name = cursor.getString(nameIndex);
        String tp = String.valueOf(cursor.getDouble(tpIndex));
        String quantity = String.valueOf(cursor.getInt(quantityIndex));
        String discount = String.valueOf(cursor.getDouble(discountIndex));
        Double finalRate = cursor.getDouble(finalRateIndex);

        //Round of the finalRate in 2 decimal places
        String finalRateRoundOf = String.format("%.2f", finalRate);

        //Set these values in list item views
        productName.setText(name);
        productTP.setText("TP : "+tp);
        productQuantity.setText("Qty : "+quantity);
        productDiscount.setText("% : "+discount);
        prodcutFinalRate.setText("FR : "+finalRateRoundOf);

    }
}
