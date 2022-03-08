package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class TransactionContract {
    public static class TransactionEntry implements BaseColumns {
        private static final String TABLE_NAME = "txn";
        private static final String TRANSACTION_ID = "transactionId";
        private static final String REFERENCE = "reference";
        private static final String TYPE = "type";
        private static final String SUCCESS = "success";
        private static final String CURRENCY = "currency";
        private static final String DEBIT = "debit";
        private static final String CREDIT = "credit";
        private static final String CHARGE = "charge";
        private static final String NOTE = "note";
        private static final String CREATED_AT = "createdAt";
        private static final String UPDATED_AT = "updatedAt";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getTransactionId() {
            return TRANSACTION_ID;
        }

        public static String getREFERENCE() {
            return REFERENCE;
        }

        public static String getTYPE() {
            return TYPE;
        }

        public static String getSUCCESS() {
            return SUCCESS;
        }

        public static String getCURRENCY() {
            return CURRENCY;
        }

        public static String getDEBIT() {
            return DEBIT;
        }

        public static String getCREDIT() {
            return CREDIT;
        }

        public static String getCHARGE() {
            return CHARGE;
        }

        public static String getNOTE() {
            return NOTE;
        }

        public static String getCreatedAt() {
            return CREATED_AT;
        }

        public static String getUpdatedAt() {
            return UPDATED_AT;
        }
    }

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + TransactionContract.TransactionEntry.TABLE_NAME + " ( " +
            TransactionEntry.TRANSACTION_ID + " INTEGER PRIMARY KEY NOT NULL," +
            TransactionContract.TransactionEntry.REFERENCE + " TEXT," +
            TransactionContract.TransactionEntry.TYPE + " TEXT," +
            TransactionContract.TransactionEntry.SUCCESS + " INTEGER, " +
            TransactionContract.TransactionEntry.CURRENCY + " TEXT," +
            TransactionContract.TransactionEntry.DEBIT + " REAL," +
            TransactionContract.TransactionEntry.CREDIT + " REAL," +
            TransactionContract.TransactionEntry.CHARGE + " REAL, " +
            TransactionContract.TransactionEntry.NOTE + " TEXT," +
            TransactionContract.TransactionEntry.UPDATED_AT + " DATE, " +
            TransactionContract.TransactionEntry.CREATED_AT + " DATE)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TransactionContract.TransactionEntry.TABLE_NAME;
}
