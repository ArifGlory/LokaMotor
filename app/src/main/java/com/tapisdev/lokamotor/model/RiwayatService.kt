package com.tapisdev.lokamotor.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RiwayatService(
    var id_riwayat : String = "",
    var id_antrian : String = "",
    var id_user : String = "",
    var nama_user : String = "",
    var foto_user : String = "",
    var jenis_layanan : String = "",
    var harga : String = "",
    var deskripsi : String = ""
) : Parcelable,java.io.Serializable