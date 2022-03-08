package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class NotificationContract {
    public static class NotificationEntry implements BaseColumns {
        private static final String TABLE_NAME = "notification";
        private static final String NOTIFICATION_ID = _ID;
        private static final String TITLE = "title";
        private static final String MESSAGE = "message";
        private static final String SENT_TIME = "sentTime";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getNotificationId() {
            return NOTIFICATION_ID;
        }

        public static String getTITLE() {
            return TITLE;
        }

        public static String getMESSAGE() {
            return MESSAGE;
        }

        public static String getSentTime() {
            return SENT_TIME;
        }
    }

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + NotificationEntry.TABLE_NAME + " ( " +
            NotificationEntry.NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            NotificationEntry.TITLE + " TEXT NOT NULL," +
            NotificationEntry.MESSAGE + " TEXT NOT NULL," +
            NotificationEntry.SENT_TIME + " INTEGER)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME;
}
