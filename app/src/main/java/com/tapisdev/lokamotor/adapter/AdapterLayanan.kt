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
import com.tapisdev.lokamotor.activity.admin.DetailServiceActivity
import com.tapisdev.lokamotor.activity.admin.ListServiceActivity
import com.tapisdev.lokamotor.model.Layanan
import com.tapisdev.lokamotor.model.UserPreference
import com.tapisdev.mysteam.model.UserModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_layanan.view.*
import kotlinx.android.synthetic.main.row_riwayat.view.*
import kotlinx.android.synthetic.main.row_riwayat.view.lineRiwayat
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterLayanan(private val list:ArrayList<Layanan>) : RecyclerView.Adapter<AdapterLayanan.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_layanan,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val myDB = FirebaseFirestore.getInstance()
    val layananRef = myDB.collection("layanan")
    lateinit var pDialogLoading : SweetAlertDialog
    var listLayananDipilih = ArrayList<Layanan>()

    override fun onBindViewHolder(holder: Holder, position: Int) {
        mUserPref = UserPreference(holder.view.lineLayanan.context)

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat
        pDialogLoading = SweetAlertDialog(holder.view.tvNamaLayanan.context, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)


        holder.view.tvNamaLayanan.text = list?.get(position)?.nama_layanan
        holder.view.tvHargaLayanan.text = "Rp. "+df.format(list?.get(position)?.harga_layanan)
        if (mUserPref.getJenisUser().equals("admin")){
            holder.view.cbLayanan.visibility = View.INVISIBLE
        }
        if (holder.view.lineLayanan.context is DetailServiceActivity){
            if ((holder.view.lineLayanan.context as DetailServiceActivity).antrian.status.equals("selesai")){
                holder.view.cbLayanan.isEnabled = false
                holder.view.cbLayanan.visibility = View.GONE
            }
        }

        holder.view.lineLayanan.setOnLongClickListener {

            if (holder.view.lineLayanan.context is ListServiceActivity){
                if (mUserPref.getJenisUser().equals("admin")){
                    SweetAlertDialog(holder.view.lineLayanan.context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin menghapus ini ?")
                        .setContentText("Data yang sudah dihapus tidak bisa dikembalikan")
                        .setConfirmText("Ya")
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()
                            pDialogLoading.show()
                            layananRef.document(list?.get(position)?.id_layanan).update("active",0).addOnSuccessListener {
                                pDialogLoading.dismiss()
                                Toasty.success(holder.view.lineLayanan.context, "Data berhasil dihapus", Toast.LENGTH_LONG, true).show()

                                if (holder.view.lineLayanan.context is ListServiceActivity){
                                    (holder.view.lineLayanan.context as ListServiceActivity).getDataLayanan()
                                }

                                Log.d("deleteDoc", "DocumentSnapshot successfully deleted!")
                            }.addOnFailureListener {
                                    e ->
                                pDialogLoading.dismiss()
                                Toasty.error(holder.view.lineLayanan.context, "terjadi kesalahan "+e, Toast.LENGTH_LONG, true).show()
                                Log.w("deleteDoc", "Error deleting document", e)
                            }

                        }
                        .setCancelButton(
                            "Tidak"
                        ) { sDialog -> sDialog.dismissWithAnimation() }
                        .show()
                }
            }


            return@setOnLongClickListener true
        }
        holder.view.cbLayanan.setOnCheckedChangeListener { compoundButton, b ->
            Log.d("layanan"," checkbox : "+b)
            var layanan  = list?.get(position)

            if (b){
                if (holder.view.lineLayanan.context is ListServiceActivity){
                    (holder.view.lineLayanan.context as ListServiceActivity).addLayanan(layanan)
                }
            }else{
                if (holder.view.lineLayanan.context is ListServiceActivity){
                    (holder.view.lineLayanan.context as ListServiceActivity).removeLayanan(layanan)
                }
            }

        }


    }

    fun convertDate(tanggal : String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd")
        //val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val output = formatter.format(parser.parse(tanggal))

        return output
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}