package com.tapisdev.lokamotor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.Query
import com.tapisdev.lokamotor.MainActivity
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.activity.admin.AddDetailService
import com.tapisdev.lokamotor.activity.pengguna.MendaftarAntrianActivity
import com.tapisdev.lokamotor.adapter.AdapterAntrian
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_antrian.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AntrianActivity : BaseActivity() {

    var TAG_GET_ANTRIAN = "getAntrian"
    var TAG_GET_ANTRIAN_AKTIF = "getAntrianAktif"
    lateinit var adapter: AdapterAntrian

    var listAntrian = ArrayList<Antrian>()
    var listAntrianAktif = ArrayList<Antrian>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antrian)
        mUserPref = UserPreference(this)
        adapter = AdapterAntrian(listAntrian)
        rvAntrian.setHasFixedSize(true)
        rvAntrian.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvAntrian.adapter = adapter


        ivBack.setOnClickListener {
            onBackPressed()
        }
        btnCreate.setOnClickListener {
            val i = Intent(applicationContext, MendaftarAntrianActivity::class.java)
            startActivity(i)
        }
        ivCurrentQueue.setOnClickListener {
            if (mUserPref.getJenisUser().equals("admin")){
                if(listAntrianAktif.size > 0){
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Selesaikan Antrian Ini ?")
                        .setContentText("Antrian diselesaikan, dan input detail Service")
                        .setConfirmText("Ya")
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()

                            val i = Intent(this,AddDetailService::class.java)
                            i.putExtra("antrian",listAntrianAktif.get(0) as Serializable)
                            startActivity(i)

                        }
                        .setCancelButton(
                            "Tidak"
                        ) { sDialog -> sDialog.dismissWithAnimation() }
                        .show()
                }
            }
        }

        updateUI()
        getDataAntrian()
    }

    fun updateUI(){
        if (mUserPref.getJenisUser().equals("admin")){
            btnCreate.visibility = View.INVISIBLE
        }
    }

    fun getDataAntrian(){
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())

        antrianRef.whereEqualTo("tanggal",currentDate.toString())
            .orderBy("nomor_antrian",Query.Direction.ASCENDING)
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
                animation_view_antrian.setAnimation(R.raw.empty_box)
                animation_view_antrian.playAnimation()
                animation_view_antrian.loop(false)
            }else{
                animation_view_antrian.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_ANTRIAN,"err : "+exception.message)
        }
    }

    fun getDataCurrentAntrian(){
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())

        antrianRef.whereEqualTo("tanggal",currentDate.toString())
            .get().addOnSuccessListener { result ->
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                listAntrianAktif.clear()
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var antrian : Antrian = document.toObject(Antrian::class.java)
                    antrian.id_antrian = document.id

                    if (antrian.status.equals("aktif")){
                        listAntrianAktif.add(antrian)
                    }

                }
                if (listAntrianAktif.size == 0){
                    currentQueue.setText("--")
                }else{
                    currentQueue.setText(""+listAntrianAktif.get(0).nomor_antrian)
                }

            }.addOnFailureListener { exception ->
                showErrorMessage("terjadi kesalahan currentQueue : "+exception.message)
                Log.d(TAG_GET_ANTRIAN_AKTIF,"err : "+exception.message)
            }
    }

    override fun onResume() {
        super.onResume()
        getDataAntrian()
        getDataCurrentAntrian()
    }
}
