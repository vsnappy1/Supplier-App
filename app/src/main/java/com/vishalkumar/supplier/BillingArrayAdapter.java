package com.vishalkumar.supplier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BillingArrayAdapter extends ArrayAdapter<BillingItem> {


    public BillingArrayAdapter(Context context,  ArrayList<BillingItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.billing_item, parent, false);
        }
        //Get the position of current item/product
        BillingItem currentItem = getItem(position);

        // Find all the text views in billing_item.xml file
        TextView product = listItemView.findViewById(R.id.textViewProductItem);
        TextView quantity = listItemView.findViewById(R.id.textViewQtyItem);
        TextView tradePrice = listItemView.findViewById(R.id.textViewTPItem);
        TextView discountOrPrice = listItemView.findViewById(R.id.textViewDisountPriceItem);
        TextView unitPrice = listItemView.findViewById(R.id.textViewUnitItem);
        TextView total = listItemView.findViewById(R.id.textViewTotalItem);

        // Get detail of current item
        int id = currentItem.getId();
        String name = currentItem.getName();
        int qty = currentItem.getQuantity();
        Double tp = currentItem.getTP();
        Double discountORPrice = currentItem.getDiscountOrPrice();
        boolean isNet = currentItem.getIsNet();
        Double unit;
        // If its a net item
        if(isNet){
            unit = discountORPrice;
        }
        else {
            unit = tp - (tp*(discountORPrice/100));

        }

        //Cost of item per piece
        Double totalCostOfItem = qty * unit;

        // Set Text on the text view
        product.setText(name);
        quantity.setText("Qty: "+String.valueOf(qty));
        // If its a net item
        if(isNet){
            tradePrice.setText("TP: "+"--");
            discountOrPrice.setText("%"+"--");
            unitPrice.setText("Unit: "+String.valueOf(unit));
            total.setText("Total: "+String.valueOf(totalCostOfItem));
        }
        // If Its a TP Item
        else {
            tradePrice.setText("TP: "+String.valueOf(tp));
            discountOrPrice.setText("%"+String.valueOf(discountORPrice));
            unitPrice.setText("Unit: "+String.valueOf(unit));
            total.setText("Total: "+String.valueOf(totalCostOfItem));
        }

        return listItemView;
    }


}

