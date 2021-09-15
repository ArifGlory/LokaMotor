package com.tapisdev.lokamotor.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.adapter.AdapterLayanan
import com.tapisdev.lokamotor.adapter.AdapterRiwayatService
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Layanan
import com.tapisdev.lokamotor.model.RiwayatService
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_list_service.*
import kotlinx.android.synthetic.main.activity_riwayat_service.*

class ListServiceActivity : BaseActivity() {

    var TAG_GET_LAYANAN = "getLayanan"
    lateinit var adapter: AdapterLayanan

    var listLayanan = ArrayList<Layanan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_service)
        mUserPref = UserPreference(this)
        adapter = AdapterLayanan(listLayanan)
        rvLayanan.setHasFixedSize(true)
        rvLayanan.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvLayanan.adapter = adapter

        ivBackLayanan.setOnClickListener {
            onBackPressed()
        }
        btnCreateLayanan.setOnClickListener {
            startActivity(Intent(this, AddServiceActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        getDataLayanan()
    }

    fun getDataLayanan(){

        if (mUserPref.getJenisUser().equals("pengguna")){

        }else{
            layananRef.whereEqualTo("active",1)
                .get().addOnSuccessListener { result ->
                listLayanan.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var layanan : Layanan = document.toObject(Layanan::class.java)
                    layanan.id_layanan = document.id

                    listLayanan.add(layanan)

                }
                if (listLayanan.size == 0){
                    animation_view_layanan.setAnimation(R.raw.empty_box)
                    animation_view_layanan.playAnimation()
                    animation_view_layanan.loop(false)
                }else{
                    animation_view_layanan.visibility = View.INVISIBLE
                }
                adapter.notifyDataSetChanged()

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_LAYANAN,"err : "+exception.message)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        getDataLayanan()
    }
}