package com.tech.sid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WaveView extends WebView {

    public WaveView(Context context) {
        super(context);
        initializeWebSettings();
        setupWebView();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeWebSettings();
        setupWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebSettings() {
        WebSettings webSetting = getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setDomStorageEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);
    }

    private void setupWebView() {
        setWebChromeClient(new WebChromeClient());
        setBackgroundColor(Color.TRANSPARENT);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        loadUrl("file:///android_asset/voicewave.html");
    }

    public void initialize(DisplayMetrics dm) {
        // Use full screen width from DisplayMetrics or WebView's measured width
        int width = getMeasuredWidth() > 0 ? getMeasuredWidth() : dm.widthPixels;
        int height = getMeasuredHeight() > 0 ? getMeasuredHeight() : dm.heightPixels / 4; // Example: 1/4 of screen height
        loadUrl("javascript:wave.setWidth(\"" + width + "\")");
        loadUrl("javascript:wave.setHeight(\"" + height + "\")");

        loadUrl("javascript:wave.start()");
        speechPaused();
    }

    public void stop() {
        loadUrl("javascript:wave.stop()");

        removeAllViews();
        clearHistory();
        clearCache(true);

        if (Build.VERSION.SDK_INT < 18) {
            clearView();
        } else {
            loadUrl("about:blank");
        }

        freeMemory();
        pauseTimers();
        loadUrl("file:///android_asset/voicewave.html");
    }

    public void speechStarted() {
        loadUrl("javascript:wave.setAmplitude(\"1\")");
    }

    public void setAmplitude(float amplitude) {
        loadUrl("javascript:wave.setAmplitude(\"" + amplitude + "\")");
    }

    public void speechEnded() {
        loadUrl("javascript:wave.setAmplitude(\"0.1\")");
    }

    public void speechPaused() {
        loadUrl("javascript:wave.setAmplitude(\"0.0\")");
    }
}
//package com.tech.sid;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.util.DisplayMetrics;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//
//public class WaveView extends WebView {
//
//    public WaveView(Context context) {
//        super(context);
//        initializeWebSettings();
//        setupWebView();
//    }
//
//    public WaveView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initializeWebSettings();
//        setupWebView();
//    }
//
//    @SuppressLint("SetJavaScriptEnabled")
//    private void initializeWebSettings() {
//        WebSettings webSetting = getSettings();
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setUseWideViewPort(true);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setDomStorageEnabled(true);
//        WebView.setWebContentsDebuggingEnabled(true);
//    }
//
//    private void setupWebView() {
//        setWebChromeClient(new WebChromeClient());
//        setBackgroundColor(Color.TRANSPARENT);
//        setVerticalScrollBarEnabled(false);
//        setHorizontalScrollBarEnabled(false);
//        loadUrl("file:///android_asset/voicewave.html");
//    }
//
//    public void initialize(DisplayMetrics dm) {
//        // Use WebView's measured dimensions or fallback to DisplayMetrics
//        int width = getMeasuredWidth() > 0 ? getMeasuredWidth() : dm.widthPixels * 92 / 100;
//        int height = getMeasuredHeight() > 0 ? getMeasuredHeight() : dm.heightPixels / 4; // Example: 1/4 of screen height
//        loadUrl("javascript:wave.setWidth(\"" + width + "\")");
//        loadUrl("javascript:wave.setHeight(\"" + height + "\")");
//        loadUrl("javascript:wave.start()");
//    }
//
//    public void stop() {
//        loadUrl("javascript:wave.stop()");
//
//        removeAllViews();
//        clearHistory();
//        clearCache(true);
//
//        if (Build.VERSION.SDK_INT < 18) {
//            clearView();
//        } else {
//            loadUrl("about:blank");
//        }
//
//        freeMemory();
//        pauseTimers();
//        loadUrl("file:///android_asset/voicewave.html");
//    }
//
//    public void speechStarted() {
//        loadUrl("javascript:wave.setAmplitude(\"1\")");
//    }
//
//    public void setAmplitude(float amplitude) {
//        loadUrl("javascript:wave.setAmplitude(\"" + amplitude + "\")");
//    }
//
//    public void speechEnded() {
//        loadUrl("javascript:wave.setAmplitude(\"0.1\")");
//    }
//
//    public void speechPaused() {
//        loadUrl("javascript:wave.setAmplitude(\"0.0\")");
//    }
//}
////package com.tech.sid;
////
/////*
////Copyright [2016] [Doğan Kılıç]
////
////Licensed under the Apache License, Version 2.0 (the "License")
////you may not use this file except in compliance with the License.
////You may obtain a copy of the License at
////
////    http://www.apache.org/licenses/LICENSE-2.0
////
////Unless required by applicable law or agreed to in writing, software
////distributed under the License is distributed on an "AS IS" BASIS,
////WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
////See the License for the specific language governing permissions and
////limitations under the License.
////*/
////
////import android.annotation.SuppressLint;
////import android.content.Context;
////import android.graphics.Color;
////import android.os.Build;
////import android.util.AttributeSet;
////import android.util.DisplayMetrics;
////import android.webkit.WebChromeClient;
////import android.webkit.WebSettings;
////import android.webkit.WebView;
////
////public class WaveView extends WebView {
////
////    public WaveView(Context context) {
////        super(context);
////    }
////
////    public WaveView(Context context, AttributeSet attrs) {
////        super(context, attrs);
////
////        initializeWebSettings();
////
////        setWebChromeClient(new WebChromeClient());
////        setBackgroundColor(Color.TRANSPARENT);
////        setVerticalScrollBarEnabled(false);
////        setHorizontalScrollBarEnabled(false);
////
////        loadUrl("file:///android_asset/voicewave.html");
////    }
////
////    @SuppressLint("SetJavaScriptEnabled")
////    private void initializeWebSettings() {
////        WebSettings webSetting = getSettings();
////        webSetting.setJavaScriptEnabled(true);
////        webSetting.setUseWideViewPort(true);
////        webSetting.setLoadWithOverviewMode(true);
////        webSetting.setDomStorageEnabled(true);
////        WebView.setWebContentsDebuggingEnabled(true);
////
////    }
////
////    public void initialize(DisplayMetrics dm) {
////        loadUrl("javascript:SW9.setWidth(\"" + dm.widthPixels * 92 / 100 + "\")");
////        loadUrl("javascript:SW9.start(\"" + "\")");
////    }
////
////    public void stop() {
////        loadUrl("javascript:SW9.stop(\"" + "\")");
////
////        removeAllViews();
////
////        clearHistory();
////        clearCache(true);
////
////        if (Build.VERSION.SDK_INT < 18) {
////            clearView();
////        } else {
////            loadUrl("about:blank");
////        }
////
////        freeMemory();
////        pauseTimers();
////
////        loadUrl("file:///android_asset/voicewave.html");
////    }
////
////    public void speechStarted() {
////        loadUrl("javascript:SW9.setAmplitude(\"" + 1 + "\")");
////    }
////    public void setAmplitude(float amplitude) {
////        loadUrl("javascript:SW9.setAmplitude(\"" + amplitude + "\")");
////    }
////    public void speechEnded() {
////        loadUrl("javascript:SW9.setAmplitude(\"" + 0.1 + "\")");
////    }
////
////    public void speechPaused() {
////        loadUrl("javascript:SW9.setAmplitude(\"" + 0.0 + "\")");
////    }
////}
