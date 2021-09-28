package com.tapisdev.lokamotor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.adapter.AdapterAntrian
import com.tapisdev.lokamotor.adapter.AdapterRiwayatService
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.RiwayatService
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_riwayat_service.*

class RiwayatServiceActivity : BaseActivity() {

    var TAG_GET_ANTRIAN = "getAntrian"
    lateinit var adapter: AdapterRiwayatService

    var listAntrian = ArrayList<Antrian>()
    var listRiwayat = ArrayList<RiwayatService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_service)
        mUserPref = UserPreference(this)
        adapter = AdapterRiwayatService(listAntrian)
        rvRiwayat.setHasFixedSize(true)
        rvRiwayat.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvRiwayat.adapter = adapter

        ivBackRiwayat.setOnClickListener {
            onBackPressed()
        }


        getDataRiwayat()
    }

    fun getDataRiwayat(){

        if (mUserPref.getJenisUser().equals("pengguna")){
            antrianRef.whereEqualTo("id_user",auth.currentUser?.uid)
                .whereEqualTo("status","selesai")
                .orderBy("tanggal", Query.Direction.DESCENDING)
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
                    animation_view_riwayat.setAnimation(R.raw.empty_box)
                    animation_view_riwayat.playAnimation()
                    animation_view_riwayat.loop(false)
                }else{
                    animation_view_riwayat.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_ANTRIAN,"err : "+exception.message)
            }
        }else{
            antrianRef.whereEqualTo("status","selesai")
                .orderBy("tanggal", Query.Direction.DESCENDING)
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
                        animation_view_riwayat.setAnimation(R.raw.empty_box)
                        animation_view_riwayat.playAnimation()
                        animation_view_riwayat.loop(false)
                    }else{
                        animation_view_riwayat.visibility = View.INVISIBLE
                    }
                    adapter.notifyDataSetChanged()

                }.addOnFailureListener { exception ->
                    showErrorMessage("terjadi kesalahan : "+exception.message)
                    Log.d(TAG_GET_ANTRIAN,"err : "+exception.message)
                }
        }

    }
}
