package calculator

import kotlin.system.exitProcess

fun main() {
    while(true){
        val input = readln()
        if (isCommand(input)) continue
        var sum = 0
        for (element in input.split(" ")) sum += element.toInt()
        println(sum)
    }
}
fun isCommand(input: String): Boolean {
    if (input == "") return true
    if (input == "/help") {
        println("The program calculates the sum of numbers")
        return true
    }
    if (input == "/exit") {
        println("Bye!")
        exitProcess(0)
    }
    return false
}