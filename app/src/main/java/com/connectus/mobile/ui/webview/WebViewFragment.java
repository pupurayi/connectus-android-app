package com.connectus.mobile.ui.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.ui.deposit.WireDepositFragment;
import com.connectus.mobile.ui.transaction.TransactionStatusFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class WebViewFragment extends Fragment {
    private static final String TAG = WebViewFragment.class.getSimpleName();

    ImageView imageViewBack;

    FragmentManager fragmentManager;
    WebView webView;
    private String link, parentFragment;
    private Map<String, String> headers = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            parentFragment = bundle.getString("parentFragment");
            link = bundle.getString("link");
            String headers = bundle.getString("headers");
            if (headers != null) {
                this.headers = new Gson().fromJson(headers, new TypeToken<HashMap<String, String>>() {
                }.getType());
            }
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        if (link == null) {
            getActivity().onBackPressed();
        }

        webView = getView().findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(link, headers);


        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

    private class MyWebViewClient extends WebViewClient {
        ProgressDialog pd = new ProgressDialog(getContext());

        public MyWebViewClient() {
            pd.setTitle("Loading...");
            pd.setMessage("Please Wait ...");
            pd.show();
        }

        @Override
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            handleUrl(view, url, headers);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            handleUrl(view, url, headers);
            return true;
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            if (pd != null) {
                pd.dismiss();
            }
        }
    }

    private void handleUrl(WebView webView, String url, Map<String, String> headers) {
        if (parentFragment.equals(WireDepositFragment.class.getSimpleName())) {
            if (url.contains("/payfast/response/success")) {
                Bundle bundle = new Bundle();
                TransactionStatusFragment transactionStatusFragment = new TransactionStatusFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                bundle.putBoolean("success", true);
                bundle.putString("transactionStatusTitle", "Deposit Successful");
                bundle.putString("transactionStatusMessage", "Deposit into profile was successful!");
                transactionStatusFragment.setArguments(bundle);
                transaction.replace(R.id.container, transactionStatusFragment, TransactionStatusFragment.class.getSimpleName());
                transaction.commit();
            } else if (url.contains("/payfast/response/cancel")) {
                Bundle bundle = new Bundle();
                TransactionStatusFragment transactionStatusFragment = new TransactionStatusFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                bundle.putBoolean("success", false);
                bundle.putString("transactionStatusTitle", "Deposit Failed");
                bundle.putString("transactionStatusMessage", "Deposit into profile was not successful!");
                transactionStatusFragment.setArguments(bundle);
                transaction.replace(R.id.container, transactionStatusFragment, TransactionStatusFragment.class.getSimpleName());
                transaction.commit();
            }
        }
        if (url.contains("emalyami.com")) {
            webView.loadUrl(url, headers);
        } else {
            webView.loadUrl(url);
        }
    }
}