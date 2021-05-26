package com.tapisdev.lokamotor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.tapisdev.lokamotor.activity.RegisterActivity
import com.tapisdev.lokamotor.activity.SplashActivity
import com.tapisdev.lokamotor.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    var TAG_LOGIN = "login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        keDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        cirLoginButton.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getEmail = editTextEmail.text.toString()
        var getPass  = editTextPassword.text.toString()

        if (getEmail.equals("") || getEmail.length == 0){
            showErrorMessage("Email harus diisi")
        }else if (getPass.equals("") || getPass.length == 0){
            showErrorMessage("Password harus diisi")
        }else{
            signIn(getEmail,getPass)
        }
    }

    fun signIn(email : String,pass : String){
        showLoading(this)
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, OnCompleteListener { task ->
            if(task.isSuccessful){
                var userId = auth.currentUser?.uid
                Log.d(TAG_LOGIN,"user ID : "+userId)


                startActivity(Intent(this, SplashActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)


            }else{
                dismissLoading()
                showErrorMessage("Password / Email salah")
            }
        })
    }
}
