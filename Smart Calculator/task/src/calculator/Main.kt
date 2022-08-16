package calculator

import kotlin.system.exitProcess

fun main() {
    while(true){
        val input = readln()
        when(input) {
            "" -> continue
            "/help" -> { println("The program calculates the sum of numbers"); continue }
            "/exit" -> { println("Bye!"); exitProcess(0) }
        }
        val result = calculate(input.split(" ").toMutableList())
        println(result)
    }
}
fun calculate(values: MutableList<String>): Int {
    var result = values[0].toInt()
    for (i in 1 until values.size step 2) { // it is a symbol
            values[i] = if (values[i].count { it == '-' } % 2 == 0) "+" else "-"
    }
    for (i in 2 until values.size step 2) {// it is a number
        when (values[i - 1]) {
            "+" -> result += values[i].toInt()
            "-" -> result -= values[i].toInt()
        }
    }
    return result
}
