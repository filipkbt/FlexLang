package com.example.empty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_pagination.*


class SelectDifficultyActivity : AppCompatActivity() {
    var googleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_difficulty)

        buttonBack.setOnClickListener {
            backHome()
        }
        buttonLogOut.setOnClickListener(){
            logOut()
        }
    }
    private fun backHome(){
        val intent = Intent(this, MainActivity::class.java)
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


    fun onSelectDifficultyClick(textview: View) {
        val difficulty = textview.getTag()

        val intent = Intent(this, SelectCategoryActivity::class.java)
        intent.putExtra("difficulty", difficulty.toString())
        startActivity(intent)
    }

}
