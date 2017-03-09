package com.example.android.products;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.products.data.ProductContract.ProductEntry;


public class ProductCursorAdaptor extends CursorAdapter {
    private Context mContexts;


    public ProductCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0);
        mContexts = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        view.setTag(productViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ProductViewHolder productViewHolder = (ProductViewHolder) view.getTag();

        final String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final double priceValue = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        final int productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

        final Cursor cursorValue = cursor;
        String quantityString = "In Stock: " + quantity;
        String priceString = "Price $" + priceValue;
        final Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);

        productViewHolder.mNameTextView.setText(productName);
        productViewHolder.mQuantityTextView.setText(quantityString);
        productViewHolder.mPriceTextView.setText(priceString);

        productViewHolder.mSellOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues newValues = new ContentValues();
                if (quantity > 1) {
                    int quantity = cursorValue.getInt(cursorValue.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
                    int sold = cursorValue.getInt(cursorValue.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD));
                    int quantityValue = quantity;
                    int soldValue = sold;
                    newValues.put(ProductEntry.COLUMN_PRODUCT_SOLD, ++soldValue);
                    newValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --quantityValue);
                    resolver.update(uri, newValues, null, null);
                    mContexts.getContentResolver().notifyChange(uri, null);

                    // TODO replace hardcoded toast messages with values in strings.xml
                    Toast.makeText(context, "Sold 1 " + productName, Toast.LENGTH_SHORT).show();

                } else if (quantity == 1){
                    int quantity = cursorValue.getInt(cursorValue.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
                    int sold = cursorValue.getInt(cursorValue.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD));
                    int quantityValue = quantity;
                    int soldValue = sold;
                    newValues.put(ProductEntry.COLUMN_PRODUCT_SOLD, ++soldValue);
                    newValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --quantityValue);
                    resolver.update(uri, newValues, null, null);
                    mContexts.getContentResolver().notifyChange(uri, null);

                    Toast.makeText(context, "Order " + productName, Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private class ProductViewHolder {
        TextView mNameTextView;
        TextView mQuantityTextView;
        TextView mPriceTextView;
        Button mSellOneButton;

        ProductViewHolder(View view) {

            mNameTextView = (TextView) view.findViewById(R.id.product_name_list_view_text_view);
            mQuantityTextView = (TextView) view.findViewById(R.id.quantity_list_view_text_view);
            mPriceTextView = (TextView) view.findViewById(R.id.price_list_view_text_view);
            mSellOneButton = (Button) view.findViewById(R.id.list_view_sell_button);

        }
    }
}

