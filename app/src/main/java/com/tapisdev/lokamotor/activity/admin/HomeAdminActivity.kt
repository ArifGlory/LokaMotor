package com.tapisdev.lokamotor.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.activity.AntrianActivity
import com.tapisdev.lokamotor.activity.RiwayatServiceActivity
import com.tapisdev.lokamotor.activity.pengguna.ProfilActivity
import com.tapisdev.lokamotor.adapter.AdapterAntrian
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_antrian.*
import kotlinx.android.synthetic.main.activity_home_admin.*
import kotlinx.android.synthetic.main.activity_home_admin.rvAntrian
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeAdminActivity : BaseActivity() {

    var TAG_GET_ANTRIAN = "getAntrian"
    lateinit var adapter: AdapterAntrian
    var listAntrian = ArrayList<Antrian>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)
        mUserPref = UserPreference(this)
        adapter = AdapterAntrian(listAntrian)
        rvAntrian.setHasFixedSize(true)
        rvAntrian.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvAntrian.adapter = adapter

        lineProfil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineAntrian.setOnClickListener {
            startActivity(Intent(this, AntrianActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatServiceActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineLayanan.setOnClickListener {
            startActivity(Intent(this, ListServiceActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        getDataAntrian()
    }

    fun getDataAntrian(){
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())

        antrianRef.whereEqualTo("tanggal",currentDate.toString())
            .orderBy("nomor_antrian", Query.Direction.ASCENDING)
            .limit(5)
            .get().addOnSuccessListener { result ->
                listAntrian.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var antrian : Antrian = document.toObject(Antrian::class.java)
                    antrian.id_antrian = document.id

                    if (!antrian.status.equals("selesai")){
                        listAntrian.add(antrian)
                    }

                }
                if (listAntrian.size == 0){
                    /*animation_view_antrian.setAnimation(R.raw.empty_box)
                    animation_view_antrian.playAnimation()
                    animation_view_antrian.loop(false)*/
                    tvNoAntrian.visibility = View.VISIBLE
                    av_antrian.visibility = View.INVISIBLE
                }else{
                    av_antrian.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_ANTRIAN,"err : "+exception.message)
            }
    }

    override fun onResume() {
        super.onResume()
        getDataAntrian()
    }
}
