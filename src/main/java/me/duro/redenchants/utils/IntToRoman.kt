package me.duro.redenchants.utils

fun intToRoman(num: Int): String = buildString {
    var remain = num
    val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    val numerals = listOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")

    values.indices.forEach {
        while (remain >= values[it]) {
            append(numerals[it])
            remain -= values[it]
        }
    }
}