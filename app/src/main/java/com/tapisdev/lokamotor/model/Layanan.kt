package com.tapisdev.lokamotor.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Layanan(
    var id_layanan : String = "",
    var nama_layanan : String = "",
    var harga_layanan : Int = 0,
    var active : Int = 1
) : Parcelable,java.io.Serializable