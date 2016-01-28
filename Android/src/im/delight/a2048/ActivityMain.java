package im.delight.a2048;

/*
 * Copyright (c) delight.im <info@delight.im>
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

import android.graphics.Bitmap;
import android.content.Intent;
import im.delight.android.webview.AdvancedWebView;
import java.io.File;
import java.util.Random;
import im.delight.android.baselib.Social;
import im.delight.android.baselib.Strings;
import im.delight.android.baselib.UI;
import im.delight.android.baselib.ViewScreenshot;
import im.delight.android.progress.SimpleProgressDialog;
import im.delight.apprater.AppRater;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMain extends Activity implements AdvancedWebView.Listener, ViewScreenshot.Callback {

	private static final String GAME_DOWNLOAD_URL = "http://www.delight.im/get/2048";
	private static final String GAME_HTML_URL = "file:///android_asset/Game/index.html";
	private static final String HTML_NEW_LINE = "<br />";
	private static final String HTML_NEW_PARAGRAPH = "<br /><br />";
	private static final String SCREENSHOT_FILENAME = "2048";
	private AdvancedWebView mWebViewGame;
	private View mViewProgress;
	private AlertDialog mAlertDialog;
	private SimpleProgressDialog mSimpleProgressDialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide the notification/system bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// create the layout from XML
		setContentView(R.layout.activity_main);

		// get the references to all views
		mWebViewGame = (AdvancedWebView) findViewById(R.id.webviewGame);
		mViewProgress = findViewById(R.id.viewProgress);

		// set up the WebView
		mWebViewGame.setListener(this, this);

		// load the local HTML/CSS/JS
		mWebViewGame.loadUrl(GAME_HTML_URL);

		// set up the ActionBar
		UI.forceOverflowMenu(this);

		// check if this is the first launch
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean isFirstLaunch = prefs.getBoolean(Preferences.IS_FIRST_LAUNCH, true);
		// if this is the first time the user launched the app
		if (isFirstLaunch) {
			// show an introductory help text
			showIntro();

			// remember that the first launch is now over
			final SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(Preferences.IS_FIRST_LAUNCH, false);
			if (Build.VERSION.SDK_INT >= 9) {
				editor.apply();
			}
			else {
				editor.commit();
			}
		}
		// if this is not the first time the user launched the app
		else {
			// prompt the user to rate the app if this is appropriate
			AppRater appRater = new AppRater(this);
			appRater.setPhrases(R.string.app_rater_title, R.string.app_rater_explanation, R.string.app_rater_now, R.string.app_rater_later, R.string.app_rater_never);
			appRater.show();
		}
	}

	private void showIntro() {
		final View viewIntro = View.inflate(this, R.layout.dialog_intro, null);

		final String[] metaDescription = getResources().getStringArray(R.array.meta_description);
		final StringBuilder shortDescription = new StringBuilder();
		shortDescription.append(metaDescription[0]);
		shortDescription.append("\n\n");
		shortDescription.append(metaDescription[1]);
		shortDescription.append("\n\n");
		shortDescription.append(metaDescription[2]);

		final TextView textViewIntro = (TextView) viewIntro.findViewById(R.id.textViewIntro);
		textViewIntro.setText(shortDescription.toString());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.how_it_works);
		builder.setView(viewIntro);
		builder.setNegativeButton(R.string.intro_start, null);
		mAlertDialog = builder.show();
	}

	private void showDemoBoard() {
		// randomly create either the initial board distribution or the final one
		final String boardDistribution;
		final int currentScore;
		if ((new Random()).nextInt(100) < 50) {
			boardDistribution = "[0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0]";
			currentScore = 0;
		}
		else {
			boardDistribution = "[8, 1024, 2, 32, 0, 64, 256, 4, 0, 1024, 16, 2, 0, 0, 0, 512]";
			currentScore = 12084;
		}

		// show the board distribution
		dispatchJavaScript("(function() { var powers = "+boardDistribution+"; var container = document.getElementsByClassName('tile-container')[0]; container.innerHTML = ''; for (var i = 0; i < powers.length; i++) { var power = powers[i]; if (power < 2) { continue; } var newTileHTML = '<div class=\"tile tile-'+power+' tile-position-'+(1+(i % 4))+'-'+(1+Math.floor(i / 4))+'\"><div class=\"tile-inner\">'+power+'</div></div>'; var newTile = document.createElement('div'); newTile.innerHTML = newTileHTML; container.appendChild(newTile.firstChild); } })();");

		// show the corresponding current score
		dispatchJavaScript("(function() { document.getElementsByClassName('score-container')[0].innerHTML = '"+currentScore+"'; })()");

		// show the general best score
		dispatchJavaScript("(function() { document.getElementsByClassName('best-container')[0].innerHTML = '16844'; })()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.new_game:
				// in demo mode
				if (Config.DEMO) {
					// create a (seemingly) random board distribution
					showDemoBoard();
				}
				// in release mode
				else {
					// create a new board with only two pieces
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(R.string.are_you_sure);
					builder.setMessage(R.string.new_game_sure);
					builder.setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startNewGame();
						}
					});
					builder.setNegativeButton(R.string.cancel, null);
					builder.show();
				}
				return true;
			case R.id.help:
				showHelp();
				return true;
			case R.id.share_with_friends:
				shareWithFriends();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private String getMetaHTML() {
		StringBuilder out = new StringBuilder();

		String[] descriptionLines = getResources().getStringArray(R.array.meta_description);
		for (String descriptionLine : descriptionLines) {
			if (out.length() > 0) {
				out.append(HTML_NEW_PARAGRAPH);
			}
			out.append(descriptionLine);
		}

		out.append(HTML_NEW_PARAGRAPH);
		out.append("<b>");
		out.append(getString(R.string.meta_share_title));
		out.append("</b>");
		out.append(HTML_NEW_LINE);
		out.append(getString(R.string.meta_share_body));

		out.append(HTML_NEW_PARAGRAPH);
		out.append("<b>");
		out.append(getString(R.string.meta_translate_title));
		out.append("</b>");
		out.append(HTML_NEW_LINE);
		out.append(getString(R.string.meta_translate_body));

		out.append(HTML_NEW_PARAGRAPH);
		out.append("<b>");
		out.append(getString(R.string.meta_credits_title));
		out.append("</b>");
		out.append(HTML_NEW_LINE);
		out.append(getString(R.string.meta_credits_body));

		out.append(HTML_NEW_PARAGRAPH);
		out.append("<b>");
		out.append(getString(R.string.meta_translators_title));
		out.append("</b>");
		out.append(HTML_NEW_LINE);
		out.append(getString(R.string.meta_translators_body).replace("\n", HTML_NEW_LINE));

		out.append(HTML_NEW_PARAGRAPH);
		out.append("<b>");
		out.append(getString(R.string.meta_libraries_title));
		out.append("</b>");
		out.append(HTML_NEW_LINE);
		out.append(getString(R.string.meta_libraries_body).replace("\n", HTML_NEW_LINE));

		return out.toString();
	}

	private void showHelp() {
		final String metaHTML = getMetaHTML();

		if (Config.DEMO) {
			final String[] descriptionChunks = Strings.splitToChunks(metaHTML, 4000);
			for (String descriptionChunk : descriptionChunks) {
				System.out.println(descriptionChunk);
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.help);
		builder.setMessage(Html.fromHtml(metaHTML));
		builder.setNeutralButton(R.string.ok, null);
		mAlertDialog = builder.show();
	}

	private void shareWithFriends() {
		// show the loading overlay
		setLoading(true);
		// build the screenshot and wait for the result
		new ViewScreenshot(ActivityMain.this, ActivityMain.this).from(mWebViewGame).asFile(SCREENSHOT_FILENAME).build();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        mWebViewGame.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (!mWebViewGame.onBackPressed()) { return; }

        super.onBackPressed();
    }

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();

		mWebViewGame.onResume();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		mWebViewGame.onPause();

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
			mAlertDialog = null;
		}

		mWebViewGame.onDestroy();

		setLoading(false);
	}

	@SuppressLint("NewApi")
	private void dispatchJavaScript(final String javaScript) {
		mWebViewGame.post(new Runnable() {

			@Override
			public void run() {
				if (Build.VERSION.SDK_INT >= 19) {
					mWebViewGame.evaluateJavascript(javaScript, null);
				}
				else {
					mWebViewGame.loadUrl("javascript:"+javaScript);
				}
			}

		});
	}

	private void startNewGame() {
		dispatchJavaScript("(function() { gameManager.restart(); })();");
	}

	private void setLoading(boolean loading) {
		if (loading) {
			mSimpleProgressDialog = SimpleProgressDialog.show(this);
		}
		else {
			if (mSimpleProgressDialog != null) {
				mSimpleProgressDialog.dismiss();
				mSimpleProgressDialog = null;
			}
		}
	}

	@Override
	public void onSuccess(final File file) {
		setLoading(false);

		final String invitationText = getString(R.string.invitation_teaser, GAME_DOWNLOAD_URL);
		final View viewShareText = View.inflate(this, R.layout.dialog_share_text, null);
		final ImageView imageViewSharePicture = (ImageView) viewShareText.findViewById(R.id.imageViewSharePicture);
		final EditText editTextShareText = (EditText) viewShareText.findViewById(R.id.editTextShareText);

		// show a preview of the screenshot that will be shared
		imageViewSharePicture.setImageURI(Uri.fromFile(file));

		// show a preview of the message that will be shared
		editTextShareText.setText(invitationText+"\n");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.share_with_friends);
		builder.setView(viewShareText);
		builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO share picture from `file` variable as well (seems to require external storage access)
				Social.shareText(ActivityMain.this, getString(R.string.share_with_friends), editTextShareText.getText().toString(), getString(R.string.app_name));
			}

		});
		builder.setNegativeButton(R.string.cancel, null);
		mAlertDialog = builder.show();
	}

	@Override
	public void onError() {
		setLoading(false);
		Toast.makeText(ActivityMain.this, R.string.share_screenshot_error, Toast.LENGTH_SHORT).show();
	}

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mViewProgress.setVisibility(View.GONE);
				mWebViewGame.setVisibility(View.VISIBLE);
			}

		});
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) { }

    @Override
    public void onExternalPageRequest(String url) { }

}
