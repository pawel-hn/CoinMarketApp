package pawel.hn.coinmarketapp.model


import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("credit_count")
    val creditCount: Int,
    val elapsed: Int,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("error_message")
    val errorMessage: Any,
    val notice: Any,
    val timestamp: String
)