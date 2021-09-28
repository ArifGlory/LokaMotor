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
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.Layanan
import com.tapisdev.lokamotor.model.RiwayatService
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_detail_service.*
import kotlinx.android.synthetic.main.activity_add_detail_service.rvLayanan
import kotlinx.android.synthetic.main.activity_list_service.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailServiceActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var antrian : Antrian
    lateinit var riwayatService : RiwayatService
    var TAG_SIMPAN = "simpanRiwayat"
    var TAG_GET_LAYANAN = "getLayanan"
    var listLayanan = ArrayList<Layanan>()
    var strLayananDipilih = ArrayList<String>()
    lateinit var adapter: AdapterLayanan


    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = sdf.format(Date())
    val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
    val df = nf as DecimalFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_detail_service)
        mUserPref = UserPreference(this)
        adapter = AdapterLayanan(listLayanan)
        rvLayanan.setHasFixedSize(true)
        rvLayanan.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvLayanan.adapter = adapter

        i = intent
        antrian = i.getSerializableExtra("antrian") as Antrian

        ivBack.setOnClickListener {
            onBackPressed()
        }
        btnToggleAntrian.setOnClickListener {
            if (antrian.status.equals("aktif")){
                if (antrian.tanggal.equals(currentDate.toString())){

                }
            }else{

            }
        }

        updateUI()
        getDataLayanan()
    }


    fun updateUI(){
        if (antrian.status.equals("aktif")){
            if (antrian.tanggal.equals(currentDate.toString())){
                tvNamaUser.setText(antrian.nama_user)
                textInputDeskripsi.visibility = View.GONE
                edHarga.setText("Rp. "+df.format(antrian.totalBayar))
                tvNomorAntrian.setText("Nomor Antrian - "+antrian.nomor_antrian)
                btnToggleAntrian.setText("Selesaikan Antrian")
            }else{
                btnToggleAntrian.setText("Antrian Sudah Tidak Berlaku")
                btnToggleAntrian.isEnabled = false

                tvNamaUser.setText(antrian.nama_user)
                textInputDeskripsi.visibility = View.GONE
                edHarga.setText("Rp. "+df.format(antrian.totalBayar))
                edHarga.isEnabled = false
                tvNomorAntrian.setText("Nomor Antrian - "+antrian.nomor_antrian)
            }

        }else{
            tvNamaUser.setText(antrian.nama_user)
            textInputDeskripsi.visibility = View.GONE
            edHarga.setText("Rp. "+df.format(antrian.totalBayar))
            tvNomorAntrian.setText("Nomor Antrian - "+antrian.nomor_antrian)
            btnToggleAntrian.setText("Aktifkan Antrian")
        }

    }

    fun checkValidation(){
        var getHarga = edHarga.text.toString()
        var getDeksirpsi = edDeskripsi.text.toString()

        if (getHarga.equals("") || getHarga.length == 0){
            showErrorMessage("Total Harga Belum diisi")
        }else if (getDeksirpsi.equals("") || getDeksirpsi.length == 0){
            showErrorMessage("Komentar/Dekskripsi Belum diisi")
        }else{
           /* riwayatService = RiwayatService("",
                antrian.id_antrian,
                antrian.id_user,
                antrian.nama_user,
                antrian.foto_user,
                antrian.jenis_layanan,
                getHarga,
                 getDeksirpsi)*/

            saveRiwayat()
        }
    }

    fun saveRiwayat(){
        showLoading(this)

        riwayatRef.document().set(riwayatService).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                antrianRef.document(antrian.id_antrian).update("status","selesai").addOnCompleteListener { task ->
                    dismissLoading()
                    if (task.isSuccessful){
                        showLongSuccessMessage("Simpan Riwayat Service Berhasil")
                        onBackPressed()
                    }else{
                        showLongErrorMessage("terjadi kesalahan : "+task.exception)
                        Log.d("aktivasiAntrian","err : "+task.exception)
                    }
                }

            }else{
                dismissLoading()
                showLongErrorMessage("Error, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }

    fun getDataLayanan(){
        var strSelected = antrian.list_layanan.substring(1,antrian.list_layanan.length - 1 )
        var lists = strSelected.split(",")

        for (i in 0 until  lists.size){
            Log.d("isiSelected:","isi nya :"+ lists.get(i).trim())
            var temp = lists.get(i).trim()
            strLayananDipilih.add(temp)
        }
        Log.d("isiSelected:","hasil "+ lists.toString())
        showLoading(this)

        layananRef.whereEqualTo("active",1)
            .get().addOnSuccessListener { result ->
                listLayanan.clear()
                dismissLoading()
                for (document in result){
                    var layanan : Layanan = document.toObject(Layanan::class.java)
                    layanan.id_layanan = document.id

                    if (strLayananDipilih.contains(layanan.id_layanan)){
                        listLayanan.add(layanan)
                    }
                }
                adapter.notifyDataSetChanged()


            }.addOnFailureListener { exception ->
                dismissLoading()
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_LAYANAN,"err : "+exception.message)
            }

    }
}