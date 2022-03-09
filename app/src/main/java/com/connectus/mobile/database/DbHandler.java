package com.connectus.mobile.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.connectus.mobile.api.dto.BalanceDTO;
import com.connectus.mobile.api.dto.Transaction;
import com.connectus.mobile.api.dto.siba.MySibaInvite;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.api.dto.siba.SibaProfileMember;
import com.connectus.mobile.api.dto.siba.SibaInvite;
import com.connectus.mobile.api.dto.siba.SibaPlan;
import com.connectus.mobile.api.dto.siba.chat.ChatMessage;
import com.connectus.mobile.database.contract.BalanceContract;
import com.connectus.mobile.database.contract.ChatMessageContract;
import com.connectus.mobile.database.contract.MySibaInviteContract;
import com.connectus.mobile.database.contract.NotificationContract;
import com.connectus.mobile.database.contract.OfferingContract;
import com.connectus.mobile.database.contract.SibaProfileContract;
import com.connectus.mobile.database.contract.SupportMessageContract;
import com.connectus.mobile.database.contract.TransactionContract;
import com.connectus.mobile.ui.offering.OfferingDto;
import com.connectus.mobile.ui.old.notification.NotificationDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DbHandler extends SQLiteOpenHelper {
    private static final String TAG = DbHandler.class.getSimpleName();
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static final String DATABASE_NAME = "connectus.db";
    // always update database version
    private static final int DATABASE_VERSION = 6;

    public DbHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OfferingContract.SQL_CREATE_ENTRIES);

        db.execSQL(BalanceContract.SQL_CREATE_ENTRIES);
        db.execSQL(NotificationContract.SQL_CREATE_ENTRIES);
        db.execSQL(TransactionContract.SQL_CREATE_ENTRIES);
        db.execSQL(MySibaInviteContract.SQL_CREATE_ENTRIES);
        db.execSQL(SibaProfileContract.SQL_CREATE_ENTRIES);
        db.execSQL(ChatMessageContract.SQL_CREATE_ENTRIES);
        db.execSQL(SupportMessageContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(OfferingContract.SQL_DROP_TABLE);

        db.execSQL(BalanceContract.SQL_DROP_TABLE);
        db.execSQL(NotificationContract.SQL_DROP_TABLE);
        db.execSQL(TransactionContract.SQL_DROP_TABLE);
        db.execSQL(MySibaInviteContract.SQL_DROP_TABLE);
        db.execSQL(SibaProfileContract.SQL_DROP_TABLE);
        db.execSQL(ChatMessageContract.SQL_DROP_TABLE);
        db.execSQL(SupportMessageContract.SQL_DROP_TABLE);
        onCreate(db);
    }

    public void insertOffering(OfferingDto offeringDto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(OfferingContract.OfferingEntry.getOfferingId(), offeringDto.getId().toString());
        cValues.put(OfferingContract.OfferingEntry.getUserId(), offeringDto.getUserId().toString());
        cValues.put(OfferingContract.OfferingEntry.getNAME(), offeringDto.getName());
        cValues.put(OfferingContract.OfferingEntry.getDESCRIPTION(), offeringDto.getDescription());
        cValues.put(OfferingContract.OfferingEntry.getRATING(), offeringDto.getRating());
        cValues.put(OfferingContract.OfferingEntry.getCREATED(), offeringDto.getCreated().toString());
        cValues.put(OfferingContract.OfferingEntry.getUPDATED(), offeringDto.getUpdated().toString());
        long newRowId = db.replace(OfferingContract.OfferingEntry.getTableName(), null, cValues);
        db.close();
    }

    public List<OfferingDto> getOfferings() {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = new String[]{OfferingContract.OfferingEntry.getOfferingId(), OfferingContract.OfferingEntry.getUserId(), OfferingContract.OfferingEntry.getNAME(), OfferingContract.OfferingEntry.getDESCRIPTION(), OfferingContract.OfferingEntry.getRATING(), OfferingContract.OfferingEntry.getCREATED(), OfferingContract.OfferingEntry.getUPDATED()};
        Cursor cursor = db.query(OfferingContract.OfferingEntry.getTableName(), columns, null, null, null, null, null);

        int offeringIdPos = cursor.getColumnIndex(OfferingContract.OfferingEntry.getOfferingId());
        int userIdPos = cursor.getColumnIndex(OfferingContract.OfferingEntry.getUserId());
        int namePos = cursor.getColumnIndex(OfferingContract.OfferingEntry.getNAME());
        int descriptionPos = cursor.getColumnIndex(OfferingContract.OfferingEntry.getDESCRIPTION());
        int ratingPos = cursor.getColumnIndex(OfferingContract.OfferingEntry.getRATING());
        int createdPos = cursor.getColumnIndex(OfferingContract.OfferingEntry.getCREATED());
        int updatedPos = cursor.getColumnIndex(OfferingContract.OfferingEntry.getUPDATED());

        List<OfferingDto> offerings = new LinkedList<>();
        while (cursor.moveToNext()) {
            UUID offeringId = UUID.fromString(cursor.getString(offeringIdPos));
            UUID userId = UUID.fromString(cursor.getString(userIdPos));
            String name = cursor.getString(namePos);
            String description = cursor.getString(descriptionPos);
            int rating = cursor.getInt(ratingPos);
            ZonedDateTime created = null;
            ZonedDateTime updated = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                created = ZonedDateTime.parse(cursor.getString(createdPos));
                updated = ZonedDateTime.parse(cursor.getString(updatedPos));
            }
            offerings.add(new OfferingDto(offeringId, userId, name, description, rating, created, updated));
        }
        cursor.close();
        db.close();
        return offerings;
    }

    public void deleteAllOfferings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OfferingContract.OfferingEntry.getTableName(), null, null);
        db.close();
    }


    public void insertBalance(BalanceDTO balanceDTO) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(BalanceContract.BalanceEntry.getBalanceId(), balanceDTO.getBalanceId());
        cValues.put(BalanceContract.BalanceEntry.getCURRENCY(), balanceDTO.getCurrency());
        cValues.put(BalanceContract.BalanceEntry.getAMOUNT(), balanceDTO.getAmount());
        String createdAt = balanceDTO.getCreatedAt() != null ? dateFormat.format(balanceDTO.getCreatedAt()) : null;
        cValues.put(BalanceContract.BalanceEntry.getCreatedAt(), createdAt);
        String updatedAt = balanceDTO.getUpdatedAt() != null ? dateFormat.format(balanceDTO.getUpdatedAt()) : null;
        cValues.put(BalanceContract.BalanceEntry.getUpdatedAt(), updatedAt);
        long newRowId = db.replace(BalanceContract.BalanceEntry.getTableName(), null, cValues);
        db.close();
    }

    public Set<BalanceDTO> getBalances() {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = new String[]{BalanceContract.BalanceEntry.getBalanceId(), BalanceContract.BalanceEntry.getCURRENCY(), BalanceContract.BalanceEntry.getAMOUNT(), BalanceContract.BalanceEntry.getCreatedAt(), BalanceContract.BalanceEntry.getUpdatedAt()};
        Cursor cursor = db.query(BalanceContract.BalanceEntry.getTableName(), columns, null, null, null, null, null);
        int balanceIdPos = cursor.getColumnIndex(BalanceContract.BalanceEntry.getBalanceId());
        int currencyPos = cursor.getColumnIndex(BalanceContract.BalanceEntry.getCURRENCY());
        int amountPos = cursor.getColumnIndex(BalanceContract.BalanceEntry.getAMOUNT());
        int createdAtPos = cursor.getColumnIndex(BalanceContract.BalanceEntry.getCreatedAt());
        int updatedAtPos = cursor.getColumnIndex(BalanceContract.BalanceEntry.getUpdatedAt());

        Set<BalanceDTO> balances = new HashSet<>();
        while (cursor.moveToNext()) {
            long balanceId = Long.parseLong(cursor.getString(balanceIdPos));
            String currency = cursor.getString(currencyPos);
            double amount = Double.parseDouble(cursor.getString(amountPos));

            Date createdAt = null;
            try {
                createdAt = dateFormat.parse(cursor.getString(createdAtPos));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date updatedAt = null;
            try {
                updatedAt = dateFormat.parse(cursor.getString(updatedAtPos));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            balances.add(new BalanceDTO(balanceId, currency, amount, createdAt, updatedAt));
        }
        cursor.close();
        db.close();
        return balances;
    }

    public void deleteAllBalances() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BalanceContract.BalanceEntry.getTableName(), null, null);
        db.close();
    }

    public void insertTransaction(Transaction transaction) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(TransactionContract.TransactionEntry.getTransactionId(), transaction.getTransactionId());
        cValues.put(TransactionContract.TransactionEntry.getREFERENCE(), transaction.getReference());
        cValues.put(TransactionContract.TransactionEntry.getTYPE(), transaction.getType());
        cValues.put(TransactionContract.TransactionEntry.getSUCCESS(), transaction.isSuccess());
        cValues.put(TransactionContract.TransactionEntry.getCURRENCY(), transaction.getCurrency());
        cValues.put(TransactionContract.TransactionEntry.getDEBIT(), transaction.getDebit());
        cValues.put(TransactionContract.TransactionEntry.getCREDIT(), transaction.getCredit());
        cValues.put(TransactionContract.TransactionEntry.getCHARGE(), transaction.getCharge());
        cValues.put(TransactionContract.TransactionEntry.getNOTE(), transaction.getNote());
        String createdAt = transaction.getCreatedAt() != null ? dateFormat.format(transaction.getCreatedAt()) : null;
        cValues.put(TransactionContract.TransactionEntry.getCreatedAt(), createdAt);
        String updatedAt = transaction.getUpdatedAt() != null ? dateFormat.format(transaction.getUpdatedAt()) : null;
        cValues.put(TransactionContract.TransactionEntry.getUpdatedAt(), updatedAt);
        long newRowId = db.replace(TransactionContract.TransactionEntry.getTableName(), null, cValues);
        db.close();
    }

    public List<Transaction> getTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = new String[]{TransactionContract.TransactionEntry.getTransactionId(), TransactionContract.TransactionEntry.getREFERENCE(), TransactionContract.TransactionEntry.getTYPE(), TransactionContract.TransactionEntry.getSUCCESS(), TransactionContract.TransactionEntry.getCURRENCY(), TransactionContract.TransactionEntry.getDEBIT(), TransactionContract.TransactionEntry.getCREDIT(), TransactionContract.TransactionEntry.getCHARGE(), TransactionContract.TransactionEntry.getNOTE(), TransactionContract.TransactionEntry.getCreatedAt(), TransactionContract.TransactionEntry.getUpdatedAt()};
        Cursor cursor = db.query(TransactionContract.TransactionEntry.getTableName(), columns, null, null, null, null, null);
        int transactionIdPos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getTransactionId());
        int referencePos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getREFERENCE());
        int typePos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getTYPE());
        int successPos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getSUCCESS());
        int currencyPos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getCURRENCY());
        int debitPos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getDEBIT());
        int creditPos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getCREDIT());
        int chargePos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getCHARGE());
        int notePos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getNOTE());
        int createdAtPos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getCreatedAt());
        int updatedAtPos = cursor.getColumnIndex(TransactionContract.TransactionEntry.getUpdatedAt());


        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            long transactionId = Long.parseLong(cursor.getString(transactionIdPos));
            String reference = cursor.getString(referencePos);
            String type = cursor.getString(typePos);
            boolean success = cursor.getInt(successPos) == 1;
            String currency = cursor.getString(currencyPos);
            double debit = cursor.getDouble(debitPos);
            double credit = cursor.getDouble(creditPos);
            double charge = cursor.getDouble(chargePos);
            String note = cursor.getString(notePos);
            Date createdAt = null;
            try {
                createdAt = dateFormat.parse(cursor.getString(createdAtPos));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date updatedAt = null;
            try {
                updatedAt = dateFormat.parse(cursor.getString(updatedAtPos));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction transaction = new Transaction(transactionId, reference, type, success, currency, debit, credit, charge, note, createdAt, updatedAt);
            transactions.add(transaction);
        }
        cursor.close();
        db.close();
        return transactions;
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TransactionContract.TransactionEntry.getTableName(), null, null);
        db.close();
    }

    public void insertNotification(NotificationDTO notificationDTO) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(NotificationContract.NotificationEntry.getTITLE(), notificationDTO.getTitle());
        cValues.put(NotificationContract.NotificationEntry.getMESSAGE(), notificationDTO.getMessage());
        cValues.put(NotificationContract.NotificationEntry.getSentTime(), notificationDTO.getSentTime());
        long newRowId = db.insert(NotificationContract.NotificationEntry.getTableName(), null, cValues);
        db.close();
    }

    public List<NotificationDTO> getNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = new String[]{NotificationContract.NotificationEntry.getNotificationId(), NotificationContract.NotificationEntry.getTITLE(), NotificationContract.NotificationEntry.getMESSAGE(), NotificationContract.NotificationEntry.getSentTime()};
        Cursor cursor = db.query(NotificationContract.NotificationEntry.getTableName(), columns, null, null, null, null, null);
        int notificationIdPos = cursor.getColumnIndex(NotificationContract.NotificationEntry.getNotificationId());
        int titlePos = cursor.getColumnIndex(NotificationContract.NotificationEntry.getTITLE());
        int messagePos = cursor.getColumnIndex(NotificationContract.NotificationEntry.getMESSAGE());
        int sentTimePos = cursor.getColumnIndex(NotificationContract.NotificationEntry.getSentTime());

        List<NotificationDTO> notifications = new LinkedList<>();
        while (cursor.moveToNext()) {
            long notificationId = cursor.getLong(notificationIdPos);
            String title = cursor.getString(titlePos);
            String message = cursor.getString(messagePos);
            long sentTime = cursor.getLong(sentTimePos);
            notifications.add(new NotificationDTO(notificationId, title, message, sentTime));
        }
        cursor.close();
        db.close();
        return notifications;
    }

    public void deleteAllNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NotificationContract.NotificationEntry.getTableName(), null, null);
        db.close();
    }

    public void insertMySibaInvite(MySibaInvite mySibaInvite) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(MySibaInviteContract.MySibaInviteEntry.getINVITE(), mySibaInvite.getInvite());
        cValues.put(MySibaInviteContract.MySibaInviteEntry.getSUBJECT(), mySibaInvite.getSubject());
        cValues.put(MySibaInviteContract.MySibaInviteEntry.getCreatedBy(), mySibaInvite.getCreatedBy());
        String createdAt = mySibaInvite.getCreatedAt() != null ? dateFormat.format(mySibaInvite.getCreatedAt()) : null;
        cValues.put(MySibaInviteContract.MySibaInviteEntry.getCreatedAt(), createdAt);
        long newRowId = db.replace(MySibaInviteContract.MySibaInviteEntry.getTableName(), null, cValues);
        db.close();
    }

    public List<MySibaInvite> getMySibaInvites() {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = new String[]{MySibaInviteContract.MySibaInviteEntry.getINVITE(), MySibaInviteContract.MySibaInviteEntry.getSUBJECT(), MySibaInviteContract.MySibaInviteEntry.getCreatedBy(), MySibaInviteContract.MySibaInviteEntry.getCreatedAt()};
        Cursor cursor = db.query(MySibaInviteContract.MySibaInviteEntry.getTableName(), columns, null, null, null, null, null);
        int invitePos = cursor.getColumnIndex(MySibaInviteContract.MySibaInviteEntry.getINVITE());
        int subjectPos = cursor.getColumnIndex(MySibaInviteContract.MySibaInviteEntry.getSUBJECT());
        int createdByPos = cursor.getColumnIndex(MySibaInviteContract.MySibaInviteEntry.getCreatedBy());
        int createdAtPos = cursor.getColumnIndex(MySibaInviteContract.MySibaInviteEntry.getCreatedAt());

        List<MySibaInvite> invites = new LinkedList<>();
        while (cursor.moveToNext()) {
            String invite = cursor.getString(invitePos);
            String subject = cursor.getString(subjectPos);
            String createdBy = cursor.getString(createdByPos);

            Date createdAt = null;
            try {
                createdAt = dateFormat.parse(cursor.getString(createdAtPos));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            invites.add(new MySibaInvite(invite, subject, createdBy, createdAt));
        }
        cursor.close();
        db.close();
        return invites;
    }

    public void deleteAllMySibaInvites() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MySibaInviteContract.MySibaInviteEntry.getTableName(), null, null);
        db.close();
    }

    public void insertSibaProfile(SibaProfile sibaProfile) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(SibaProfileContract.SibaProfileEntry.getSibaProfileId(), sibaProfile.getId().toString());
        cValues.put(SibaProfileContract.SibaProfileEntry.getSUBJECT(), sibaProfile.getSubject());
        cValues.put(SibaProfileContract.SibaProfileEntry.getAdminProfileId(), sibaProfile.getAdminProfileId().toString());
        cValues.put(SibaProfileContract.SibaProfileEntry.getCURRENCY(), sibaProfile.getCurrency());
        cValues.put(SibaProfileContract.SibaProfileEntry.getBALANCE(), sibaProfile.getBalance());
        cValues.put(SibaProfileContract.SibaProfileEntry.getSTATUS(), sibaProfile.getStatus());
        cValues.put(SibaProfileContract.SibaProfileEntry.getMEMBERS(), new Gson().toJson(sibaProfile.getMembers()));
        cValues.put(SibaProfileContract.SibaProfileEntry.getINVITES(), new Gson().toJson(sibaProfile.getInvites()));
        cValues.put(SibaProfileContract.SibaProfileEntry.getPLANS(), new Gson().toJson(sibaProfile.getPlans()));
        String createdAt = sibaProfile.getCreatedAt() != null ? dateFormat.format(sibaProfile.getCreatedAt()) : null;
        cValues.put(SibaProfileContract.SibaProfileEntry.getCreatedAt(), createdAt);
        String updatedAt = sibaProfile.getUpdatedAt() != null ? dateFormat.format(sibaProfile.getUpdatedAt()) : null;
        cValues.put(SibaProfileContract.SibaProfileEntry.getUpdatedAt(), updatedAt);
        long newRowId = db.replace(SibaProfileContract.SibaProfileEntry.getTableName(), null, cValues);
        db.close();
    }

    public List<SibaProfile> getSibaProfiles() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = new String[]{SibaProfileContract.SibaProfileEntry.getSibaProfileId(), SibaProfileContract.SibaProfileEntry.getSUBJECT(), SibaProfileContract.SibaProfileEntry.getAdminProfileId(), SibaProfileContract.SibaProfileEntry.getCURRENCY(), SibaProfileContract.SibaProfileEntry.getBALANCE(), SibaProfileContract.SibaProfileEntry.getSTATUS(), SibaProfileContract.SibaProfileEntry.getMEMBERS(), SibaProfileContract.SibaProfileEntry.getINVITES(), SibaProfileContract.SibaProfileEntry.getPLANS(), SibaProfileContract.SibaProfileEntry.getCreatedAt(), SibaProfileContract.SibaProfileEntry.getUpdatedAt()};
        Cursor cursor = db.query(SibaProfileContract.SibaProfileEntry.getTableName(), columns, null, null, null, null, null);
        int sibaProfileIdPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getSibaProfileId());
        int subjectPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getSUBJECT());
        int adminProfileIdPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getAdminProfileId());
        int currencyPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getCURRENCY());
        int balancePos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getBALANCE());
        int statusPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getSTATUS());
        int membersPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getMEMBERS());
        int invitesPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getINVITES());
        int plansPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getPLANS());
        int createdAtPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getCreatedAt());
        int updatedAtPos = cursor.getColumnIndex(SibaProfileContract.SibaProfileEntry.getUpdatedAt());


        List<SibaProfile> sibaProfiles = new LinkedList<>();
        while (cursor.moveToNext()) {
            UUID sibaProfileId = UUID.fromString(cursor.getString(sibaProfileIdPos));
            String subject = cursor.getString(subjectPos);
            UUID adminProfileId = UUID.fromString(cursor.getString(adminProfileIdPos));
            String currency = cursor.getString(currencyPos);
            Double balance = cursor.getDouble(balancePos);
            String status = cursor.getString(statusPos);

            List<SibaProfileMember> members = null;
            try {
                String membersJson = cursor.getString(membersPos);
                members = new Gson().fromJson(membersJson, new TypeToken<List<SibaProfileMember>>() {
                }.getType());
            } catch (Exception ignore) {
            }

            List<SibaInvite> invites = null;
            try {
                String invitesJson = cursor.getString(invitesPos);
                invites = new Gson().fromJson(invitesJson, new TypeToken<List<SibaInvite>>() {
                }.getType());
            } catch (Exception ignore) {
            }

            String plansJson = cursor.getString(plansPos);
            List<SibaPlan> plans = plansJson != null ? new Gson().fromJson(plansJson, new TypeToken<ArrayList<SibaPlan>>() {
            }.getType()) : null;

            Date createdAt = null;
            try {
                createdAt = dateFormat.parse(cursor.getString(createdAtPos));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date updatedAt = null;
            try {
                updatedAt = dateFormat.parse(cursor.getString(updatedAtPos));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sibaProfiles.add(new SibaProfile(sibaProfileId, subject, adminProfileId, currency, balance, status, members, invites, plans, createdAt, updatedAt));
        }
        cursor.close();
        db.close();
        return sibaProfiles;
    }

    public void deleteAllSibaProfiles() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SibaProfileContract.SibaProfileEntry.getTableName(), null, null);
        db.close();
    }

    public void insertChatMessage(ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(ChatMessageContract.ChatMessageEntry.getMessageId(), chatMessage.getMessageId());
        cValues.put(ChatMessageContract.ChatMessageEntry.getSibaProfileId(), chatMessage.getSiba_profile_id().toString());
        cValues.put(ChatMessageContract.ChatMessageEntry.getSenderProfileId(), chatMessage.getSender_profile_id().toString());
        cValues.put(ChatMessageContract.ChatMessageEntry.getMESSAGE(), chatMessage.getMessage());
        cValues.put(ChatMessageContract.ChatMessageEntry.getCreatedAt(), chatMessage.getCreatedAt());
        long newRowId = db.replace(ChatMessageContract.ChatMessageEntry.getTableName(), null, cValues);
        db.close();
    }

    public List<ChatMessage> getChatMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = new String[]{ChatMessageContract.ChatMessageEntry.getMessageId(), ChatMessageContract.ChatMessageEntry.getSibaProfileId(), ChatMessageContract.ChatMessageEntry.getSenderProfileId(), ChatMessageContract.ChatMessageEntry.getMESSAGE(), ChatMessageContract.ChatMessageEntry.getCreatedAt()};
        Cursor cursor = db.query(ChatMessageContract.ChatMessageEntry.getTableName(), columns, null, null, null, null, ChatMessageContract.ChatMessageEntry.getCreatedAt());
        int messageIdPos = cursor.getColumnIndex(ChatMessageContract.ChatMessageEntry.getMessageId());
        int sibaProfileIdPos = cursor.getColumnIndex(ChatMessageContract.ChatMessageEntry.getSibaProfileId());
        int senderProfileIdPos = cursor.getColumnIndex(ChatMessageContract.ChatMessageEntry.getSenderProfileId());
        int messagePos = cursor.getColumnIndex(ChatMessageContract.ChatMessageEntry.getMESSAGE());
        int createdAtPos = cursor.getColumnIndex(ChatMessageContract.ChatMessageEntry.getCreatedAt());

        List<ChatMessage> chatMessages = new LinkedList<>();
        while (cursor.moveToNext()) {
            String messageId = cursor.getString(messageIdPos);
            UUID sibaProfileId = UUID.fromString(cursor.getString(sibaProfileIdPos));
            UUID senderProfileId = UUID.fromString(cursor.getString(senderProfileIdPos));
            String message = cursor.getString(messagePos);
            Long createdAt = cursor.getLong(createdAtPos);
            chatMessages.add(new ChatMessage(messageId, sibaProfileId, senderProfileId, message, createdAt));
        }
        cursor.close();
        db.close();
        return chatMessages;
    }

    public void deleteAllChatMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ChatMessageContract.ChatMessageEntry.getTableName(), null, null);
        db.close();
    }

    public void deleteAllSupportMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SupportMessageContract.SupportMessageEntry.getTableName(), null, null);
        db.close();
    }
}
