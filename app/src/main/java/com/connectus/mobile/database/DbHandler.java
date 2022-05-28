package com.connectus.mobile.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.connectus.mobile.database.contract.ProductContract;
import com.connectus.mobile.ui.product.ProductDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DbHandler extends SQLiteOpenHelper {
    private static final String TAG = DbHandler.class.getSimpleName();
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static final String DATABASE_NAME = "connectus.db";
    // always update database version
    private static final int DATABASE_VERSION = 12;

    public DbHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ProductContract.SQL_DROP_TABLE);
        onCreate(db);
    }

    public void insertProduct(ProductDto productDto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(ProductContract.ProductEntry.getProductId(), productDto.getId().toString());
        cValues.put(ProductContract.ProductEntry.getUserId(), productDto.getUserId().toString());
        cValues.put(ProductContract.ProductEntry.getCATEGORY(), productDto.getCategory());
        cValues.put(ProductContract.ProductEntry.getNAME(), productDto.getName());
        cValues.put(ProductContract.ProductEntry.getDESCRIPTION(), productDto.getDescription());
        cValues.put(ProductContract.ProductEntry.getPRICE(), productDto.getPrice());
        cValues.put(ProductContract.ProductEntry.getImageFirst(), productDto.getImageFirst());
        cValues.put(ProductContract.ProductEntry.getLAT(), productDto.getLat());
        cValues.put(ProductContract.ProductEntry.getLNG(), productDto.getLng());
        cValues.put(ProductContract.ProductEntry.getRATING(), productDto.getRating());
        cValues.put(ProductContract.ProductEntry.getCREATED(), productDto.getCreated());
        cValues.put(ProductContract.ProductEntry.getUPDATED(), productDto.getUpdated());
        long newRowId = db.replace(ProductContract.ProductEntry.getTableName(), null, cValues);
        db.close();
    }

    public List<ProductDto> getProducts() {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = new String[]{
                ProductContract.ProductEntry.getProductId(),
                ProductContract.ProductEntry.getUserId(),
                ProductContract.ProductEntry.getCATEGORY(),
                ProductContract.ProductEntry.getNAME(),
                ProductContract.ProductEntry.getDESCRIPTION(),
                ProductContract.ProductEntry.getPRICE(),
                ProductContract.ProductEntry.getImageFirst(),
                ProductContract.ProductEntry.getLAT(),
                ProductContract.ProductEntry.getLNG(),
                ProductContract.ProductEntry.getRATING(),
                ProductContract.ProductEntry.getCREATED(),
                ProductContract.ProductEntry.getUPDATED()
        };
        Cursor cursor = db.query(ProductContract.ProductEntry.getTableName(), columns, null, null, null, null, null);

        int productPosId = cursor.getColumnIndex(ProductContract.ProductEntry.getProductId());
        int userIdPos = cursor.getColumnIndex(ProductContract.ProductEntry.getUserId());
        int categoryPos = cursor.getColumnIndex(ProductContract.ProductEntry.getCATEGORY());
        int namePos = cursor.getColumnIndex(ProductContract.ProductEntry.getNAME());
        int descriptionPos = cursor.getColumnIndex(ProductContract.ProductEntry.getDESCRIPTION());
        int pricePos = cursor.getColumnIndex(ProductContract.ProductEntry.getPRICE());
        int imageFirstPos = cursor.getColumnIndex(ProductContract.ProductEntry.getImageFirst());
        int latPos = cursor.getColumnIndex(ProductContract.ProductEntry.getLAT());
        int lngPos = cursor.getColumnIndex(ProductContract.ProductEntry.getLNG());
        int ratingPos = cursor.getColumnIndex(ProductContract.ProductEntry.getRATING());
        int createdPos = cursor.getColumnIndex(ProductContract.ProductEntry.getCREATED());
        int updatedPos = cursor.getColumnIndex(ProductContract.ProductEntry.getUPDATED());

        List<ProductDto> products = new LinkedList<>();
        while (cursor.moveToNext()) {
            UUID productId = UUID.fromString(cursor.getString(productPosId));
            UUID userId = null;
            if (cursor.getString(userIdPos) != null) {
                userId = UUID.fromString(cursor.getString(userIdPos));
            }
            String category = cursor.getString(categoryPos);
            String name = cursor.getString(namePos);
            String description = cursor.getString(descriptionPos);
            double price = cursor.getDouble(pricePos);
            String imageFirst = cursor.getString(imageFirstPos);
            double lat = cursor.getDouble(latPos);
            double lng = cursor.getDouble(lngPos);
            int rating = cursor.getInt(ratingPos);
            String created = cursor.getString(createdPos);
            String updated = cursor.getString(updatedPos);
            products.add(new ProductDto(productId, userId, category, name, description, price, imageFirst, lat, lng, rating, created, updated));
        }
        cursor.close();
        db.close();
        return products;
    }

    public void deleteAllProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ProductContract.ProductEntry.getTableName(), null, null);
        db.close();
    }
}
