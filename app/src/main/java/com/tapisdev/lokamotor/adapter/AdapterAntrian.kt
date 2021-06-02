package com.tapisdev.lokamotor.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.GLDebugHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.activity.AntrianActivity
import com.tapisdev.lokamotor.activity.SplashActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.UserPreference
import com.tapisdev.mysteam.model.UserModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_antrian.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterAntrian(private val list:ArrayList<Antrian>) : RecyclerView.Adapter<AdapterAntrian.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_antrian,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val myDB = FirebaseFirestore.getInstance()
    val antrianRef = myDB.collection("antrian")
    lateinit var pDialogLoading : SweetAlertDialog

    override fun onBindViewHolder(holder: Holder, position: Int) {
        mUserPref = UserPreference(holder.view.lineAntrian.context)

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat
        pDialogLoading = SweetAlertDialog(holder.view.tvNamaUser.context, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        holder.view.tvNamaUser.text = list?.get(position)?.nama_user
        holder.view.tvJenisLayanan.text =list?.get(position)?.jenis_layanan
        holder.view.tvNomorAntrian.text = ""+list?.get(position)?.nomor_antrian

        if (list?.get(position)?.status.equals("aktif")){
            holder.view.rlBackground.setBackgroundColor(ContextCompat.getColor(holder.view.lineAntrian.context, R.color.light_blue_500))

            holder.view.tvNamaUser.setTextColor(ContextCompat.getColor(holder.view.lineAntrian.context,R.color.white))
            holder.view.tvJenisLayanan.setTextColor(ContextCompat.getColor(holder.view.lineAntrian.context,R.color.white))
            holder.view.tvNomorAntrian.setTextColor(ContextCompat.getColor(holder.view.lineAntrian.context,R.color.white))
        }


        if (list?.get(position)?.foto_user.equals("")){
            holder.view.ivFotoUser.setImageResource(R.drawable.ic_placeholder)
        }else {
            Glide.with(holder.view.ivFotoUser.context)
                .load(list?.get(position)?.foto_user)
                .into(holder.view.ivFotoUser)
        }


        holder.view.lineAntrian.setOnLongClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())

            if (mUserPref.getJenisUser().equals("admin")){
                if (position != 0){
                    Toasty.error(holder.view.lineAntrian.context, "Antrian ini tidak berada pada posisi pertama", Toast.LENGTH_SHORT, true).show()
                }else{
                    if (list?.get(position)?.status.equals("waiting")){
                        SweetAlertDialog(holder.view.lineAntrian.context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Aktifkan antrian ini ?")
                            .setContentText("Antrian akan dimulai")
                            .setConfirmText("Ya")
                            .setConfirmClickListener { sDialog ->
                                sDialog.dismissWithAnimation()
                                pDialogLoading.show()

                                antrianRef.document(list?.get(position)?.id_antrian).update("status","aktif").addOnCompleteListener { task ->
                                    pDialogLoading.dismiss()
                                    if (task.isSuccessful){
                                        Toasty.success(holder.view.lineAntrian.context, "Antrian Aktif", Toast.LENGTH_SHORT, true).show()
                                        val i = Intent(holder.view.lineAntrian.context,AntrianActivity::class.java)
                                        holder.view.lineAntrian.context.startActivity(i)
                                    }else{
                                        Toasty.error(holder.view.lineAntrian.context, "terjadi kesalahan : "+task.exception, Toast.LENGTH_SHORT, true).show()
                                        Log.d("aktivasiAntrian","err : "+task.exception)
                                    }
                                }

                            }
                            .setCancelButton(
                                "Tidak"
                            ) { sDialog -> sDialog.dismissWithAnimation() }
                            .show()
                    }
                }

            }



            true
        }




    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}