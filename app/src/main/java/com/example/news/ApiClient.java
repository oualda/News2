package com.example.news;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://dummy.com/"; // Requis par Retrofit, sera ignorée avec @Url

    public static Retrofit getRetrofitInstance() {
        OkHttpClient client = getSecureOkHttpClient();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
    }

    private static OkHttpClient getSecureOkHttpClient() {
        try {
            final TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            ConnectionSpec tlsSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
                    .build();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCertificates[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .connectionSpecs(Arrays.asList(
                            ConnectionSpec.CLEARTEXT, // autorise HTTP
                            tlsSpec                    // autorise HTTPS
                    ))
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        // Nettoyage du XML
                        String rawXml = response.body().string();
                        rawXml = rawXml.trim().replaceFirst("^([\\W]+)<", "<");
                        ResponseBody newBody = ResponseBody.create(response.body().contentType(), rawXml);

                        return response.newBuilder().body(newBody).build();
                    })
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .header("User-Agent", "Mozilla/5.0")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
