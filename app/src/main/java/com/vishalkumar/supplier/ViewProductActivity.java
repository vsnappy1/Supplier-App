package com.vishalkumar.supplier;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import com.vishalkumar.supplier.data.SupplierContract.ProductEntry;

import java.util.ArrayList;

public class ViewProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ProductCursorAdapter mCursorAdapter;

    // To Show that list is empty by an pircture and some text or just text
    View emptyView;
    TextView noResultFound;

    boolean isDataBaseEmpty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        //Find View by their id's
        final ListView productListView = findViewById(R.id.listViewProduct);
        SearchView productSearch = findViewById(R.id.searchViewProduct);
        FloatingActionButton addProductFAB = findViewById(R.id.FABAddProduct);

        //If there is no data to display, show the empty view
        noResultFound = findViewById(R.id.textViewNoResultFound);
        emptyView = findViewById(R.id.emptyView);

        // Initially we consider that database is not empty
        isDataBaseEmpty = false;

        //get the system service for inputMethodManager
      //  inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //Now hide the keyboard
       // inputMethodManager.hideSoftInputFromWindow(searchProductEditText.getWindowToken(),0);

        // Set On Click Listener on 'addProductFAB'
        addProductFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the Editor Activity to add new product
                Intent editorActivity = new Intent(ViewProductActivity.this, EditorActivity.class);
                startActivity(editorActivity);
            }
        });

        // Setup an Adapter to create a list item for each row of product data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ProductCursorAdapter(this,null);
        productListView.setAdapter(mCursorAdapter);

        //Initialize Loader, so that we can populate listView with the data from database
        getLoaderManager().initLoader(1,null,this);


        // Set OnClickListener on listView so that we can update or delete the selected item
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                //Open an intent to editor activity where we can uppdate the info of product
                Intent editorActivity = new Intent(ViewProductActivity.this, EditorActivity.class);

                //here 'id' perameter is the id of product
                // Construct the uri to reach this particular product
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                //Send this Uri as data with intent so that at editor activity we may know what item clicked
                editorActivity.setData(currentProductUri);

                // Start the activity
                startActivity(editorActivity);
            }
        });

        // If database has nothing in it we show a message of empty database with a picture
        productListView.setEmptyView(emptyView);


        //Set on text change listener on search view in order to filter the products
        productSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {


                productListView.setEmptyView(null);

                // Define a projection that specifies the columns from the table we care about.
                String [] projection = {
                        ProductEntry._ID,
                        ProductEntry.COLUMN_PRODUCT_NAME,
                        ProductEntry.COLUMN_PRODUCT_TP,
                        ProductEntry.COLUMN_PRODUCT_QUANTITY,
                        ProductEntry.COLUMN_PRODUCT_DISCOUNT,
                        ProductEntry.COLUMN_PRODUCT_FINAL_RATE
                };

                String selection = ProductEntry.COLUMN_PRODUCT_NAME + " LIKE '"+s.trim()+"%'";
                Cursor productCursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection,selection,null,ProductEntry.COLUMN_PRODUCT_NAME);
                mCursorAdapter.swapCursor(productCursor);
                if(productCursor.getCount() == 0){
                    noResultFound.setAlpha(1);
                }
                else {
                    noResultFound.setAlpha(0);
                }
                return false;
            }
        });
    }

    // Setup menu to this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product,menu);
        return true;
    }

    //Set on menu item pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.insertDemoData:
                insertDemoData();
                break;
        }

        return true;
    }

    //Insert demo data in database for exploring the app
    private void insertDemoData(){

        // Demo Data (for learning how to application works)
        ArrayList<Product> productList = new ArrayList<Product>();
        productList.add(new Product("Panadol",200.0,500,5.0));
        productList.add(new Product("Augmental",150.0,400,15.0));
        productList.add(new Product("Calpol",50.0,300,7.0));
        productList.add(new Product("Multical",97.0,200,0.0));
        productList.add(new Product("Novidate",170.0,100,13.0));
        productList.add(new Product("Ponistan",600.0,50,5.0));
        productList.add(new Product("Alp",76.0,200,12.5));
        productList.add(new Product("Risk",500.0,300,8.0));
        productList.add(new Product("Brofin",63.7,500,9.0));
        productList.add(new Product("Detol",56.8,200,7.5));

        ContentValues contentValues = new ContentValues();


        // Fill the database with demo Data
        for(int i = 0 ; i < productList.size() ; i++)
        {
           //Get the product from array list one by one
            Product currentProduct = productList.get(i);

            //Store the values of current product in content value
           contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, currentProduct.getName());
           contentValues.put(ProductEntry.COLUMN_PRODUCT_TP, currentProduct.getTP());
           contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, currentProduct.getQuantity());
           contentValues.put(ProductEntry.COLUMN_PRODUCT_DISCOUNT, currentProduct.getDiscount());
           contentValues.put(ProductEntry.COLUMN_PRODUCT_FINAL_RATE, currentProduct.getFinalRate());

           // Insert the data in database
           getContentResolver().insert(ProductEntry.CONTENT_URI,contentValues);

           // Clear content values
           contentValues.clear();
       }

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
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                ProductEntry.COLUMN_PRODUCT_NAME); //For ordering the product in ascending order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //is called when we want all data in adapter to be deleted
        mCursorAdapter.swapCursor(null);
    }

}
