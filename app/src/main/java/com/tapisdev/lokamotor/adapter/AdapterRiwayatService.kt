package com.tapisdev.lokamotor.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.GLDebugHelper
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.tapisdev.lokamotor.model.RiwayatService
import com.tapisdev.lokamotor.model.UserPreference
import com.tapisdev.mysteam.model.UserModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_antrian.view.*
import kotlinx.android.synthetic.main.row_riwayat.view.*
import kotlinx.android.synthetic.main.row_riwayat.view.ivFotoUser
import kotlinx.android.synthetic.main.row_riwayat.view.tvNamaUser
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AdapterRiwayatService(private val list:ArrayList<Antrian>) : RecyclerView.Adapter<AdapterRiwayatService.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_riwayat,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val myDB = FirebaseFirestore.getInstance()
    val antrianRef = myDB.collection("antrian")
    lateinit var pDialogLoading : SweetAlertDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        mUserPref = UserPreference(holder.view.lineRiwayat.context)

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat
        pDialogLoading = SweetAlertDialog(holder.view.tvTanggal.context, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(list?.get(position)?.tanggal , firstApiFormat)

      //  Log.d("parseTesting", date.dayOfWeek.toString()) // prints Wednesday
       // Log.d("parseTesting", date.month.toString()) // prints August

        //holder.view.tvTanggal.text = list?.get(position)?.tanggal
        holder.view.tvTanggal.text = ""+date.dayOfMonth+"-"+date.month.toString()+"-"+date.year.toString()
        holder.view.tvNamaUser.text = list?.get(position)?.nama_user
        holder.view.tvTotalBayar.text = "Rp. "+df.format(list?.get(position)?.totalBayar)

        if (list?.get(position)?.foto_user.equals("")){
            holder.view.ivFotoUser.setImageResource(R.drawable.ic_placeholder)
        }else {
            Glide.with(holder.view.ivFotoUser.context)
                .load(list?.get(position)?.foto_user)
                .into(holder.view.ivFotoUser)
        }

        holder.view.lineRiwayat.setOnClickListener {

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