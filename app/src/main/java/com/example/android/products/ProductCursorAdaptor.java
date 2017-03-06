package com.example.android.products;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.products.data.ProductContract.ProductEntry;
import com.example.android.products.R;

public class ProductCursorAdaptor extends CursorAdapter {

    public static final String LOG_TAG = ProductCursorAdaptor.class.getSimpleName();
    private Context mContexts;


    public ProductCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0 );
        mContexts = context;
    }

    public static class ProductViewHolder {
        public final TextView mNameTextView;
        public final TextView mQuantityTextView;
        public final TextView mPriceTextView;
        public final Button mSellOneButtons;

        public ProductViewHolder(View view) {
            mNameTextView = (TextView) view.findViewById(R.id.product_name_list_view_text_view);
            mQuantityTextView = (TextView) view.findViewById(R.id.quantity_list_view_text_view);
            mPriceTextView = (TextView) view.findViewById(R.id.price_list_view_text_view);
            mSellOneButtons = (Button) view.findViewById(R.id.list_view_sell_button);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        view.setTag(productViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ProductViewHolder productViewHolder = (ProductViewHolder) view.getTag();

        final String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final double priceValue = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        final int productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

        final Cursor cursorValue = cursor;
        String quantityString = "In Stock: " + quantity;
        String priceString = "Price $" + priceValue;
        final Uri uri = ProductEntry.CONTENT_URI;

        productViewHolder.mNameTextView.setText(productName);
        productViewHolder.mQuantityTextView.setText(quantityString);
        productViewHolder.mPriceTextView.setText(priceString);

        productViewHolder.mSellOneButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Quantity " + quantity);
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues newValues = new ContentValues();
                if (quantity > 0) {
                    int quantity = cursorValue.getInt(cursorValue.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
                    int sold = cursorValue.getInt(cursorValue.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD));
                    int quantityValue = quantity;
                    int soldValue = sold;
                    newValues.put(ProductEntry.COLUMN_PRODUCT_SOLD, ++soldValue);
                    newValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --quantityValue);
                    resolver.update(uri, newValues, null, null);
                    mContexts.getContentResolver().notifyChange(uri, null);
                }
            }
        });




    }
}
