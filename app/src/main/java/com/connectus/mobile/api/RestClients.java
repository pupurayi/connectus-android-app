package com.connectus.mobile.api;

import com.connectus.mobile.common.Constants;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClients {
    private static APIService apiService;
    private static APIService sibaApiService;

    public RestClients() {
        apiService = setupRestClient();
        sibaApiService = setupSibaRestClient();
    }

    public APIService get() {
        return apiService;
    }

    public APIService getSibaApiService() {
        return sibaApiService;
    }


    private APIService setupRestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.CORE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new SelfSigningClientBuilder().createClient())
                .build();
        return retrofit.create(APIService.class);
    }

    private APIService setupSibaRestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SIBA_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new SelfSigningClientBuilder().createClient())
                .build();
        return retrofit.create(APIService.class);
    }

    public class SelfSigningClientBuilder {
        @SuppressWarnings("null")
        public OkHttpClient configureClient(final OkHttpClient.Builder client) {
            final TrustManager[] certs = new TrustManager[]{new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                }
            }};
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(null, certs, new SecureRandom());
            } catch (final java.security.GeneralSecurityException ex) {
            }
            try {
                final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(final String hostname, final SSLSession session) {
                        return true;
                    }
                };
                client.hostnameVerifier(hostnameVerifier);
                client.sslSocketFactory(ctx.getSocketFactory(), (X509TrustManager) certs[0]);
            } catch (final Exception e) {
            }
            return client.build();
        }

        public OkHttpClient createClient() {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            final OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.addInterceptor(httpLoggingInterceptor);
            client.readTimeout(1, TimeUnit.MINUTES);
            client.connectTimeout(1, TimeUnit.MINUTES);
            return configureClient(client);
        }
    }
}

