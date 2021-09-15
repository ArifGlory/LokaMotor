package com.tapisdev.lokamotor.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.Layanan
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_service.*
import kotlinx.android.synthetic.main.activity_mendaftar_antrian.*
import java.text.SimpleDateFormat
import java.util.*


class AddServiceActivity : BaseActivity() {

    var TAG_SIMPAN = "simpanLayanan"
    lateinit var layanan : Layanan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service)
        mUserPref = UserPreference(this)


        btnSimpanLayanan.setOnClickListener {
            checkValidation()
        }
        ivBackCreateService.setOnClickListener {
            onBackPressed()
        }

    }

    fun checkValidation(){
        var namaLayanan = edNamaLayanan.text.toString()
        var harga = edHargaLayanan.text.toString()

        if (namaLayanan.equals("") || namaLayanan.length == 0){
            showErrorMessage("Anda harus mengisi nama layanan yang diiinginkan")
        }else  if (harga.equals("") || harga.length == 0){
            showErrorMessage("Anda harus mengisi harga layanan yang diiinginkan")
        }
        else {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = sdf.format(Date())

            layanan = Layanan(
                "",
                namaLayanan,
                harga.toInt(),
                1
            )
            saveLayanan()
        }
    }

    fun saveLayanan(){
        showLoading(this)

        layananRef.document().set(layanan).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                dismissLoading()
                showLongSuccessMessage("Tambah Layanan Berhasil")
                onBackPressed()
            }else{
                dismissLoading()
                showLongErrorMessage("Error, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }
}