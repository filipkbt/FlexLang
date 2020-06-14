package com.example.empty

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_word.*
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_pagination.*
import java.util.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth


class WordActivity : AppCompatActivity() {
    lateinit var mTTS: TextToSpeech
    private val REQUEST_CODE_SPEECH_INPUT = 100
    var difficulty: String = ""
    var category: String = ""
    var counter: Int = 1
    var numberOfWords: Int = 1
    var googleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)
        difficulty = intent.getStringExtra("difficulty")
        category = intent.getStringExtra("category")
        Toast.makeText(
            this,
            difficulty + ' ' + category,
            Toast.LENGTH_SHORT
        ).show()

        mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                mTTS.language = Locale.UK
            }
        })


        buttonSpeak.setOnClickListener{
            speak()
        }

        buttonNextWord.setOnClickListener{
            if( counter >= numberOfWords - 1){
                Toast.makeText(this, "Jesteś na końcu listy słówek.", Toast.LENGTH_LONG).show()
            }else{
                counter += 1
                loadWord()
            }
        }

        buttonPreviousWord.setOnClickListener{
            if( counter <= 1){
                Toast.makeText(this, "Jesteś na początku listy słówek.", Toast.LENGTH_LONG).show()
            }else{
                counter -= 1
                loadWord()
            }
        }

        loadWord()
        buttonBack.setOnClickListener {
            backActivity()
        }
        buttonLogOut.setOnClickListener(){
            logOut()
        }
    }
    private fun backActivity(){
        val intent = Intent(this, SelectCategoryActivity::class.java)
        intent.putExtra("difficulty", difficulty)
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

    private fun speak(){
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK)
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak")

        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        }catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadWord() {
        FirebaseDatabase.getInstance().reference
            .child("images")
            .child(difficulty)
            .child(category)
            .child(difficulty + "-" + category + "-" + counter)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    numberOfWords = p0.childrenCount.toInt()
                    var map = p0.value as Map<String, Any>
                    imageForeignNameTextview.text = map["title"].toString()
                    imagePolishNameTextview.text = map["polishTitle"].toString()

                    Glide.with(imageView).load(map["image"].toString()).into(imageView);

                    buttonHear.setOnClickListener{
                        val toSpeak = map["title"].toString()
                        mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data){
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if(result[0] == imageForeignNameTextview.text.toString().toLowerCase()){
                       Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
