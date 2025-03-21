package com.imes.base.network.okhttp;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;

import javax.net.ssl.HostnameVerifier;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.Protocol;

public class OkHttpClientBuilderOption {

    public List<Interceptor> interceptors;
    public List<Interceptor> networkInterceptors;
    public Authenticator authenticator;
    public Cache cache;
    public CertificatePinner certificatePinner;
    public ConnectionPool connectionPool;
    public List<ConnectionSpec> connectionSpec;
    public CookieJar cookieJar;
    public Dispatcher dispatcher;
    public Dns dns;
    public HostnameVerifier hostnameVerifier;
    public List<Protocol> protocols;
    public Proxy proxy;
    public Authenticator proxyAuthenticator;
    public ProxySelector proxySelector;
    public Boolean followRedirects;
    public Boolean followSslRedirects;

}