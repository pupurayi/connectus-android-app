package com.connectus.mobile.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.Transaction;
import com.connectus.mobile.api.dto.TransactionType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");


    private Context context;
    private final List<Transaction> transactions;

    public TransactionRecyclerAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        TransactionType transactionType = null;
        try {
            transactionType = TransactionType.valueOf(transaction.getType());
        } catch (Exception ignore) {

        }
        String transactionTypeDisplay = (transactionType != null) ? transactionType.getDisplayName() : transaction.getType().replace("_", " ");
        holder.textViewTransactionType.setText(transactionTypeDisplay);


        double debit = transaction.getDebit();
        double credit = transaction.getCredit();
        @SuppressLint("DefaultLocale")
        String amount = (debit == 0) ? String.format("- %S %.2f", transaction.getCurrency(), credit) : String.format("%S %.2f", transaction.getCurrency(), debit);
        holder.textViewTransactionAmount.setText(amount);

        String createdAt = dateFormat.format(transaction.getCreatedAt());
        holder.textViewCreatedAt.setText(createdAt);

        String reference = (transaction.getReference() != null) ? "Ref: " + transaction.getReference() : "Id: " + transaction.getTransactionId();
        holder.textViewReference.setText(reference);

        if (!transaction.isSuccess()) {
            holder.textViewTransactionType.setTextColor(Color.RED);
            holder.textViewTransactionAmount.setTextColor(Color.RED);
            holder.textViewCreatedAt.setTextColor(Color.RED);
            holder.textViewReference.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTransactionType, textViewCreatedAt, textViewTransactionAmount, textViewReference;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTransactionType = (TextView) itemView.findViewById(R.id.text_view_transaction_type);
            textViewCreatedAt = (TextView) itemView.findViewById(R.id.text_view_created_at);
            textViewTransactionAmount = (TextView) itemView.findViewById(R.id.text_view_transaction_amount);
            textViewReference = (TextView) itemView.findViewById(R.id.text_view_reference);
        }
    }
}
