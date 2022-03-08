package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class SupportMessageContract {

    public static class SupportMessageEntry implements BaseColumns {
        private static final String TABLE_NAME = "support_message";
        private static final String MESSAGE_ID = "message_id";
        private static final String SENDER_USER_ID = "sender_profile_id";
        private static final String MESSAGE = "message";
        private static final String CREATED_AT = "createdAt";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getMessageId() {
            return MESSAGE_ID;
        }

        public static String getSenderUserId() {
            return SENDER_USER_ID;
        }

        public static String getMESSAGE() {
            return MESSAGE;
        }

        public static String getCreatedAt() {
            return CREATED_AT;
        }
    }

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + SupportMessageEntry.TABLE_NAME + " ( " +
            SupportMessageEntry.MESSAGE_ID + " TEXT PRIMARY KEY NOT NULL," +
            SupportMessageEntry.SENDER_USER_ID + " TEXT NOT NULL," +
            SupportMessageEntry.MESSAGE + " TEXT, " +
            SupportMessageEntry.CREATED_AT + " REAL)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + SupportMessageEntry.TABLE_NAME;
}
