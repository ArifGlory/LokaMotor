package com.tapisdev.lokamotor.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.lokamotor.MainActivity
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.activity.AntrianActivity
import com.tapisdev.lokamotor.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home_pengguna.*

class HomePenggunaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_pengguna)

        lineProfil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineAntrian.setOnClickListener {
            startActivity(Intent(this, AntrianActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }
}
