package com.livelike.engagementsdksample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.livelike.engagementsdksample.chat.ChatActivity
import com.livelike.engagementsdksample.widget.WidgetActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        llTextView.text = "LL SDK [ ${BuildConfig.VERSION_CODE} ]"

        clientTextFiled.setText("mOBYul18quffrBDuq2IACKtVuLbUzXIPye5S3bq5")
        outlinedTextField.setText("09d93835-ee52-4757-976c-ea09d6a5798c")


        saveClientButton.setOnClickListener {
            if (!clientTextFiled.text.isNullOrEmpty()) {
                (application as LiveLikeApplication).setId(clientTextFiled.text.toString())
                saveClientButton.text = "Saved"
            } else {
                Toast.makeText(this, "Client id cannot be blank", Toast.LENGTH_LONG).show()
            }
        }

        saveProgramButton.setOnClickListener {
            if (!outlinedTextField.text.isNullOrEmpty()) {
                (application as LiveLikeApplication).setProgramCode(outlinedTextField.text.toString())
                saveProgramButton.text = "Saved"
            } else {
                Toast.makeText(this, "Program id cannot be blank", Toast.LENGTH_LONG).show()
            }
        }

        // when user clicks on Chat option
        chatLayout.setOnClickListener {
            if (!outlinedTextField.text.isNullOrEmpty()) {
                (application as LiveLikeApplication).setProgramCode(outlinedTextField.text.toString())
                val intent = Intent(this, ChatActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Program id cannot be blank", Toast.LENGTH_LONG).show()
            }
        }
        // when user clicks on Widget option
        widgetLayout.setOnClickListener {
            if (!outlinedTextField.text.isNullOrEmpty()) {
                val intent = Intent(this, WidgetActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Program id cannot be blank", Toast.LENGTH_LONG).show()
            }
        }
    }
}
