package com.imes.base.network.okhttp;

import com.imes.base.rubik.Rubik;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class ClientBuilder {
    private OkHttpClient client;

    public ClientBuilder() {
    }

    public OkHttpClient buildClient(BaseClientOption option){
        X509TrustManager trustManager = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            trustManager = trustManagerForAllCertificates();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory,trustManager)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        client = configOkHttpClient(option, builder);
        return client;
    }

    private X509TrustManager trustManagerForAllCertificates()
            throws GeneralSecurityException {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
    }

    private OkHttpClient configOkHttpClient(BaseClientOption clientOption, OkHttpClient.Builder builder) {
        if (clientOption != null) {
            OkHttpClientBuilderOption option = clientOption.option();
            if (option != null) {
                if (option.dispatcher != null) {
                    builder.dispatcher(option.dispatcher);
                }
                if (option.followRedirects != null) {
                    builder.followRedirects(option.followRedirects);
                }
                if (option.followSslRedirects != null) {
                    builder.followSslRedirects(option.followSslRedirects);
                }
                if (option.dns != null) {
                    builder.dns(option.dns);
                }
                if (option.cache != null) {
                    builder.cache(option.cache);
                }
                if (option.authenticator != null) {
                    builder.authenticator(option.authenticator);
                }
                if (option.certificatePinner != null) {
                    builder.certificatePinner(option.certificatePinner);
                }
                if (option.connectionPool != null) {
                    builder.connectionPool(option.connectionPool);
                }
                if (option.connectionSpec != null && option.connectionSpec.size() > 0) {
                    builder.connectionSpecs(option.connectionSpec);
                }
                if (option.cookieJar != null) {
                    builder.cookieJar(option.cookieJar);
                }
                if (option.hostnameVerifier != null) {
                    builder.hostnameVerifier(option.hostnameVerifier);
                }
                if (option.protocols != null && option.protocols.size() > 0) {
                    builder.protocols(option.protocols);
                }
                if (option.proxy != null) {
                    builder.proxy(option.proxy);
                }
                if (option.proxyAuthenticator != null) {
                    builder.proxyAuthenticator(option.proxyAuthenticator);
                }
                if (option.proxySelector != null) {
                    builder.proxySelector(option.proxySelector);
                }
                List<Interceptor> interceptors = option.interceptors;
                if (interceptors != null && interceptors.size() > 0) {
                    for (Interceptor interceptor : interceptors) {
                        builder.addInterceptor(interceptor);
                    }
                }
                List<Interceptor> networkInterceptors = option.networkInterceptors;
                if (networkInterceptors != null && networkInterceptors.size() > 0) {
                    for (Interceptor networkInterceptor : networkInterceptors) {
                        builder.addNetworkInterceptor(networkInterceptor);
                    }
                }

            }
            //添加日志拦截
            builder.addNetworkInterceptor(Rubik.get().getInterceptor());
        }
        return builder.build();
    }
}
