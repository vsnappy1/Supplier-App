package com.vishalkumar.supplier;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.vishalkumar.supplier.data.SupplierContract.ProductEntry;

import java.util.ArrayList;

public class BillingActivity extends AppCompatActivity {

    //Grand Total Bill
    Double grandTotalBill = 0.0;

    //For searching product
    SearchView productSearch;

    //Quantity of product selected
    EditText editTextQuantity;

    // Discount or Price of product
    EditText editTextDiscountPrice;

    //Grand Total
    TextView textViewGrandTotal;

    //List view for displaying selected item for sale
    ListView listViewBilling;

    // To hold the data which is the result of a query
    Cursor cursor;

    // Custom CursorAdapter to show only names of product in search view
    SearchProductCursorAdapter mSuggestionCursorAdapter;

    //Add FA.Button
    FloatingActionButton FABaddItem;

    //Adapter for billing
    BillingArrayAdapter mBillingArrayAdapter;

    // This are list contain the products to be sold along with quantity to be sold
    ArrayList<BillingItem> mBillingItems;

    //Name of product from database
    String productName;

    //Trade Price of product from database
    Double productTP;

    //Id of product from database
    int productId;

    //Available quantity of product from database
    int productStockQuantity;

    //discount of product from database
    Double productDiscount;

    //Is it a net item
    boolean isNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        //find views bt their id's
        productSearch = findViewById(R.id.searchViewBilling);
        editTextQuantity = findViewById(R.id.editTextBillingQty);
        editTextDiscountPrice = findViewById(R.id.editTextBillingDiscountPrice);
        textViewGrandTotal = findViewById(R.id.textViewGrandTotal);
        listViewBilling = findViewById(R.id.listViewBilling);
        FABaddItem = findViewById(R.id.floatingActionButtonAdd);

        //Initialize SuggestionCursorAdapter
        mSuggestionCursorAdapter = new SearchProductCursorAdapter(this,null);

        //Initialize billing items arraylist which contains the detail of product to be sold
        mBillingItems = new ArrayList<BillingItem>();

        // Initialize BillingArrayAdapter
        mBillingArrayAdapter = new BillingArrayAdapter(this,mBillingItems);

        //Set arrayAdapter on listView
        listViewBilling.setAdapter(mBillingArrayAdapter);

        //Set all product names in the suggestion of search view to avoid speling mistake
        // and make it easy to use
        // Now whenever text is changed in search view we perform a query
        // and get the filtered result
        productSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                // Define a projection that specifies the columns from the table we care about.
                String [] projection = {
                        ProductEntry._ID,
                        ProductEntry.COLUMN_PRODUCT_NAME,
                        ProductEntry.COLUMN_PRODUCT_TP,
                        ProductEntry.COLUMN_PRODUCT_QUANTITY,
                        ProductEntry.COLUMN_PRODUCT_DISCOUNT
                };

                //we filter the data using the letters in the search view
                String selection = ProductEntry.COLUMN_PRODUCT_NAME + " LIKE '"+s.trim()+"%'";

                cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection,selection,null, ProductEntry.COLUMN_PRODUCT_NAME);

                mSuggestionCursorAdapter.swapCursor(cursor);
                return false;
            }
        });

        //Now set the suggestion adapter on Search View
        productSearch.setSuggestionsAdapter(mSuggestionCursorAdapter);

        // Set suggestion click listener
        productSearch.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                //move the cursor to respective row
                cursor.moveToPosition(i);

                // Get the Idetail of product form cursor
                productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
                productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
                productTP = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TP));
                productStockQuantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
                productDiscount = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DISCOUNT));
                // Set this name as text in search view
                productSearch.setQuery(productName,false);

                return false;
            }
        });

        // Set on click listener on FAB
        FABaddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(productSearch.getQuery())){
                    Toast.makeText(BillingActivity.this, "Please Enter Product Name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(editTextQuantity.getText())){
                    Toast.makeText(BillingActivity.this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(editTextDiscountPrice.getText())){
                    Toast.makeText(BillingActivity.this, "Please Enter % / Price", Toast.LENGTH_SHORT).show();
                }
                else {
                    addItemInListView();
                }
            }
        });
    }

    //Setup one menu create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_billing, menu);
        return true;
    }

    //Setup on menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.proceed:
                proceedDialogBox();
        }
        return true;
    }

    //Proceed and update database
    private void proceedAndUpdateDatabase() {

        int Id;             // Id of product
        int stockQuantity;  // quantity of product in stock
        int quantity;       // Quantity of a product to be sold
        int newQuantity;    // new quantity in database after proceeding
        BillingItem currentItem;

        // get number of items
        int numberOfItems = mBillingItems.size();
        int item = 0;
        while (item < numberOfItems){

            // Get the detail of current item
            currentItem = mBillingItems.get(item);
            Id = currentItem.getId();
            stockQuantity = currentItem.getStockQuantity();
            quantity = currentItem.getQuantity();
            newQuantity = stockQuantity - quantity;

            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

            // Update on the bases of id of product
            String selection = ProductEntry._ID +"=?;";
            String[] selectionArgs = new String[]{String.valueOf(Id)};

            // Now update the database (Reduce the quantity of product which is sold)
            getContentResolver().update(ProductEntry.CONTENT_URI, values,selection,selectionArgs);

            item++;
        }



    }

    // Add items in array list to be displayed
    private void addItemInListView(){

        //Get data from edit Fields i.e discount and quantity
        Double discount = Double.parseDouble(editTextDiscountPrice.getText().toString());
        int quantity    = Integer.parseInt(editTextQuantity.getText().toString());


        // Ii discount is 0 then it is a net Item
        if(productDiscount == 0){
            isNet = true;
        }
        else {
            isNet = false;
        }
        //create a new billing item object
        BillingItem item = new BillingItem(
                productId,
                productName,
                quantity,
                productStockQuantity,
                productTP,
                discount,
                isNet);

        //add this item in list
        mBillingItems.add(item);

        //update the list by setting the adapter once more
        listViewBilling.setAdapter(mBillingArrayAdapter);

        //Calculate grand total
        grandTotalBill = grandTotalBill + getTotalCostOfItem();

        //Set this grand total in grandTotal TextView
        textViewGrandTotal.setText("Total: "+String.valueOf(grandTotalBill));

        //Clear Text Fields
        productSearch.setQuery(null,false);
        editTextQuantity.setText(null);
        editTextDiscountPrice.setText(null);
    }

    // Show dialog box before proceeding
    private void proceedDialogBox(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Proceed Bill?");
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Update database
                proceedAndUpdateDatabase();
                // Clear the array list of old items which are sold
                mBillingItems.clear();
                // Also clear arrayAdapter
                mBillingArrayAdapter.clear();
                // Make grand Total zero
                grandTotalBill = 0.;
                //Set this grand total in grandTotal TextView
                textViewGrandTotal.setText("Total: 0");
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Discard the bill
                // Clear the array list
                mBillingItems.clear();
                // Also clear arrayAdapter
                mBillingArrayAdapter.clear();
                // Make grand Total zero
                grandTotalBill = 0.;
                //Set this grand total in grandTotal TextView
                textViewGrandTotal.setText("Total: 0");
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do Nothing
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //Get the total cost of one item
    private Double getTotalCostOfItem(){

        // Per Unit price of item
        Double unit;
        //Discount on item or price of item
        Double discountORPrice = Double.parseDouble(editTextDiscountPrice.getText().toString().trim());
        // trade price of item
        Double tp = Double.parseDouble(productTP.toString().trim());
        // Quantity of item to be sold
        int qty = Integer.parseInt(editTextQuantity.getText().toString().trim());
        // If its a net item (Net item is an item which is sold on basis of price not discount)
        if(isNet){
            unit = discountORPrice;
        }
        else {
            unit = tp - (tp*(discountORPrice/100));
        }

        //Cost of item per piece
        Double totalCostOfItem = qty * unit;

        return totalCostOfItem;
    }

    private void unProcessedDataDialogBox(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Exit the billing activity
               finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do Nothing
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Overide the on backpressed to ask user wether he/she wants to exit or not
    @Override
    public void onBackPressed() {
        //If there is atleast one l item in list then pop upp the dialog box
        if(mBillingArrayAdapter.getCount() > 0){
            unProcessedDataDialogBox();
        }
        else {
            // Else do the default
            super.onBackPressed();
        }
    }
}
