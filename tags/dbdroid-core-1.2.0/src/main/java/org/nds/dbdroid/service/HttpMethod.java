package org.nds.dbdroid.service;

/**
 * Java 5 enumeration of HTTP request methods.
 * @author Nicolas Dos Santos
 */
public enum HttpMethod {
    GET,
    POST,
    HEAD,
    OPTIONS,
    PUT,
    DELETE,
    TRACE;

    public static HttpMethod getHttpMethod(String method) {
        return valueOf(method.toUpperCase());
    }
}
