package com.example.android.products;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.content.Intent;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.products.data.ProductContract.ProductEntry;

import java.io.FileDescriptor;
import java.io.IOException;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int SEND_MAIL_REQUEST = 1;

    private ContentResolver mContentResolver;

    private static final int PRODUCTS_CURSOR = 0;

    private Bitmap mBitmap;
    private String mProductName;
    private int mQuantity;
    private double mProductPrice;
    private Uri mImageUri;
    private int mSold;
    private String mSupplier;

    private boolean mProductHasChanged = false;

    private Uri mCurrentProductUri;
    private TextView mNameTextView;
    private TextView mQuantityTextView;
    private TextView mPriceTextView;
    private ImageView mImageView;
    private TextView mOrderTextView;
    private TextView mSoldTextView;
    private TextView mSupplierTextView;
    private Button mAddOrderButton;
    private Button mAddSellButton;
    private Button mOrderButton;
    private Button mSellButton;
    private ViewTreeObserver mViewTree;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mContentResolver = getContentResolver();
        mCurrentProductUri = getIntent().getData();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.edit_product_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
            }
        });

        mNameTextView = (TextView) findViewById(R.id.product_name_text_view);
        mQuantityTextView = (TextView) findViewById(R.id.current_quantity_text_view);
        mPriceTextView = (TextView) findViewById(R.id.product_price_text_view);
        mImageView = (ImageView) findViewById(R.id.detail_image_view);
        mSupplierTextView = (TextView) findViewById(R.id.supplier_text_view);
        mOrderTextView = (TextView) findViewById(R.id.order_text_view);
        mSoldTextView = (TextView) findViewById(R.id.sell_text_view);
        mOrderButton = (Button) findViewById(R.id.detail_activity_order_button);
        mAddOrderButton = (Button) findViewById(R.id.order_increment_button);
        mSellButton = (Button) findViewById(R.id.detail_activity_sell_button);
        mAddSellButton = (Button) findViewById(R.id.sell_increment_button);

        mQuantityTextView.setOnTouchListener(mTouchListener);

        mCurrentProductUri = getIntent().getData();

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        getLoaderManager().initLoader(PRODUCTS_CURSOR, null, this);
    }

    private void sendEmail() {
        if (mImageUri != null) {
            String subject = "Order";
            String stream = "Please Send 10 " + mProductName + "\n"
                    + "Product image: \n";

            Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                    .setStream(mImageUri)
                    .setSubject(subject)
                    .setText(stream)
                    .getIntent();

            shareIntent.setData(mImageUri);
            shareIntent.setType("message/rfc822");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (Build.VERSION.SDK_INT < 21) {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            } else {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }


            startActivityForResult(Intent.createChooser(shareIntent, "Share with"), SEND_MAIL_REQUEST);

        } else {
            String subject = "Order ";
            String stream = "Please Send 10 " + mProductName + "\n";

            Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                    .setSubject(subject)
                    .setText(stream)
                    .getIntent();

            shareIntent.setData(mImageUri);
            shareIntent.setType("message/rfc822");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (Build.VERSION.SDK_INT < 21) {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            } else {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }


            startActivityForResult(Intent.createChooser(shareIntent, "Share with"), SEND_MAIL_REQUEST);

        }
    }

    private Bitmap getBitmapFromUri(ImageView mImageView, Uri uri, Context context) {
        if (uri == null) {
            Log.v(LOG_TAG, "Uri from getBitmapFromUri class method is null, double CHECK");
            return null;
        }
        /**
         * Have to divide the imageView with image but using the width and height of both
         */

        int targetImageWidth = mImageView.getWidth();
        int targetImageHeight = mImageView.getHeight();
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            /** parcelFileDescriptor openFile method String mode is gonna be (only read access here "r")
             * parcel -> file with description which was inside parcel ->
             * ...**/
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            //put to null while decoding -> avoid memory allocation
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            int optionsW = options.outWidth; //resulting width
            int optionsH = options.outHeight; //resulting height

            //get minimum for scaling and change inJustDecodeBounds = false;
            int scaling = Math.min(optionsW / targetImageWidth, optionsH / targetImageHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaling;
            options.inPurgeable = true; //for lollipop and below
            /** After scaling we can decode it from file descriptor again **/
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            /** Create Bitmap (Rotation fixes) **/
            if (image.getWidth() > image.getHeight()) {
                //if width more than height
                Matrix mat = new Matrix();
                int degreesOfRotation = 90;
                mat.postRotate(degreesOfRotation);
                Bitmap imageRotate_1 = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mat, true);
                return imageRotate_1;
            } else {
                return image;
            }

        } catch (Exception e) {
            Log.v(LOG_TAG, "Could not load image file. ", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, "Couldn't close Parcel File Descriptor.");
            }
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveProduct() {
        String nameString = mNameTextView.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String priceString = mPriceTextView.getText().toString().trim();
        String supplierString = mSupplierTextView.getText().toString().trim();
        String imageString;

        if (mImageUri != null) {
            imageString = mImageUri.toString();
        } else {
            imageString = "";
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageString);

        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful), Toast.LENGTH_SHORT).show();
            }
        }else {

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteProduct() {

        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_SOLD,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER};

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
            int soldColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);

            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            final String image = cursor.getString(imageColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int sold = cursor.getInt(soldColumnIndex);

            mNameTextView.setText(name);
            mQuantityTextView.setText(String.valueOf(quantity));
            mPriceTextView.setText(String.valueOf(price));
            mSoldTextView.setText(String.valueOf(sold));
            mSupplierTextView.setText(supplier);

            if (!image.isEmpty()) {
                mImageUri = Uri.parse(image);
                mBitmap = getBitmapFromUri(mImageView, mImageUri, DetailActivity.this);
                mImageView.setImageBitmap(mBitmap);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameTextView.clearComposingText();
        mQuantityTextView.clearComposingText();
        mPriceTextView.clearComposingText();
    }
}
