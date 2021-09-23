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
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.Layanan
import com.tapisdev.lokamotor.model.RiwayatService
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_list_service.*
import kotlinx.android.synthetic.main.activity_riwayat_service.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListServiceActivity : BaseActivity() {

    var TAG_GET_LAYANAN = "getLayanan"
    var SELECT_LAYANAN = "selectedLayanan"
    var TAG_SIMPAN = "simpanAntrian"
    var TAG_GET_ANTRIAN = "getAntrian"
    var totalBayar = 0

    lateinit var adapter: AdapterLayanan
    lateinit var antrian : Antrian
    var listLayanan = ArrayList<Layanan>()
    var listAntrian = java.util.ArrayList<Antrian>()
    var listLayananDipilih = ArrayList<Layanan>()
    var strLayananDipilih = ArrayList<String>()

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
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val currentDate = sdf.format(Date())
                Log.d(SELECT_LAYANAN,"string list layanan dipilih :  "+strLayananDipilih.toString())

                antrian = Antrian("",
                    auth.currentUser!!.uid,
                    mUserPref.getName()!!,
                    mUserPref.getFoto()!!,
                    strLayananDipilih.toString(),
                    currentDate.toString(),
                    "waiting",
                    0,
                    totalBayar
                )
                saveAntrian(antrian)
            }
        }

        getDataLayanan()
        updateUI()
    }

    fun updateUI(){
        if (!mUserPref.getJenisUser().equals("admin")){
            btnCreateLayanan.visibility = View.GONE
        }else{
            btnKonfirmasiLayanan.visibility = View.GONE
            tvTotal.visibility = View.GONE
        }
    }

    fun saveAntrian(myAntrian : Antrian){
        showLoading(this)

        antrianRef.whereEqualTo("tanggal",myAntrian.tanggal)
            //.whereEqualTo("status","waiting")
            //.whereEqualTo("status","aktif")
            //.orderBy("nomor_antrian",Query.Direction.DESCENDING)
            .get().addOnSuccessListener { result ->
                listAntrian.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var antrian : Antrian = document.toObject(Antrian::class.java)
                    antrian.id_antrian = document.id

                    if (!antrian.status.equals("selesai")){
                        listAntrian.add(antrian)
                        Log.d("isiAntrian : ",antrian.toString())
                    }

                }

                //save to db
                pDialogLoading.setTitleText("menyimpan data..")
                if (listAntrian.size == 0){
                    myAntrian.nomor_antrian = 1
                    Log.d(TAG_SIMPAN,"antrian pertama")
                }else{
                    //get the biggest nomor antrian
                    var max = 0
                    for(i in 0 until  listAntrian.size){
                        if (i == 0){
                            max = listAntrian.get(i).nomor_antrian
                        }else{
                            if (max < listAntrian.get(i).nomor_antrian){
                                max = listAntrian.get(i).nomor_antrian
                            }
                        }
                    }

                    myAntrian.nomor_antrian = max + 1
                    Log.d(TAG_SIMPAN,"max nomor antrian = "+max)
                }

                antrianRef.document().set(myAntrian).addOnCompleteListener {
                        task ->
                    if (task.isSuccessful){
                        dismissLoading()
                        showLongSuccessMessage("Tambah Antrian Berhasil")
                        onBackPressed()
                    }else{
                        dismissLoading()
                        showLongErrorMessage("Error, coba lagi nanti ")
                        Log.d(TAG_SIMPAN,"err : "+task.exception)
                    }
                }


            }.addOnFailureListener { exception ->
                dismissLoading()
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_ANTRIAN,"err : "+exception.message)
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
        strLayananDipilih.add(layanan.id_layanan)
        countHargaTotal()

        //Log.d(SELECT_LAYANAN,"isi list layanan dipilih :  "+listLayananDipilih.toString())
        Log.d(SELECT_LAYANAN,"isi str list layanan dipilih :  "+strLayananDipilih.toString())
    }

    fun removeLayanan(layanan: Layanan){
        listLayananDipilih.remove(layanan)
        strLayananDipilih.remove(layanan.id_layanan)

        if (listLayananDipilih.size > 0){
            listLayananDipilih.sortBy { layanan : Layanan -> layanan.id_layanan }
        }

        countHargaTotal()
       // Log.d(SELECT_LAYANAN,"isi list layanan dipilih :  "+listLayananDipilih.toString())
        Log.d(SELECT_LAYANAN,"isi str list layanan dipilih :  "+strLayananDipilih.toString())
    }

    fun countHargaTotal(){
        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        for (i in 0  until listLayananDipilih.size){
            totalBayar = totalBayar + listLayananDipilih.get(i).harga_layanan
        }
        tvTotal.setText("Total : Rp. "+df.format(totalBayar))
    }

    override fun onResume() {
        super.onResume()
        getDataLayanan()
    }
}