package com.example.empty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_pagination.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlinx.android.synthetic.main.activity_select_category.*


class SelectCategoryActivity : AppCompatActivity() {
    var difficulty = ""
    var googleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_category)
        this.difficulty = intent.getStringExtra("difficulty")

        buttonBack.setOnClickListener {
            backDifficulty()
        }
        buttonLogOut.setOnClickListener(){
            logOut()
        }

        FirebaseDatabase.getInstance().reference
            .child("images")
            .child(this.difficulty)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    animalsCountText.text = dataSnapshot.child("animals").childrenCount.toString()
                    familyCountText.text = dataSnapshot.child("family").childrenCount.toString()
                    plantsCountText.text = dataSnapshot.child("plants").childrenCount.toString()
                    sportCountText.text = dataSnapshot.child("sport").childrenCount.toString()
                    weatherCountText.text = dataSnapshot.child("weather").childrenCount.toString()
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }
    private fun backDifficulty(){
        val intent = Intent(this, SelectDifficultyActivity::class.java)
        startActivity(intent)
    }
    private fun logOut(){
        FirebaseAuth.getInstance().signOut()
        googleSignInClient?.signOut()
        finish()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Log out.", Toast.LENGTH_LONG).show()
    }

    fun onSelectCategoryClick(textview: View) {
        val category = textview.getTag()

        val intent = Intent(this, WordActivity::class.java)
        intent.putExtra("category", category.toString())
        intent.putExtra("difficulty", this.difficulty)
        startActivity(intent)
    }
}
