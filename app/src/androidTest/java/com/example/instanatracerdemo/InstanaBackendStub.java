package com.example.instanatracerdemo;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

class InstanaBackendStub extends NanoHTTPD {

    private final List<String> spans = new ArrayList<>();

    public InstanaBackendStub(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.POST && session.getUri().equals("/api")) {
            spans.add(convertStreamToString(session.getInputStream()));
            Response response = newFixedLengthResponse(Response.Status.NO_CONTENT, "", null);
            response.addHeader("Connection", "close");
            return response;
        }
        if (session.getMethod() == Method.GET && session.getUri().equals("/calls")) {
            return newFixedLengthResponse(Response.Status.OK, "application/json", spansAsJsonArray());
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json", null);
    }

    private String spansAsJsonArray() {
        StringBuilder sb = new StringBuilder();
        for (String span: spans) {
            sb.append(span);
            sb.append("\n");
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
