package cz.cvut.fel.lushnalv.models.google

import com.fasterxml.jackson.annotation.JsonProperty

data class PlusCode(
    @JsonProperty("compound_code")
    val compoundCode:String,
    @JsonProperty("global_code")
    val globalCode:String
)
