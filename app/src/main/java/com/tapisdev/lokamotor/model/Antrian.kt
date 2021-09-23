package com.tapisdev.lokamotor.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Antrian(
    var id_antrian : String = "",
    var id_user : String = "",
    var nama_user : String = "",
    var foto_user : String = "",
    var list_layanan : String = "",
    var tanggal : String = "",
    var status : String = "",
    var nomor_antrian : Int = 0,
    var totalBayar : Int = 0
) : Parcelable,java.io.Serializable