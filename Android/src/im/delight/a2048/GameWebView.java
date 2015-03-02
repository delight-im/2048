package im.delight.a2048;

/**
 * Copyright 2014 www.delight.im <info@delight.im>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GameWebView extends WebView {

	private GameListener mGameListener;
	private boolean mReady;

	public GameWebView(Context context) {
		super(context);
		init();
	}

	public GameWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GameWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setGameListener(GameListener listener) {
		mGameListener = listener;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	protected void init() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		setSaveEnabled(true);

		WebSettings webSettings = getSettings();
		webSettings.setAllowFileAccess(false);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

		setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				if (mGameListener != null) {
					mGameListener.onLoaded();
				}
			}

		});
	}

	@SuppressWarnings("deprecation")
	public void init(Context context) {
		final String filesDir = context.getFilesDir().getPath();
		final String databaseDir = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases";

		WebSettings webSettings = getSettings();
		webSettings.setDatabaseEnabled(true);
		webSettings.setDatabasePath(databaseDir);

		mReady = true;
	}

	@Override
	public void loadUrl(String url) {
		if (mReady) {
			super.loadUrl(url);
		}
		else {
			throw new RuntimeException("You must call init(...) before you can call loadUrl(...)");
		}
	}

	@Override
	public void loadUrl(String url, Map<String,String> additionalHttpHeaders) {
		if (mReady) {
			super.loadUrl(url, additionalHttpHeaders);
		}
		else {
			throw new RuntimeException("You must call init(...) before you can call loadUrl(...)");
		}
	}

}
