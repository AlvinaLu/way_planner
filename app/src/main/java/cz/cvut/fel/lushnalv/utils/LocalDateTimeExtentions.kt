package cz.cvut.fel.lushnalv

import java.time.LocalDateTime

fun LocalDateTime.getHourString(): String {
    return if (this.hour < 10) {
        "0" + this.hour.toString()
    } else {
        this.hour.toString()
    }
}

fun LocalDateTime.getMinuteString(): String {
    return if (this.minute < 10) {
        "0" + this.minute.toString()
    } else {
        this.minute.toString()
    }
}



fun Long.getHourString(): String {
    return if (this == 0L) {
        ""
    } else {
        this.toString() + "h"
    }
}

fun Long.getMinuteString(): String {
    if (this == 0L) {
        return ""
    } else {
        return this.toString() + "m"
    }
}

fun Long.getHoursAndMinutesString(): String {
    if(this%60L==0L){
        return "${(this/60)}h"
    }else{
        if(this<60L){
            return "${(this)}m"
        }else{
            val h = this/60L
            val m = this-h*60L
            return  "${h}h ${m}m"
        }
    }
}

fun Int.getKmMetersString(): String {
    if(this !=0 && this%1000==0){
        return "${(this/1000)}km"
    }else{
        if(this<1000){
            return "${(this)}m"
        }else{
            val km = this/1000
            val m = this-km*1000
            return  "${km}km ${m}m"
        }
    }

}
