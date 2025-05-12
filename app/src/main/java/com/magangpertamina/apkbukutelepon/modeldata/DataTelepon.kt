package com.magangpertamina.apkbukutelepon.modeldata

import com.google.firebase.database.Exclude
import java.io.Serializable

data class DataTelepon(
    val namaKontak: String = "",
    val noPekerja: String = "",
    val jabatan: String = "",
    val bagian: String = "",
    val alamat: String = "",
    val telpRumah: String = "",
    val telpKantor: String = "",
    @Exclude var id: String = "",  // Gunakan anotasi Exclude
): Serializable {
    constructor() : this("", "", "", "", "", "", "", "")
}

