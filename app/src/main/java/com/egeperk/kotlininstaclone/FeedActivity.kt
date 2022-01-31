package com.egeperk.kotlininstaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.egeperk.kotlininstaclone.adapter.PostAdapter
import com.egeperk.kotlininstaclone.databinding.ActivityFeedBinding
import com.egeperk.kotlininstaclone.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedBinding
    private lateinit var auth : FirebaseAuth

    private lateinit var db :FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    lateinit var adapter : PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore

        postArrayList = ArrayList<Post>()

        getData()


        binding.feedRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(postArrayList)
        binding.feedRecyclerView.adapter = adapter
    }

    private fun getData() {

        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        postArrayList.clear()

                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val email = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post = Post(email, comment, downloadUrl)

                            postArrayList.add(post)


                        }

                        adapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.clone_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post) {
            val intentToUpload = Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intentToUpload)
        } else if(item.itemId == R.id.sign_out) {
            auth.signOut()
            val intentToMain = Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intentToMain)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}