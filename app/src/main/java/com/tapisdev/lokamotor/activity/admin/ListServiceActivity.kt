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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ListServiceActivity : BaseActivity() {

    var TAG_GET_LAYANAN = "getLayanan"
    var SELECT_LAYANAN = "selectedLayanan"
    lateinit var adapter: AdapterLayanan

    var listLayanan = ArrayList<Layanan>()
    var listLayananDipilih = ArrayList<Layanan>()

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
        btnKonfirmasiLayanan.setOnClickListener {

            if (listLayananDipilih.size == 0){
                showErrorMessage("Anda belum memilih layanan")
            }else{
                listLayananDipilih.sortBy { layanan : Layanan -> layanan.id_layanan }
                Log.d(SELECT_LAYANAN,"sorted list layanan dipilih :  "+listLayananDipilih.toString())
            }
        }

        getDataLayanan()
        updateUI()
    }

    fun updateUI(){
        if (!mUserPref.getJenisUser().equals("admin")){
            btnCreateLayanan.visibility = View.GONE
        }
    }

    fun getDataLayanan(){

        if (mUserPref.getJenisUser().equals("pengguna")){
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

    fun addLayanan(layanan : Layanan){
        listLayananDipilih.add(layanan)
        countHargaTotal()

        Log.d(SELECT_LAYANAN,"isi list layanan dipilih :  "+listLayananDipilih.toString())
    }

    fun removeLayanan(layanan: Layanan){
        listLayananDipilih.remove(layanan)
        if (listLayananDipilih.size > 0){
            listLayananDipilih.sortBy { layanan : Layanan -> layanan.id_layanan }
        }

        countHargaTotal()
        Log.d(SELECT_LAYANAN,"isi list layanan dipilih :  "+listLayananDipilih.toString())
    }

    fun countHargaTotal(){
        var total = 0
        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        for (i in 0  until listLayananDipilih.size){
            total = total + listLayananDipilih.get(i).harga_layanan
        }
        tvTotal.setText("Total : Rp. "+df.format(total))
    }

    override fun onResume() {
        super.onResume()
        getDataLayanan()
    }
}