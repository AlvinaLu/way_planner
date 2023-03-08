package cz.cvut.fel.lushnalv.utils

import com.fasterxml.jackson.databind.ObjectMapper
import retrofit2.HttpException


import com.google.gson.Gson


fun HttpException.getErrorResponse(): ErrorResponse {
    try {
        val errorBody = this.response()?.errorBody()?.string()
        return Gson().fromJson(errorBody, ErrorResponse::class.java)
    }catch (e: Exception){
        return ErrorResponse()
    }
}

data class ErrorResponse(
    val errorCode: String = "UNKNOWN",
    val errorMessage: String = "Unknown error!"){

    override fun toString(): String {
        return "ErrorResponse(errorCode=$errorCode, errorMessage=$errorMessage)"
    }
}



