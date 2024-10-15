package com.receipts.receipt_sharing.domain.recipes

import com.receipts.receipt_sharing.R
import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    var name : String,
    val amount : Long,
    val measureType : Measure
)

@Serializable
enum class Measure(val shortName : Int){
    Litres(R.string.litre_name),
    Millilitres(R.string.millilitre_name),
    Gram(R.string.gram_name),
    Kilogram(R.string.kilogram_name),
}
