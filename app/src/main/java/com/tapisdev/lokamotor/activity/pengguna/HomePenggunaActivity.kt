package com.tapisdev.lokamotor.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.tapisdev.lokamotor.MainActivity
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.activity.AntrianActivity
import com.tapisdev.lokamotor.activity.RiwayatServiceActivity
import com.tapisdev.lokamotor.adapter.AdapterRiwayatService
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.RiwayatService
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_home_pengguna.*
import kotlinx.android.synthetic.main.activity_home_pengguna.rvRiwayat
import kotlinx.android.synthetic.main.activity_riwayat_service.*

class HomePenggunaActivity : BaseActivity() {

    var TAG_GET_RIWAYAT = "getRiwayat"
    lateinit var adapter: AdapterRiwayatService

    var listAntrian = ArrayList<Antrian>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_pengguna)
        mUserPref = UserPreference(this)
        adapter = AdapterRiwayatService(listAntrian)
        rvRiwayat.setHasFixedSize(true)
        rvRiwayat.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvRiwayat.adapter = adapter

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

       // getDataRiwayat()
    }

    fun getDataRiwayat(){
        antrianRef.whereEqualTo("id_user",auth.currentUser?.uid)
            .whereEqualTo("status","selesai")
            .orderBy("tanggal", Query.Direction.DESCENDING)
           .limit(4)
            .get().addOnSuccessListener { result ->
                listAntrian.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var antrian : Antrian = document.toObject(Antrian::class.java)
                    antrian.id_antrian = document.id
                    listAntrian.add(antrian)

                }
                if (listAntrian.size == 0){
                    av_riwayat.setAnimation(R.raw.empty_box)
                    av_riwayat.playAnimation()
                    av_riwayat.loop(false)
                }else{
                    av_riwayat.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_RIWAYAT,"err : "+exception.message)
            }
    }

    override fun onResume() {
        super.onResume()
        getDataRiwayat()
    }
}
