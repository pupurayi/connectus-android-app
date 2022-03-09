package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class OfferingContract {
    public static class OfferingEntry implements BaseColumns {
        private static final String TABLE_NAME = "offering";
        private static final String OFFERING_ID = "id";
        private static final String USER_ID = "userId";
        private static final String NAME = "name";
        private static final String DESCRIPTION = "description";
        private static final String RATING = "rating";
        private static final String CREATED = "created";
        private static final String UPDATED = "updated";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getOfferingId() {
            return OFFERING_ID;
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

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + OfferingContract.OfferingEntry.TABLE_NAME + " ( " +
            OfferingEntry.OFFERING_ID + " TEXT PRIMARY KEY NOT NULL," +
            OfferingEntry.USER_ID + " TEXT," +
            OfferingEntry.NAME + " TEXT," +
            OfferingEntry.DESCRIPTION + " TEXT," +
            OfferingEntry.RATING + " INTEGER," +
            OfferingEntry.UPDATED + " TEXT, " +
            OfferingEntry.CREATED + " TEXT)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + OfferingContract.OfferingEntry.TABLE_NAME;
}
