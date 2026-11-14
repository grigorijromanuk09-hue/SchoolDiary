package com.example.schooldiary

import android.annotation.SuppressLint
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
import org.json.JSONArray
import org.json.JSONTokener

class DnevnikActivity : ComponentActivity() {

    private lateinit var web: WebView

    private val extractJs = """
        (function(){
          var tables=document.querySelectorAll('table');
          var t=null;
          for(var i=0;i<tables.length;i++){
            if((tables[i].innerText||'').indexOf('Ср. взвеш')>=0){t=tables[i];break;}
          }
          if(!t) return null;
          var res=[];
          t.querySelectorAll('tbody tr').forEach(function(tr){
            var tds=tr.querySelectorAll('td');
            if(tds.length<2) return;
            var name=(tds[0].innerText||'').trim();
            var marks=[];
            tds[1].querySelectorAll('div').forEach(function(d){
              if(d.children.length===0){
                var s=(d.textContent||'').trim();
                if(s.length>=1 && s.length<=2 && /^[2-5]/.test(s) && (s.length==1 || s[1]=='+' || s[1]=='-')){
                  var desc=d.previousElementSibling ? (d.previousElementSibling.textContent||'') : '';
                  marks.push({m:s, ok: desc.indexOf('не учитывается')<0});
                }
              }
            });
            res.push({n:name, m:marks});
          });
          return JSON.stringify(res);
        })()
    """.trimIndent()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val button = Button(this).apply {
            text = "Загрузить оценки в приложение"
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
                val value = try {
                    JSONTokener(raw).nextValue()
                } catch (e: Exception) {
                    null
                }
                val text = value as? String
                if (text == null) {
                    Toast.makeText(
                        this,
                        "Таблица не найдена. Откройте страницу оценок и подождите, пока она загрузится.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    try {
                        val arr = JSONArray(text)
                        var subjects = 0
                        var marks = 0
                        for (i in 0 until arr.length()) {
                            val c = arr.getJSONObject(i).getJSONArray("m").length()
                            if (c > 0) subjects++
                            marks += c
                        }
                        getSharedPreferences("dnevnik", MODE_PRIVATE).edit()
                            .putString("marks", text)
                            .putLong("time", System.currentTimeMillis())
                            .apply()
                        Toast.makeText(
                            this,
                            "Сохранено: предметов " + subjects + ", оценок " + marks,
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Не удалось разобрать таблицу", Toast.LENGTH_LONG).show()
                    }
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
