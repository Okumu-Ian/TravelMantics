package com.eeyan.travelmantics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        findViewById<AppCompatButton>(R.id.btn_save_credentials).setOnClickListener {
            createUser(it)
        }
    }

    private fun createUser(view: View){
        val snackbar: Snackbar = Snackbar.make(view,"Please Wait..",Snackbar.LENGTH_INDEFINITE)
        snackbar.show()
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(
            findViewById<TextInputEditText>(R.id.edt_email_register).text.toString(),
            findViewById<TextInputEditText>(R.id.edt_password_register).text.toString()
        ).addOnCompleteListener {
         if(it.isSuccessful){
             FirebaseFirestore.getInstance().collection("users").add(
                 hashMapOf("user_id" to auth.currentUser?.email,
                     "user_full_name" to findViewById<TextInputEditText>(R.id.edt_first_last_register).text.toString())
             ).addOnCompleteListener { result ->
                 if(result.isSuccessful){
                     snackbar.dismiss()
                     startActivity(Intent(this@SignUpActivity,AdminActivity::class.java))
                     finish()
                 }else{
                     snackbar.dismiss()
                 }
             }
         } else{
             snackbar.dismiss()
         }
        }
    }
}
