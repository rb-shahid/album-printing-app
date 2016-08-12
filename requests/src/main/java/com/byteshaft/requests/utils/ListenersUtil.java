package com.byteshaft.requests.utils;

import android.content.Context;
import android.os.Handler;

import com.byteshaft.requests.FormDataHttpRequest;
import com.byteshaft.requests.HttpRequestStateListener;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ListenersUtil {

    private Handler mMainHandler;
    private static ListenersUtil sListenersUtil;

    public static ListenersUtil getInstance(Context context) {
        if (sListenersUtil == null) {
            sListenersUtil = new ListenersUtil(context);
        }
        return sListenersUtil;
    }

    private ListenersUtil(Context context) {
        mMainHandler = new Handler(context.getMainLooper());
    }

    protected void emitOnReadyStateChanged(
            ArrayList<HttpRequestStateListener> listeners,
            final HttpURLConnection connection,
            final int requestType,
            final int readyState
    ) {
        for (final HttpRequestStateListener listener : listeners) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onReadyStateChanged(connection, requestType, readyState);
                }
            });
        }
    }

    protected void emitOnFileUploadProgressChanged(
            ArrayList<FormDataHttpRequest.FileUploadProgressUpdateListener> listeners,
            final File file,
            final long uploaded,
            final long total
    ) {
        for (final FormDataHttpRequest.FileUploadProgressUpdateListener listener : listeners) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onFileUploadProgressUpdate(file, uploaded, total);
                }
            });
        }
    }
}