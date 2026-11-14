package com.example.schooldiary

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONTokener

class DnevnikActivity : ComponentActivity() {

    private lateinit var web: WebView

    private val extractJs = """
        (function(){
          function deepest(el){
            for (var i=0;i<el.children.length;i++){
              var c=el.children[i];
              if ((c.textContent||'').indexOf('Ср. взвеш')>=0) return deepest(c);
            }
            return el;
          }
          var el=deepest(document.body);
          if (el===document.body) return 'NOT_FOUND';
          while (el.parentElement && el.parentElement!==document.body && el.parentElement.innerText.length<3000){
            el=el.parentElement;
          }
          var cl=el.cloneNode(true);
          cl.querySelectorAll('script,style,img,svg,iframe,noscript').forEach(function(n){n.remove();});
          cl.querySelectorAll('*').forEach(function(n){
            Array.from(n.attributes).forEach(function(a){
              if (a.name!=='class') n.removeAttribute(a.name);
            });
          });
          var s=cl.outerHTML;
          return s.length>45000 ? s.slice(0,45000) : s;
        })()
    """.trimIndent()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val button = Button(this).apply {
            text = "Скопировать структуру таблицы"
        }
        web = WebView(this)
        root.addView(button, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(web, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true)
        web.webViewClient = WebViewClient()
        web.loadUrl("https://dnevnik.ru/")

        button.setOnClickListener {
            web.evaluateJavascript(extractJs) { raw ->
                val text = try {
                    JSONTokener(raw).nextValue() as? String ?: "NOT_FOUND"
                } catch (e: Exception) {
                    "NOT_FOUND"
                }
                if (text == "NOT_FOUND") {
                    Toast.makeText(this, "Таблица не найдена. Откройте страницу оценок и подождите, пока она загрузится.", Toast.LENGTH_LONG).show()
                } else {
                    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("dnevnik", text))
                    Toast.makeText(this, "Скопировано: " + text.length + " символов", Toast.LENGTH_LONG).show()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (web.canGoBack()) web.goBack() else finish()
            }
        })
    }
}
