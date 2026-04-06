package com.example.expancetracker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.provider.Settings
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class ExpensePopupActivity: AppCompatActivity()  {

//    private var overView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        val amount = intent.getStringExtra("amount")
//        val merchant = intent.getStringExtra("merchant")
//
//        if (!Settings.canDrawOverlays(this)) {
//            val intent = Intent(
//                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                Uri.parse("package:$packageName")
//            )
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            return
//        }
//
////        showBottomSheet(amount, merchant)
//        showOverlay(amount, merchant)
//        finish()
    }


//
//    private fun showBottomSheet(amount: String?, merchant: String?){
//
//        val dialog = BottomSheetDialog(this)
//        val view = layoutInflater.inflate(R.layout.overlay_expences, null)
//        dialog.setContentView(view)
//
//        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//
////        val Bottomsheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
////        Bottomsheet?.layoutParams?.height  = ViewGroup.LayoutParams.MATCH_PARENT
//
//        val amountText = view.findViewById<TextView>(R.id.amountTextView)
//        val merchantText = view.findViewById<TextView>(R.id.merchantTextView)
//        val editText = view.findViewById<EditText>(R.id.detailEditText)
//        val canclebutton = view.findViewById<Button>(R.id.canclebutton)
//        val continuebutton = view.findViewById<Button>(R.id.continueButton)
//
//        amountText.text = amount ?: "0"
//        merchantText.text = merchant ?: "unknown"
//
//        canclebutton.setOnClickListener {
//            dialog.dismiss()
//            finish()
//        }
//
//        continuebutton.setOnClickListener {
//            val purpose = editText.text.toString()
//            Log.d("expence tracker: ", "amount : $amount merchant: $merchant")
//            dialog.dismiss()
//            finish()
//        }
//
//        dialog.setOnDismissListener { finish() }
//        dialog.show()
//
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        ExpanceAccessibilityService.resetPopupFlag()
//
//    }


}