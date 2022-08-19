package calculator

import kotlin.system.exitProcess

val variables = mutableMapOf<String, Int>()
var assignmentProcessed = false
fun main() {
    while(true){
        val input = readln()
        when{
            input == "" -> continue
            input == "/help" -> { println("The program calculates the sum of numbers"); continue }
            input == "/exit" -> { println("Bye!"); exitProcess(0) }
            input.matches("\\s*/.*".toRegex()) -> { println("Unknown command"); continue }
            input.matches("[a-zA-Z]+".toRegex()) -> { println(validIdentifier(input)); continue }
            input.count { it == '=' } > 1 -> { println("Invalid assignment"); continue }
        }
        val isAssignment = input.matches("\\s*[a-zA-Z]+\\s*=\\s*([a-zA-Z]|[0-9])+\\s*".toRegex())
        val result = when {
            isAssignment -> assign(input.replace("\\s+".toRegex(),"").split("="))
            else -> calculate(input.split(" ").toMutableList())
        }
        if (!assignmentProcessed) println(result)
        assignmentProcessed = false
    }
}
fun validIdentifier(name: String) = if (!name.matches("\\s*[a-zA-Z]+\\s*".toRegex())) "Invalid Identifier" else getValue(name)
fun getValue(key: String): String = if (!variables.containsKey(key)) "Unknown variable" else variables[key].toString()
fun assign(values: List<String>): String {
    return try {
        if (!values[0].matches("[a-zA-Z]+".toRegex())) "Invalid identifier"
        else if (!values[1].matches("([a-zA-Z]+|\\d+)".toRegex())) "Invalid assignment"
        else {
            variables[values[0]] = values[1].toInt()
            assignmentProcessed = true
            ""
        }
    } catch (e: NumberFormatException) {
        if (variables.containsKey(values[1])) {
            variables[values[0]] = variables[values[1]]!!
            assignmentProcessed = true
            ""
        } else "Unknown variable."
    }
}
fun calculate(values: MutableList<String>): String {
    for (i in 1 until values.size step 2) { // it is a symbol
        if (!values[i].matches("[+-]+".toRegex())) return "Invalid expression"
        values[i] = if (values[i].count { it == '-' } % 2 == 0) "+" else "-"
    }
    var result: Int
    try {
        result = getNextValue(values[0])
        for (i in 2 until values.size step 2) {// it is a number
            when (values[i - 1]) {
                "+" -> result += getNextValue(values[i])
                "-" -> result -= getNextValue(values[i])
            }
        }
    } catch (e: Exception) {
        return "Invalid expression"
    }
    return result.toString()
}
fun getNextValue(key: String): Int = if (key.matches("\\d+".toRegex())) key.toInt() else validIdentifier(key).toInt()
//prints invalid expression if there is a invalid or unknown variable in an expression a  + 4r + a
