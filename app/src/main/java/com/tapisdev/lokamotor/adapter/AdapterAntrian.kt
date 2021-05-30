package com.tapisdev.lokamotor.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.tapisdev.lokamotor.R
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

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        holder.view.tvNamaUser.text = list?.get(position)?.nama_user
        holder.view.tvJenisLayanan.text =list?.get(position)?.jenis_layanan

        if (list?.get(position)?.status.equals("aktif")){
            holder.view.rlBackground.setBackgroundColor(ContextCompat.getColor(holder.view.lineAntrian.context, R.color.light_blue_500))
        }


        if (list?.get(position)?.foto_user.equals("")){
            holder.view.ivFotoUser.setImageResource(R.drawable.ic_placeholder)
        }else {
            Glide.with(holder.view.ivFotoUser.context)
                .load(list?.get(position)?.foto_user)
                .into(holder.view.ivFotoUser)
        }


        holder.view.lineAntrian.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            /*val i = Intent(holder.view.lineAntrian.context, DetailSteamActivity::class.java)
            i.putExtra("steam",list.get(position) as Serializable)
            holder.view.lineAntrian.context.startActivity(i)*/
        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}