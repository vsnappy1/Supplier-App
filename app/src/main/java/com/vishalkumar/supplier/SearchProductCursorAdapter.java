package com.vishalkumar.supplier;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.vishalkumar.supplier.R;
import com.vishalkumar.supplier.data.SupplierContract;

public class SearchProductCursorAdapter extends CursorAdapter {

    public SearchProductCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.search_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Find view in search_item.xml file
        TextView productName = view.findViewById(R.id.textViewSearchItem);
        TextView discountOrPrice = view.findViewById(R.id.textViewSearchItemDiscountOrPrice);

        //Get the column index from cursor
        int nameIndex = cursor.getColumnIndex(SupplierContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int tpIndex = cursor.getColumnIndex(SupplierContract.ProductEntry.COLUMN_PRODUCT_TP);
        int discountIndex = cursor.getColumnIndex(SupplierContract.ProductEntry.COLUMN_PRODUCT_DISCOUNT);

        // Get the detail of product
        String name = cursor.getString(nameIndex);
        Double tp = cursor.getDouble(tpIndex);
        Double discount = cursor.getDouble(discountIndex);

        //Set this as text of productName TextView
        productName.setText(name);

        //If product has a discount than show that discount in search
        if(discount > 0){
            discountOrPrice.setText("%"+String.valueOf(discount));
        }
        //Else if product is a net item and has no discount but only price than show its price
        else {
            discountOrPrice.setText("TP "+String.valueOf(tp));
        }
    }
}
