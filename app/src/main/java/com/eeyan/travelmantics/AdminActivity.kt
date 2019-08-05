package com.eeyan.travelmantics

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.util.*


class AdminActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? =null
    private var storageReference: StorageReference? = null
    private var fireStore: FirebaseFirestore? = null
    private var edtTitle: TextInputEditText? = null
    private var edtPrice: TextInputEditText? = null
    private var edtDescription: TextInputEditText? = null
    private var btnUploadPhoto: AppCompatButton? = null
    private var imgUploadDestination: AppCompatImageView? = null
    private val avatar = 101
    private var myUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        initUI()
    }
    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, AdminActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
    private fun initUI(){
        edtTitle = findViewById(R.id.edt_admin_destination_name)
        edtPrice = findViewById(R.id.edt_admin_destination_price)
        edtDescription = findViewById(R.id.edt_admin_destination_description)
        btnUploadPhoto = findViewById(R.id.btn_img_destination)
        imgUploadDestination = findViewById(R.id.img_destination)

        //initialize all firebase components
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        fireStore = FirebaseFirestore.getInstance()

        btnUploadPhoto?.setOnClickListener {
            checkPermission()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_admin,menu)
        return true
    }

    private fun getValues(){
        val title = edtTitle?.text.toString()
        val price = edtPrice?.text.toString()
        val desc = edtDescription?.text.toString()
        val imageUrl = uploadToStorage(myUri!!)
        val sendHashMap = hashMapOf(
            "destination_title" to title,
            "destination_price" to price,
            "destination_description" to desc,
            "destination_image_url" to imageUrl
        )
        fireStore?.collection("travel_destinations")
            ?.add(sendHashMap)
            ?.addOnSuccessListener {
                Toast.makeText(this,"Success", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@AdminActivity,UserActivity::class.java))
                finish()
            }?.
                addOnFailureListener {
                    Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show()
                }
    }

    fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this@AdminActivity,Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@AdminActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),102)
        }else{
            pickImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 102){
            pickImage()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, avatar)
    }

    private fun uploadToStorage(uri: Uri): String{
        val randomURL = "images/destinations/"+ UUID.randomUUID().toString()
        val reference = storageReference?.child(randomURL)
        reference?.putFile(uri)?.addOnSuccessListener {

        }?.addOnFailureListener{

        }?.addOnCompleteListener{

        }
        return randomURL
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == avatar && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }else{
                myUri = data.data
                Glide.with(this@AdminActivity).load(myUri).into(findViewById(R.id.img_destination))
            }

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val item = item.itemId
        when(item){
            R.id.action_save -> {
                getValues()
            }
            R.id.action_travel -> {
                startActivity(Intent(this@AdminActivity,UserActivity::class.java))
            }
        }
        return true
    }
}
