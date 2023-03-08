package cz.cvut.fel.lushnalv.utils

import com.google.android.gms.maps.model.LatLng

fun LatLng.getStringForRequest(): String{
    return "${this.latitude},${this.longitude}"
}