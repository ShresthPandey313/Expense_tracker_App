package com.example.expancetracker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.lang.Runnable
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class ExpanceAccessibilityService: AccessibilityService(){
    private var serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var parentJob: Job? = null
    private var isProcessing = false
    private var lastPayment = ""
    private var overView: View?= null
    private var lastTriggerTime = 0L
    private var ispopupshown = false
    private var lastDetectedText = ""


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            val rootNode = rootInActiveWindow ?: return // extract the ui element of the app on mobile
            val screenText = getAllText(rootNode)  // function in the onInterrupt method
            paymentDetected(screenText)
        }
    }

    override fun onInterrupt() {
    }
    //--------------------------------- getAllText()--------------------------------------------------//
    private fun getAllText(node: AccessibilityNodeInfo?): String{
        if( node == null) return ""
        var text = ""
        if(node.text != null){
            text += node.text.toString() + " "
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                text += getAllText(child)
            }
        }
        return text
    }
    //--------------------------------- getAllText()--------------------------------------------------//

    //----------------------------------- paymentDetected() -----------------------------------------//

    private fun paymentDetected(text: String){
        var lowerCase = text.lowercase()

        if(ispopupshown || isProcessing) return

        if ((lowerCase.contains("payment") && lowerCase.contains("successful")) && lowerCase.contains("₹")){

            val currentTime = System.currentTimeMillis()

            if (text == lastDetectedText) return

            if(ispopupshown && currentTime - lastTriggerTime < 3000){
                return
            }

            var amountRegex = Regex("₹\\s?\\d+")
            var amountMatch = amountRegex.find(text)
            var amount = amountMatch?.value ?: return

            var parts = text.split("\n", " ")
            val index = parts.indexOfFirst { it.startsWith("₹") }

            var merchant =  parts.getOrNull(index + 1) ?: "Unknown"

            val paymentKey = "$amount-$merchant"
            if (paymentKey == lastPayment) return


            for (i in (index - 3)..(index + 3)) {
                if (i >= 0 && i < parts.size) {
                    val word = parts[i]

                    // Skip useless words
                    if (word.length > 3 &&
                        !word.contains("₹") &&
                        !word.lowercase().contains("payment") &&
                        !word.lowercase().contains("successful") &&
                        !word.lowercase().contains("split") &&
                        !word.lowercase().contains("expense") &&
                        !word.lowercase().contains("banking")&&
                        !word.lowercase().startsWith("xxxx")&&
                        !word.all(){ it.isDigit()} &&
                        parts.size > 16


                    ) {

                        merchant = word
                        break
                    }

                }
            }

            Log.d("Expence Tracker", "Amount : $amount  Merchant: $merchant")
            isProcessing = true

            parentJob?.cancel()
            parentJob = serviceScope.launch {

                delay(800)
                if(!isActive) return@launch
                try {
                    val rootNode = rootInActiveWindow ?: return@launch
                    val updatedText = getAllText(rootNode)?.lowercase() ?: return@launch

                    if (!(updatedText.contains("payment") &&
                                updatedText.contains("successful") &&
                                updatedText.contains("₹"))) {
                        return@launch
                    }

                    showOverlay(amount, merchant)
                    ispopupshown = true

                } catch (e: Exception) {
                    Log.e("Expence Tracker", "Runnable crash: ${e.message}")
                }
            }


            lastTriggerTime = currentTime
            lastDetectedText = text
            lastPayment = paymentKey

            serviceScope.launch {
                delay(600)
                isProcessing = false
            }
        }
    }

//    private fun showPopUp(amount: String, merchant: String){
//        val intent = Intent(this , ExpensePopupActivity::class.java)
//        intent.putExtra("amount", amount)
//        intent.putExtra("merchant", merchant)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//
//    }

    //--------------------------------------------------------------------------------------------


    private fun showOverlay(amount: String?, merchant: String?){


        val windowManager = getSystemService(WINDOW_SERVICE) as android.view.WindowManager

        cleanOverlay(windowManager )

        val inflater  = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.overlay_expences, null)

        val amountText = view.findViewById<TextView>(R.id.amountTextView)
        val merchantText = view.findViewById<TextView>(R.id.merchantTextView)
        val editText = view.findViewById<EditText>(R.id.detailEditText)
        val canclebutton = view.findViewById<Button>(R.id.canclebutton)
        val continuebutton = view.findViewById<Button>(R.id.continueButton)

        amountText.text = amount
        merchantText.text = merchant

        canclebutton.setOnClickListener {
            ispopupshown = false
            lastPayment = ""
            removeOverlay(windowManager)
        }

        continuebutton.setOnClickListener {
            val data  = editText.text.toString()
            ispopupshown = false
            lastPayment = ""
            Log.d("Expence Tracker: ", "amount : $amount  merchant: $merchant")
            removeOverlay(windowManager)
        }

        val pharma = WindowManager.LayoutParams(
            android.view.WindowManager.LayoutParams.WRAP_CONTENT,
            android.view.WindowManager.LayoutParams.WRAP_CONTENT,
            android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        pharma.gravity = Gravity.CENTER
        if (overView != null) return

        try {
            windowManager.addView(view, pharma)
            overView = view
        } catch (e: Exception) {
            Log.e("Expence Tracker", "Overlay error: ${e.message}")
        }


    }

    fun removeOverlay(windowManager: WindowManager) {
        try {
            overView?.let {
                windowManager.removeView(it)
                overView = null
            }
        } catch (e: Exception) {
            overView = null
        }
    }

    fun cleanOverlay(windowManager: WindowManager){
        overView?.let {
            windowManager.removeView(it)
            overView = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ServiceDebug", "Service Destroyed")
        serviceScope.cancel()
    }


}