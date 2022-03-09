package com.connectus.mobile.ui.old.transaction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.ui.dashboard.DashboardFragment;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TransactionStatusFragment extends Fragment {
    private static final String TAG = TransactionStatusFragment.class.getSimpleName();

    ImageView imageViewTransactionStatus;
    TextView textViewTransactionStatusTitle, textViewTransactionStatusMessage;
    Button buttonClose;

    FragmentManager fragmentManager;
    private boolean success;
    private String transactionStatusTitle;
    private String transactionStatusMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            success = arguments.getBoolean("success");
            transactionStatusTitle = arguments.getString("transactionStatusTitle");
            transactionStatusMessage = arguments.getString("transactionStatusMessage");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        if (transactionStatusTitle == null || transactionStatusMessage == null) {
            getActivity().onBackPressed();
        }
        imageViewTransactionStatus = view.findViewById(R.id.image_view_transaction_status);
        if (success) {
            imageViewTransactionStatus.setImageResource(R.drawable.checked);
            imageViewTransactionStatus.setColorFilter(getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            imageViewTransactionStatus.setImageResource(R.drawable.cancel);
            imageViewTransactionStatus.setColorFilter(getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
        textViewTransactionStatusTitle = view.findViewById(R.id.text_view_transaction_status_title);
        textViewTransactionStatusTitle.setText(transactionStatusTitle);
        textViewTransactionStatusMessage = view.findViewById(R.id.text_view_transaction_status_message);
        textViewTransactionStatusMessage.setText(transactionStatusMessage);

        buttonClose = view.findViewById(R.id.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
                if (dashboardFragment == null) {
                    dashboardFragment = new DashboardFragment();
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
                transaction.commit();
            }
        });
    }
}