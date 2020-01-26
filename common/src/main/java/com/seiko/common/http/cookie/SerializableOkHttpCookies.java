package com.seiko.common.http.cookie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import okhttp3.Cookie;

/**
 * Modified by xyoye on 2017/6/23.
 */
public class SerializableOkHttpCookies implements Serializable {

    private transient final Cookie cookies;
    private transient Cookie clientCookies;

    public SerializableOkHttpCookies(Cookie cookies) {
        this.cookies = cookies;
    }

    public Cookie getCookies() {
        Cookie bestCookies = cookies;
        if (clientCookies != null) {
            bestCookies = clientCookies;
        }
        return bestCookies;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookies.name());
        out.writeObject(cookies.value());
        out.writeLong(cookies.expiresAt());
        out.writeObject(cookies.domain());
        out.writeObject(cookies.path());
        out.writeBoolean(cookies.secure());
        out.writeBoolean(cookies.httpOnly());
        out.writeBoolean(cookies.hostOnly());
        out.writeBoolean(cookies.persistent());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        long expiresAt = in.readLong();
        String domain = (String) in.readObject();
        String path = (String) in.readObject();
        boolean secure = in.readBoolean();
        boolean httpOnly = in.readBoolean();
        boolean hostOnly = in.readBoolean();
//        boolean persistent = in.readBoolean();
        Cookie.Builder builder = new Cookie.Builder();
        builder.name(name);
        builder.value(value);
        builder.expiresAt(expiresAt);
        if (hostOnly) {
            builder.hostOnlyDomain(domain);
        } else {
            builder.domain(domain);
        }
        builder.path(path);
        if (secure) {
            builder.secure();
        }
        if (httpOnly) {
            builder.httpOnly();
        }
        clientCookies =builder.build();
    }
}
