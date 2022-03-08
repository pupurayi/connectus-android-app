package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class BalanceContract {
    public static class BalanceEntry implements BaseColumns {
        private static final String TABLE_NAME = "profile_balance";
        private static final String BALANCE_ID = "balanceId";
        private static final String CURRENCY = "currency";
        private static final String AMOUNT = "amount";
        private static final String CREATED_AT = "createdAt";
        private static final String UPDATED_AT = "updatedAt";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getBalanceId() {
            return BALANCE_ID;
        }

        public static String getCURRENCY() {
            return CURRENCY;
        }

        public static String getAMOUNT() {
            return AMOUNT;
        }

        public static String getCreatedAt() {
            return CREATED_AT;
        }

        public static String getUpdatedAt() {
            return UPDATED_AT;
        }
    }

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + BalanceEntry.TABLE_NAME + " ( " +
            BalanceEntry.BALANCE_ID + " INTEGER PRIMARY KEY NOT NULL," +
            BalanceEntry.CURRENCY + " TEXT UNIQUE NOT NULL," +
            BalanceEntry.AMOUNT + " REAL NOT NULL," +
            BalanceEntry.CREATED_AT + " DATE, " +
            BalanceEntry.UPDATED_AT + " DATE)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + BalanceEntry.TABLE_NAME;
}
