package cz.cvut.fel.lushnalv.utils

import java.time.LocalDateTime

fun Int.getDistance() : String{
    return if(this < 499){
        this.toString() + " m"
    }else if( this < 999){
        (this/1000.00).toString() + " km"
    }else{
        (this/1000).toString() + " km"
    }
}

fun Int.getHoursOrMinutesWithZero() : String{
    return if(this < 10){
        "0" + this.toString()
    }else{
        this.toString()
    }
}

