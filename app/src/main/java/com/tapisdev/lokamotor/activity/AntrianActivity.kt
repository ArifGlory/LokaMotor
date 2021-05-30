package com.tapisdev.lokamotor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.adapter.AdapterAntrian
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.Antrian
import com.tapisdev.lokamotor.model.UserPreference
import kotlinx.android.synthetic.main.activity_antrian.*

class AntrianActivity : BaseActivity() {

    var TAG_GET_ANTRIAN = "getAntrian"
    lateinit var adapter: AdapterAntrian

    var listAntrian = ArrayList<Antrian>()

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

        }

        getDataAntrian()
    }

    fun getDataAntrian(){
        steamRef.get().addOnSuccessListener { result ->
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
}
