package com.tapisdev.lokamotor.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.RiwayatService
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_detail_service.*

class AddDetailService : BaseActivity() {

    lateinit var i : Intent
    lateinit var antrian : Antrian
    lateinit var riwayatService : RiwayatService
    var TAG_SIMPAN = "simpanRiwayat"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_detail_service)
        mUserPref = UserPreference(this)

        i = intent
        antrian = i.getSerializableExtra("antrian") as Antrian

        ivBack.setOnClickListener {
            onBackPressed()
        }
        btnSelesaikan.setOnClickListener {
            checkValidation()
        }

        updateUI()
    }

    fun updateUI(){
        tvNamaUser.setText(antrian.nama_user)
      //  tvJenisLayanan.setText(antrian.jenis_layanan)
        tvNomorAntrian.setText("Nomor Antrian - "+antrian.nomor_antrian)
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
}
