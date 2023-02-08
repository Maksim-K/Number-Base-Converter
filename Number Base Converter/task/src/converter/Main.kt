package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

// Do not delete this line

fun main() {
    Converter.consoleMenu1()
}

object Converter {
    const val promptTitleMenu1 =
        "Enter two numbers in format: {source base} {target base} (To quit type /exit)"
    const val promptTitleMenu2 =
        "Enter number in base {user source base} to convert to base {user target base} (To go back type /back)"
    var sourceBase = 10
    var targetBase = 10
    val scale = 5

    enum class Commands(val command: String) {
        TO_DECIMAL("/to"), FROM_DECIMAL("/from"),
        EXIT("/exit"), BACK("/back"),
    }

    fun consoleMenu1() {
        while (true) {
            when (val inputData = input(promptTitleMenu1)) {
                Commands.EXIT.command -> {
                    return
                }

                else -> {
                    val bases = inputData.split(" ").map { it.toInt() }
                    sourceBase = bases[0]
                    targetBase = bases[1]
                    consoleMenu2()
                }
            }
        }
    }

    fun consoleMenu2() {
        while (true) {
            when (val inputData = input(
                promptTitleMenu2
                    .replace("{user source base}", sourceBase.toString())
                    .replace("{user target base}", targetBase.toString())
            )) {
                Commands.BACK.command -> {
                    return
                }

                else -> {
                    println(
                        "Conversion result: ${
                            fromBigDecimalConvert(
                                toBigDecimalConvert(inputData, checkRadix(sourceBase)),
                                checkRadix(targetBase)
                            )
                        }"
                    )
                }
            }
        }
    }

    private fun input(message: String): String {
        print("$message ").also { return readln() }
    }

    private fun checkRadix(radix: Int) =
        if (radix > 1 && radix <= ('Z'.code - 'A'.code + 10)) {
            radix
        } else 10

    private fun toBigDecimalConvert(number: String, radix: Int): BigDecimal {
        val integer = number.substringBefore('.')
        val fractional = if (number.contains('.')) {
            number.substringAfter('.')
        } else ""

        return if (fractional.isEmpty()) {
            toDecimalConvert(integer, radix).toBigDecimal()
        } else toDecimalConvert(integer, radix).toBigDecimal() +
                toDecimalConvertFractional(fractional, radix)
    }

    private fun toDecimalConvertFractional(fractional: String, radix: Int): BigDecimal {
        var result = BigDecimal.ZERO
        fractional.reversed().forEach {
            result = (it.digitToIntExtended().toBigDecimal() + result).setScale(scale, RoundingMode.CEILING) / radix.toBigDecimal()
        }
        return result
    }

    private fun toDecimalConvert(number: String, radix: Int): BigInteger {
        var result = BigInteger.ZERO
        val isNegative = (number.first() == '-')
        val value = if (isNegative) {
            number.substring(1)
        } else if (number == "0") {
            return BigInteger.ZERO
        } else number

        value.forEach {
            result = it.digitToIntExtended().toBigInteger() + result * radix.toBigInteger()
        }

        return if (isNegative) {
            -result
        } else result
    }

    private fun fromBigDecimalConvert(number: BigDecimal, radix: Int): String {
        val integer = (number / BigDecimal.ONE)
        val fractional = number - integer
        var resultString = decimalConvert(integer.toBigInteger(), radix)

        if (fractional != BigDecimal.ZERO)
            resultString = "$resultString.${decimalFractionalConvert(fractional, radix)}"

        return if (number < BigDecimal.ZERO) "-$resultString" else resultString
    }

    private fun decimalFractionalConvert(number: BigDecimal, radix: Int): String {
        var resultString = ""
        var value = number
        var currentScale = 0

        while (true) {
            value *= radix.toBigDecimal()
            resultString = "$resultString${
                digitToChar(
                    (value / BigDecimal.ONE).toInt()
                )
            }"
            value %= BigDecimal.ONE
            if (value == BigDecimal.ZERO || ++currentScale == scale) return resultString
        }
    }

    private fun decimalConvert(number: BigInteger, radix: Int): String {
        var resultString = ""
        var value = if (number < BigInteger.ZERO) {
            -number
        } else if (number == BigInteger.ZERO) {
            return "0"
        } else number

        while (value > BigInteger.ZERO) {
            resultString = "${
                digitToChar(
                    (value % radix.toBigInteger()).toInt()
                )
            }$resultString"
            value /= radix.toBigInteger()
        }

        return if (number < BigInteger.ZERO) {
            "-$resultString"
        } else resultString
    }

    private fun digitToChar(digit: Int) = when {
        (digit > 9) -> ('A'.code + digit - 10).toChar()
        else -> digit.digitToChar()
    }

    private fun Char.digitToIntExtended() = when (this.uppercaseChar()) {
        in 'A'..'Z' -> this.uppercaseChar().code - 'A'.code + 10
        else -> this.digitToInt()
    }

}