package com.livelike.engagementsdksample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.livelike.engagementsdk.BuildConfig
import com.livelike.engagementsdksample.chat.ChatActivity
import com.livelike.engagementsdksample.widget.WidgetActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        llTextView.text = "LL SDK [ ${BuildConfig.VERSION_CODE} ]"
        clientTextFiled.setText("3WtkbrjmyPFUHTSckcVVUlikAAdHEy1P0zqqczF0")

        outlinedTextField.setText("719012f3-cf45-49cf-9407-c94fef0e3e80")


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
