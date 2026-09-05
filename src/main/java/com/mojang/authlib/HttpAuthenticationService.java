package com.mojang.authlib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class HttpAuthenticationService extends BaseAuthenticationService {
    private static final Logger LOGGER = LogManager.getLogger();

    protected HttpAuthenticationService() {
    }

    public static URL constantURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException var2) {
            throw new Error("Couldn't create constant for " + url, var2);
        }
    }

    public static String buildQuery(Map<String, Object> query) {
        if (query == null) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator var2 = query.entrySet().iterator();

            while (var2.hasNext()) {
                Entry<String, Object> entry = (Entry) var2.next();
                if (builder.length() > 0) {
                    builder.append('&');
                }

                try {
                    builder.append(URLEncoder.encode((String) entry.getKey(), "UTF-8"));
                } catch (UnsupportedEncodingException var6) {
                    LOGGER.error("Unexpected exception building query", var6);
                }

                if (entry.getValue() != null) {
                    builder.append('=');

                    try {
                        builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException var5) {
                        LOGGER.error("Unexpected exception building query", var5);
                    }
                }
            }

            return builder.toString();
        }
    }

    public static URL concatenateURL(URL url, String query) {
        try {
            return url.getQuery() != null && url.getQuery().length() > 0 ? new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "&" + query) : new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + query);
        } catch (MalformedURLException var3) {
            throw new IllegalArgumentException("Could not concatenate given URL with GET arguments!", var3);
        }
    }

    public String performPostRequest(URL url, String post, String contentType) throws IOException {
        throw new UnsupportedOperationException("HTTP requests are not supported in TeaVM");
    }

    public String performGetRequest(URL url) throws IOException {
        throw new UnsupportedOperationException("HTTP requests are not supported in TeaVM");
    }
}
