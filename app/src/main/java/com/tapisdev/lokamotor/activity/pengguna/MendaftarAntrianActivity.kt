package com.tapisdev.lokamotor.activity.pengguna

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.Query
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_antrian.*
import kotlinx.android.synthetic.main.activity_mendaftar_antrian.*
import java.text.SimpleDateFormat
import java.util.*

class MendaftarAntrianActivity : BaseActivity() {

    var TAG_SIMPAN = "simpanAntrian"
    lateinit var antrian : Antrian

    var TAG_GET_ANTRIAN = "getAntrian"
    var listAntrian = ArrayList<Antrian>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mendaftar_antrian)
        mUserPref = UserPreference(this)


        btnDaftarAntrian.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getLayanan = edJenisLayanan.text.toString()

        if (getLayanan.equals("") || getLayanan.length == 0){
           showErrorMessage("Anda harus mengisi jenis layanan yang diiinginkan")
        }
        else {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = sdf.format(Date())

            antrian = Antrian("",
                auth.currentUser!!.uid,
                mUserPref.getName()!!,
                mUserPref.getFoto()!!,
                getLayanan,
                currentDate.toString(),
                "waiting",
                0)
            saveAntrian(antrian)
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
}
