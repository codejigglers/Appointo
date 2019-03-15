package examples.sdk.android.clover.com.appointo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web_view);
    WebView myWebView = (WebView) findViewById(R.id.webview);
    myWebView.setWebViewClient(new WebViewClient());
    WebSettings webSettings = myWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
      myWebView.evaluateJavascript("enable();", null);
    } else {
      myWebView.loadUrl("javascript:enable();");
    }
    myWebView.loadUrl("https://calendar.google.com/calendar/embed?mode=week&src=4ipjkra4hfdluiebvd8ngq8674%40group.calendar.google.com&ctz=America%2FChicago");
  }
}
