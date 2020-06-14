package com.example.empty

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var googleSignInClient: GoogleSignInClient? = null
    val RC_SIGN_IN = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signup_button.setOnClickListener({
            createEmailId()
        })

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        google_login_button.setOnClickListener({
            var signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        })

        email_login_button.setOnClickListener{
            loginEmail()
        }

        checkConnectivity()
    }

    override fun onResume() {
        super.onResume()
        moveNextPage()
    }

    fun moveNextPage() {
        var currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun createEmailId() {
        var email = email_edittext.text.toString()
        var password = password_edittext.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                task ->
            if(task.isSuccessful){
                tryToCreateAccount()
                moveNextPage()
            }
        }
    }

    fun loginEmail() {
        var email = email_edittext.text.toString()
        var password = password_edittext.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener{
                task ->
            if(task.isSuccessful){
                tryToCreateAccount()
                moveNextPage()
            }
        }
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
            task ->
            if(task.isSuccessful){
                tryToCreateAccount()
                moveNextPage()
}
}
}

private fun tryToCreateAccount() {
    val user = FirebaseAuth.getInstance().currentUser
    val userid = user!!.uid
    val reference = FirebaseDatabase.getInstance().reference
    val query = reference
        .child("Users")
        .orderByChild("userid")
        .equalTo(userid)
    query.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.childrenCount > 0) {
                //username found
            } else {
                var map = mutableMapOf<String, Any>()

                map["userid"] = user!!.uid
                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .push()
                    .setValue(map)
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {

        }
    })
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            var account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        }
    }

    private fun checkConnectivity(){
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if(!isConnected){
            Toast.makeText(this, "Check network connection", Toast.LENGTH_SHORT).show()
        }
    }
}
