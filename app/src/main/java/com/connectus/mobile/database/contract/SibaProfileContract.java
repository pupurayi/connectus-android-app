package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class SibaProfileContract {
    public static class SibaProfileEntry implements BaseColumns {
        private static final String TABLE_NAME = "siba_profile";
        private static final String SIBA_PROFILE_ID = "siba_profile_id";
        private static final String SUBJECT = "subject";
        private static final String ADMIN_PROFILE_ID = "admin_profile_id";
        private static final String CURRENCY = "currency";
        private static final String BALANCE = "balance";
        private static final String STATUS = "status";
        private static final String MEMBERS = "members";
        private static final String INVITES = "invites";
        private static final String PLANS = "plans";
        private static final String CREATED_AT = "createdAt";
        private static final String UPDATED_AT = "updatedAt";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getSibaProfileId() {
            return SIBA_PROFILE_ID;
        }

        public static String getSUBJECT() {
            return SUBJECT;
        }

        public static String getAdminProfileId() {
            return ADMIN_PROFILE_ID;
        }

        public static String getCURRENCY() {
            return CURRENCY;
        }

        public static String getBALANCE() {
            return BALANCE;
        }

        public static String getSTATUS() {
            return STATUS;
        }

        public static String getMEMBERS() {
            return MEMBERS;
        }

        public static String getINVITES() {
            return INVITES;
        }

        public static String getPLANS() {
            return PLANS;
        }

        public static String getCreatedAt() {
            return CREATED_AT;
        }

        public static String getUpdatedAt() {
            return UPDATED_AT;
        }
    }

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + SibaProfileEntry.TABLE_NAME + " ( " +
            SibaProfileEntry.SIBA_PROFILE_ID + " TEXT PRIMARY KEY NOT NULL," +
            SibaProfileEntry.SUBJECT + " TEXT NOT NULL," +
            SibaProfileEntry.ADMIN_PROFILE_ID + " TEXT NOT NULL," +
            SibaProfileEntry.CURRENCY + " TEXT NOT NULL," +
            SibaProfileEntry.BALANCE + " REAL NOT NULL," +
            SibaProfileEntry.STATUS + " TEXT NOT NULL," +
            SibaProfileEntry.MEMBERS + " BLOB," +
            SibaProfileEntry.INVITES + " BLOB," +
            SibaProfileEntry.PLANS + " BLOB," +
            SibaProfileEntry.CREATED_AT + " DATE, " +
            SibaProfileEntry.UPDATED_AT + " DATE)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + SibaProfileEntry.TABLE_NAME;
}

