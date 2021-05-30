package com.tapisdev.lokamotor.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Antrian(
    var id_antrian : String = "",
    var id_user : String = "",
    var nama_user : String = "",
    var foto_user : String = "",
    var jenis_layanan : String = "",
    var tanggal : String = "",
    var status : String = "",
    var nomor_antrian : Int = 0
) : Parcelable,java.io.Serializable