package com.example.sample

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.multidex.BuildConfig
import com.example.sample.viewmodels.EngagementViewModel
import com.example.sample.viewmodels.EngagementViewModelFactory
import com.livelike.engagementsdk.services.messaging.proxies.WidgetInterceptor
import kotlinx.android.synthetic.main.activity_chat.*


class EngagementActivity : AppCompatActivity() {

    var functionalitySelected: Int = 0
    var mainViewModel: EngagementViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // to get intent received from MainActivity
        functionalitySelected = intent.getIntExtra("functionalitySelected", 0)

        //This will create an instance of Engagement viewmodel which can be used to creating session and initialization
        mainViewModel = ViewModelProvider(
            this,
            EngagementViewModelFactory(this.applicationContext as Application)
        ).get(EngagementViewModel::class.java)
        // Check whether chat or widget is selected
        if (functionalitySelected == CHATCLICKED) {
            widget_view.visibility = View.GONE
            mainViewModel?.setChatNickName("Kanav")


            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Do you want to open private chat ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                    finish()

                    mainViewModel?.getSession()
                        ?.enterChatRoom(com.example.sample.BuildConfig.CHATROOM_KEY)

                    mainViewModel?.getSession()
                        ?.joinChatRoom(com.example.sample.BuildConfig.CHATROOM_KEY)
                    chat_view.setSession(mainViewModel?.getSession()!!)


                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    chat_view.setSession(mainViewModel!!.getSession())

                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Select an option")
            // show alert dialog
            alert.show()
        } else {
            widget_view.setSession(mainViewModel!!.getSession())
            chat_view.visibility = View.GONE
            // Example of Widget Interceptor showing a dialog
            val interceptor = object : WidgetInterceptor() {
                override fun widgetWantsToShow() {
                    AlertDialog.Builder(this@EngagementActivity).apply {
                        setMessage("You received a Widget, what do you want to do?")
                        setPositiveButton("Show") { _, _ ->
                            showWidget() // Releases the widget
                        }
                        setNegativeButton("Dismiss") { _, _ ->
                            dismissWidget() // Discards the widget
                        }
                        create()
                    }.show()
                }
            }

            // You just need to add it on your session instance
            mainViewModel?.getSession()?.widgetInterceptor = interceptor
        }

    }

    override fun onPause() {
        super.onPause()
        mainViewModel?.pauseSession()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel?.resumeSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel?.closeSession()
    }


    companion object Functionality {
        const val CHATCLICKED = 1
        const val WIDGETCLICKED = 2
    }
}
