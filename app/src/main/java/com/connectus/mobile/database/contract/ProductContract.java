package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class ProductContract {
    public static class ProductEntry implements BaseColumns {
        private static final String TABLE_NAME = "products";
        private static final String PRODUCT_ID = "id";
        private static final String USER_ID = "userId";
        private static final String NAME = "name";
        private static final String DESCRIPTION = "description";
        private static final String RATING = "rating";
        private static final String CREATED = "created";
        private static final String UPDATED = "updated";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getProductId() {
            return PRODUCT_ID;
        }

        public static String getUserId() {
            return USER_ID;
        }

        public static String getNAME() {
            return NAME;
        }

        public static String getDESCRIPTION() {
            return DESCRIPTION;
        }

        public static String getRATING() {
            return RATING;
        }

        public static String getCREATED() {
            return CREATED;
        }

        public static String getUPDATED() {
            return UPDATED;
        }
    }

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ( " +
            ProductEntry.PRODUCT_ID + " TEXT PRIMARY KEY NOT NULL," +
            ProductEntry.USER_ID + " TEXT," +
            ProductEntry.NAME + " TEXT," +
            ProductEntry.DESCRIPTION + " TEXT," +
            ProductEntry.RATING + " INTEGER," +
            ProductEntry.UPDATED + " TEXT, " +
            ProductEntry.CREATED + " TEXT)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;
}
