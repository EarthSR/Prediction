package com.example.prediction
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var open: EditText? = null
    private var high: EditText? = null
    private var low: EditText? = null
    private var adjustClose: EditText? = null
    private var close: EditText? = null
    private var volume: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val button = findViewById<Button>(R.id.buttonsent)
        open = findViewById(R.id.txtopen)
        high = findViewById(R.id.txthigh)
        low = findViewById(R.id.txtlow)
        adjustClose = findViewById(R.id.txtadjustclose)
        close = findViewById(R.id.txtclose)
        volume = findViewById(R.id.txtvolume)


        button.setOnClickListener {
            if (open!!.text.isEmpty() || high!!.text.isEmpty() || low!!.text.isEmpty() || adjustClose!!.text.isEmpty() || close!!.text.isEmpty() || volume!!.text.isEmpty()) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("!!!!!")
                builder.setMessage("กรุณากรอกข้อมูลให้ครบถ้วน")
                builder.setNeutralButton("OK", null)
                val alert = builder.create()
                alert.show()
                return@setOnClickListener
            }


            CoroutineScope(Dispatchers.IO).launch {
                val url: String = getString(R.string.root_url)
                val okHttpClient = OkHttpClient()
                val formBody: RequestBody = FormBody.Builder()
                    .add("open", open!!.text.toString())
                    .add("high", high!!.text.toString())
                    .add("low", low!!.text.toString())
                    .add("adjclose", adjustClose!!.text.toString())
                    .add("close", close!!.text.toString())
                    .add("volume", volume!!.text.toString())
                    .build()
                val request: Request = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        val price = data.getString("price")
                        val message = "ราคาที่คาดการ คือ $price ดอลล่า"

                        runOnUiThread {
                            val builder = AlertDialog.Builder(this@MainActivity)
                            builder.setTitle("ระบบทำนายราคาทองคำ!!")
                            builder.setMessage(message)
                            builder.setNeutralButton("OK", clearText())
                            val alert = builder.create()
                            alert.show()
                        }
                    }
                }
            }
        }
    }

    private fun clearText(): DialogInterface.OnClickListener? {
        return DialogInterface.OnClickListener { dialog, which ->
            open?.text?.clear()
            high?.text?.clear()
            low?.text?.clear()
            adjustClose?.text?.clear()
            close?.text?.clear()
            volume?.text?.clear()
            open?.requestFocus()
        }
    }
}
