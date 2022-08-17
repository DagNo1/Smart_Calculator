package calculator

import kotlin.system.exitProcess

fun main() {
    while(true){
        val input = readln()
        when{
            input == "" -> continue
            input == "/help" -> { println("The program calculates the sum of numbers"); continue }
            input == "/exit" -> { println("Bye!"); exitProcess(0) }
            input.matches("\\s*/.*".toRegex()) -> { println("Unknown command"); continue }
        }
        val result = calculate(input.split(" ").toMutableList())
        println(result)
    }
}
fun calculate(values: MutableList<String>): String {
    for (i in 1 until values.size step 2) { // it is a symbol
        if (!values[i].matches("[+-]+".toRegex())) return("Invalid expression")
        values[i] = if (values[i].count { it == '-' } % 2 == 0) "+" else "-"
    }
    var result: Int
    try {
        result = values[0].toInt()
        for (i in 2 until values.size step 2) {// it is a number
            when (values[i - 1]) {
                "+" -> result += values[i].toInt()
                "-" -> result -= values[i].toInt()
            }
        }
    } catch (e: Exception) {
        return("Invalid expression")
    }
    return result.toString()
}
