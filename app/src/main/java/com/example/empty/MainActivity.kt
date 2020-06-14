package com.example.empty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var languages = arrayOf("English", "Polish")
    var spinner: Spinner? = null
    var googleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner = this.spinnerLanguages

       val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(aa)

        val btnStart = findViewById<Button>(R.id.btnStart)

        btnStart.setOnClickListener {
        val intent = Intent(this, SelectDifficultyActivity::class.java)
            intent.putExtra("language", spinner!!.getSelectedItem().toString())
            startActivity(intent)
        }

        viewMoreText.setOnClickListener{
            val intent = Intent(this, ReadMoreActivity::class.java)
            startActivity(intent)
        }

        logOutText.setOnClickListener(){
            logOut()
        }
    }
    private fun logOut(){
        FirebaseAuth.getInstance().signOut()
        googleSignInClient?.signOut()
        finish()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Log out.", Toast.LENGTH_LONG).show()
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
