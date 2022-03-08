package com.connectus.mobile.database.contract;

import android.provider.BaseColumns;

public class MySibaInviteContract {
    public static class MySibaInviteEntry implements BaseColumns {
        private static final String TABLE_NAME = "my_siba_invite";
        private static final String INVITE = "invite";
        private static final String SUBJECT = "subject";
        private static final String CREATED_BY = "createdBy";
        private static final String CREATED_AT = "createdAt";

        public static String getTableName() {
            return TABLE_NAME;
        }

        public static String getINVITE() {
            return INVITE;
        }

        public static String getSUBJECT() {
            return SUBJECT;
        }

        public static String getCreatedBy() {
            return CREATED_BY;
        }

        public static String getCreatedAt() {
            return CREATED_AT;
        }
    }

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE " + MySibaInviteEntry.TABLE_NAME + " ( " +
            MySibaInviteEntry.INVITE + " TEXT PRIMARY KEY NOT NULL," +
            MySibaInviteEntry.SUBJECT + " TEXT NOT NULL," +
            MySibaInviteEntry.CREATED_BY + " TEXT NOT NULL," +
            MySibaInviteEntry.CREATED_AT + " DATE)";

    public static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + MySibaInviteEntry.TABLE_NAME;
}
