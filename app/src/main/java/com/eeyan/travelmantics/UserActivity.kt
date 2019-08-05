package com.eeyan.travelmantics

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject

class UserActivity : AppCompatActivity() {

    private var myList: RecyclerView? = null
    private var destinationList: List<ImageDestinations>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        initUI()
    }


    private fun initUI(){

        myList = findViewById(R.id.destination_images)
        destinationList = ArrayList()
        myList?.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        myList?.itemAnimator = DefaultItemAnimator()
        val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        fireStore.collection("travel_destinations")
            .get()
            .addOnSuccessListener { querySnapshot ->

                querySnapshot.forEach {
                    try{
                        val jsonObject  = JSONObject(it.data)
                        val allDestinations = ImageDestinations(
                            jsonObject.getString("destination_image_url"),
                            jsonObject.getString("destination_price"),
                            jsonObject.getString("destination_title"),
                            jsonObject.getString("destination_description")
                        )
                        (destinationList as ArrayList<ImageDestinations>).add(allDestinations)
                        val myAdapter = TravelDestinations(this@UserActivity,
                            destinationList as ArrayList<ImageDestinations>
                        )
                        myList?.adapter = myAdapter
                    }catch (
                        exception: JSONException
                    ){

                    }
                }

            }.addOnFailureListener {
                Log.i("Exception: ",it.toString())
                throw it
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemID = item.itemId
        when(itemID){
            R.id.action_uplaod -> {
                startActivity(Intent(this,AdminActivity::class.java))
                finish()
            }
        }
        return true
    }

    class TravelDestinations(private val context: Context, private val list: List<ImageDestinations>) :
        RecyclerView.Adapter<TravelDestinations.TravelHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelHolder {
        return TravelHolder(LayoutInflater.from(context).inflate(R.layout.single_item_list,parent,false))
        }

        override fun getItemCount(): Int {
        return list.size
        }

        override fun onBindViewHolder(holder: TravelHolder, position: Int) {
        val destinations: ImageDestinations = list[position]
        val storageReference = FirebaseStorage.getInstance()
        storageReference.reference.child(destinations.destinationImage)
            .downloadUrl.addOnSuccessListener {
            Glide.with(context).load(it.toString()).into(holder.img_single_item)
        }.addOnFailureListener {}
            holder.txt_dest_name.text = destinations.destinationName
            holder.txt_dest_price.text = destinations.destinationPrice
            holder.txt_dest_desc.text = destinations.destinationDescription


        }

        class TravelHolder(view: View) : RecyclerView.ViewHolder(view){
        val txt_dest_name = view.findViewById<AppCompatTextView>(R.id.txt_destination_venue)
        val txt_dest_desc = view.findViewById<AppCompatTextView>(R.id.txt_destination_description)
        val txt_dest_price = view.findViewById<AppCompatTextView>(R.id.txt_destination_price)
        val img_single_item = view.findViewById<AppCompatImageView>(R.id.img_single_item)
        }
    }

    data class ImageDestinations(val destinationImage: String,
                                 val destinationPrice: String,
                                 val destinationName: String,
                                 val destinationDescription: String)

}
