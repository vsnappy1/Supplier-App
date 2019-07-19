package com.vishalkumar.supplier;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.vishalkumar.supplier.data.SupplierContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText productName;
    EditText productTP;
    EditText productQuantity;
    EditText productDiscount;
    TextView productFinalRate;

    Uri currentProductUri;

    // To see if fields are touched or not
    boolean isFieldTouched = false;


    // Setup on touch listener for EditText fields to know if fields touched or not
    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            isFieldTouched = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine that intent which was used to launch this activity,
        // in order to figure out if we are adding a product or updating one
        Intent intent = getIntent();
        currentProductUri = intent.getData();

        // If intent does not contain any data it is an intent to add a product
        if(currentProductUri == null){
            //we set the title of activty as add product
            setTitle("Add Product");
        }
        //If product has some data with it, it means it is an intent to update/edit a product
        else {
            //we set the title of activty as edit product
            setTitle("Edit Product");

            //Initiate Loader so that text views are filled with detail of current product
            //Initialize Loader, so that we can populate listView with the data from database
            getLoaderManager().initLoader(1,null,this);

        }

        //Find view by their id's
        productName = findViewById(R.id.editTextProductName);
        productTP = findViewById(R.id.editTextProductTP);
        productQuantity = findViewById(R.id.editTextProductQuantity);
        productDiscount = findViewById(R.id.editTextProductDiscount);
        productFinalRate = findViewById(R.id.textViewFinalRate);

        // Set on click listener on productTP and Discount
        // So as they change productFinalRate should change
        productTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Get the text in productTP field & productDiscount field
                String tp = productTP.getText().toString();
                String discount = productDiscount.getText().toString();

                //If there is no discount set (text in productTp) as text in  fina rate
                if(TextUtils.isEmpty(productDiscount.getText())){
                    productFinalRate.setText(tp);
                }
                //if Discount has some value perform mathematics and show result in productFinalRate
                else if(!TextUtils.isEmpty(tp)) {
                    Double finalRate = Double.parseDouble(tp) - (Double.parseDouble(tp) * Double.parseDouble(discount) / 100);
                    //Round of the result in 2 decimal places
                    String result = String.format("%.2f", finalRate);
                    productFinalRate.setText(result.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        productDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // Get the text in productTP field & productDiscount field
                String tp = productTP.getText().toString();
                String discount = productDiscount.getText().toString();

                //If there is no discount set (text in productTp) as text in  fina rate
                if(TextUtils.isEmpty(productDiscount.getText())){
                    productFinalRate.setText(tp);
                }
                //if Discount has some value perform mathematics and show result in productFinalRate
                else if(!TextUtils.isEmpty(tp)){
                    Double finalRate = Double.parseDouble(tp) - (Double.parseDouble(tp) * Double.parseDouble(discount) / 100);
                    //Round of the result in 2 decimal places
                    String result = String.format("%.2f", finalRate);
                    productFinalRate.setText(result.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        isFieldTouched = false;
        // See if any EditText field is touched or not
        // And if any field is touched we return true showing that there may have been changes
        // Set on touch listener on all editText fields

        productName.setOnTouchListener(mTouchListener);
        productTP.setOnTouchListener(mTouchListener);
        productQuantity.setOnTouchListener(mTouchListener);
        productDiscount.setOnTouchListener(mTouchListener);

    }

    //To display the menu items in app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        // If we are inserting a product we should hide the delete button
        if(currentProductUri == null){
            MenuItem deleteButtton = menu.findItem(R.id.action_delete);
            deleteButtton.setVisible(false);
            this.invalidateOptionsMenu();
        }
        return true;
    }

    //Perform some action when menu items are pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_save:
                saveProduct();
                return true;

            case  R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            default:
                return true;
        }
    }

    private void deleteProduct() {

        if(currentProductUri != null){

            //Delete the pet
            // returning the number of rows deleted
            int rowsDeleted = getContentResolver().delete(currentProductUri,null,null);

            if(rowsDeleted==0){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Product Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save the product in database
    private void saveProduct() {

        //Get the details of product from text fields
        String nameStr = productName.getText().toString().trim();
        String tpStr = productTP.getText().toString().trim();
        String quantityStr = productQuantity.getText().toString().trim();
        String discountStr = productDiscount.getText().toString().trim();
        String finalRateStr = productFinalRate.getText().toString();

        if(areFieldsFilled()){
            // Convert the Strings into their respective format to be stored in database
            Double tp = Double.parseDouble(tpStr);
            Integer quantity = Integer.parseInt(quantityStr);
            Double finalRate = Double.parseDouble(finalRateStr);
            Double discount = 0.0;

            //If Discount field has some String value it will be converted to equivalent Double value else it is 0.0
            if(!TextUtils.isEmpty(discountStr)){
                discount = Double.parseDouble(discountStr);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, nameStr);
            contentValues.put(ProductEntry.COLUMN_PRODUCT_TP, tp);
            contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            contentValues.put(ProductEntry.COLUMN_PRODUCT_DISCOUNT, discount);
            contentValues.put(ProductEntry.COLUMN_PRODUCT_FINAL_RATE, finalRate);

            if(currentProductUri==null){
                // This is a NEW pet, so insert a new pet into the provider,
                // returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI,contentValues);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, "Error",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, "Product Saved",
                            Toast.LENGTH_SHORT).show();
                }
                // finish the activity and return back to view product activity
                finish();
            }
            else{
                //This is an existing product, so we update it
                // returning the number of rows affected
                int rowsAffected = getContentResolver().update(currentProductUri,contentValues,null,null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this,"Error",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, "Saved Successfully",
                            Toast.LENGTH_SHORT).show();
                }
                // finish the activity and return back to view product activity
                finish();
            }
        }


    }

    //This function check if all required fields are filled of not
    private boolean areFieldsFilled(){

        boolean isOkToSave = false;

        //Get the details of product from text fields
        String nameStr = productName.getText().toString().trim();
        String tpStr = productTP.getText().toString().trim();
        String quantityStr = productQuantity.getText().toString().trim();

        //Check If Any field is empty, if so Toast a message to Warn the user
        if(TextUtils.isEmpty(quantityStr)){
            Toast.makeText(this, "Product require a Quantity", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(tpStr)){
            Toast.makeText(this, "Product require a TP", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(nameStr)){
            Toast.makeText(this, "Product require a Name", Toast.LENGTH_SHORT).show();
        }

        boolean isNameFilled = !TextUtils.isEmpty(nameStr);
        boolean isTPFilled = !TextUtils.isEmpty(tpStr);
        boolean isQuantityFilled = !TextUtils.isEmpty(quantityStr);


        if(isNameFilled && isTPFilled && isQuantityFilled){
            isOkToSave = true;
        }

        return isOkToSave;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Define a projection that specifies the columns from the table we care about.
        String [] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_TP,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_DISCOUNT,
                ProductEntry.COLUMN_PRODUCT_FINAL_RATE
        };
        // This loader will execute the ContentProvider's query method on a background thread
     return new CursorLoader(
             this,
             currentProductUri,
             projection,
             null,
             null,
             null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        cursor.moveToFirst();

        // Get the column indexes
        int nameIndex       = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int tpIndex         = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TP);
        int quantityIndex   = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int discountIndex   = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DISCOUNT);
        int finalRateIndex  = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_FINAL_RATE);

        // Get the data of current product from cursor
        String name     = cursor.getString(nameIndex);
        String tp       = String.valueOf(cursor.getDouble(tpIndex));
        String quantity = String.valueOf(cursor.getInt(quantityIndex));
        String discount = String.valueOf(cursor.getDouble(discountIndex));
        String finalRate= String.valueOf(cursor.getDouble(finalRateIndex));

        // Now fill te text views with these string
        productName.setText(name);
        productTP.setText(tp);
        productQuantity.setText(quantity);
        productDiscount.setText(discount);
        productFinalRate.setText(finalRate);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        // Empty all fields on reset
        productName.setText(null);
        productTP.setText(null);
        productQuantity.setText(null);
        productDiscount.setText(null);
        productFinalRate.setText(null);

    }

    //Dialog box to display a dialog confirming the delete
    private void showDeleteConfirmationDialog(){

        //First build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete product?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete the product when DELETE button is clicked
                deleteProduct();

                // finish the activity and return back to view product activity
                finish();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //Show the dialog box
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Dialog box to display a dialog confirming whether to save or discard
    // while inserting or updating product
    private void showUnsavedDialogBox(){

        //First build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save your changes or discard them?");
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete the product when DELETE button is clicked
                saveProduct();

                // finish the activity and return back to view product activity
                finish();
            }
        });

        builder.setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // finish the activity and return back to view product activity
                finish();
            }
        });

        //Show the dialog box
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        //If there have been changes in Edit field(s) than we ask to either SAVE or DISCARD
        if(isFieldTouched){
            showUnsavedDialogBox();
        }
        else {
            super.onBackPressed();
        }
    }

}
