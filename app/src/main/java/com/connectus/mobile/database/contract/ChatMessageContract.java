package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class ChatMessageContract {

    public static class ChatMessageEntry implements BaseColumns {
        private static final String TABLE_NAME = "chat_message";
        private static final String MESSAGE_ID = "message_id";
        private static final String SIBA_PROFILE_ID = "siba_profile_id";
        private static final String SENDER_PROFILE_ID = "sender_profile_id";
        private static final String MESSAGE = "message";
        private static final String CREATED_AT = "createdAt";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getMessageId() {
            return MESSAGE_ID;
        }

        public static String getSibaProfileId() {
            return SIBA_PROFILE_ID;
        }

        public static String getSenderProfileId() {
            return SENDER_PROFILE_ID;
        }

        public static String getMESSAGE() {
            return MESSAGE;
        }

        public static String getCreatedAt() {
            return CREATED_AT;
        }
    }

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + ChatMessageEntry.TABLE_NAME + " ( " +
            ChatMessageEntry.MESSAGE_ID + " TEXT PRIMARY KEY NOT NULL," +
            ChatMessageEntry.SIBA_PROFILE_ID + " TEXT NOT NULL," +
            ChatMessageEntry.SENDER_PROFILE_ID + " TEXT NOT NULL," +
            ChatMessageEntry.MESSAGE + " TEXT, " +
            ChatMessageEntry.CREATED_AT + " REAL)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + ChatMessageEntry.TABLE_NAME;
}
