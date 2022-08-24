package calculator

import kotlin.math.pow
import kotlin.system.exitProcess

val variables = mutableMapOf<String, Int>()
var assignmentProcessed = false
val symbols = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2, "^" to 3, "(" to 0 )
fun main() {
    while(true){
        val input = readln()
        when{
            input == "" -> continue
            input == "/help" -> { println("The program calculates the sum of numbers"); continue }
            input == "/exit" -> { println("Bye!"); exitProcess(0) }
            input.matches("\\s*/.*".toRegex()) -> { println("Unknown command"); continue }
            input.matches("\\s*[a-zA-Z]+\\s*".toRegex()) -> {
                println(validIdentifier(input.replace("\\s+".toRegex(),"")))
                continue
            }
            input.matches("[-+]?\\d+".toRegex()) -> { println(input.replace("[+]".toRegex(),"")); continue }
            input.count { it == '=' } > 1 -> { println("Invalid assignment"); continue }
            input.count { it == '(' }  != input.count { it == ')' } -> { println("Invalid expression"); continue }
        }
        val isAssignment = input.contains("=")
        val regex = "(?<=op)|(?=op)".replace("op", "[-+*/()]").toRegex()
        val result = when {
            isAssignment -> assign(input.replace("\\s+".toRegex(),"").split("="))
            else -> calculate(input.split(regex).toMutableList())
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
        else if (!values[1].matches("[+-]?([a-zA-Z]+|\\d+)".toRegex())) "Invalid assignment"
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
fun calculate(input: MutableList<String>): String {
    var values = mutableListOf<String>()
    input.forEach { values.add(it.replace("\\s+".toRegex(),"")) }
    values = values.filter{ it != "" }.toMutableList()
    var removed = 0
    var i = 1;
    do{
        while (true) {
            when {
                values[i] == "*" && values[i + 1] == "*" -> return "Invalid expression"
                values[i] == "/" && values[i + 1] == "/" -> return "Invalid expression"
                values[i] == "+" && values[i + 1] == "+" -> values.removeAt(i)
                values[i] == "+" && values[i + 1] == "-" -> values.removeAt(i)
                values[i] == "-" && values[i + 1] == "+" -> values.removeAt(i + 1)
                values[i] == "-" && values[i + 1] == "-" -> { values.removeAt(i + 1); values[i] = "+" }
                else -> break
            }
            removed++
        }
        i++
    } while (i < values.lastIndex)
    for (i in values.indices){
        if (!values[i].matches("([+-]+|[*/^]|\\(|\\)|\\w+|\\d+)".toRegex())) return "Invalid expression"
        if (values[i].matches("[+-]+".toRegex()))
            values[i] = if (values[i].count { it == '-' } % 2 == 0) "+" else "-"
        if (values[i].matches("[a-zA-Z]+".toRegex())) values[i] = getNextValue(values[i])
    }
    return postfix2Answer(infix2Postfix(values))
}
fun getNextValue(key: String): String = if (key.matches("\\d+".toRegex())) key else validIdentifier(key)
//prints invalid expression if there is a invalid or unknown variable in an expression a  + 4r + a
fun infix2Postfix (values: MutableList<String>): MutableList<String> {
    var top = -1
    val postfix = mutableListOf<String>()
    val stack = mutableListOf<String>()
    // Scanning each character of str from left to right
    for(i in values.indices) {
        if(values[i] == "(") { stack.add("("); top++ }
        else if(values[i] == ")") {
            // Pop all the elements until it reaches '('
            // and print each element before it is pop
            while (stack[top] != "(") { postfix.add(stack[top]); stack.removeAt(top); top-- }
            stack.removeAt(top); top-- // this is to remove '('
        }
        // if Operator
        else if(symbols.containsKey(values[i])) {
            // the operator to be pushed on to the top of
            // the stack should have the highest precedence
            while(top != -1 && symbols[values[i]]!! <= symbols[stack[top]]!!) { postfix.add(stack[top]); stack.removeAt(top); top-- }
            stack.add(values[i]); top++
        }
        // if operand
        else postfix.add(values[i])
    }
    // pop out the remaining operators on the stack
    while(top != -1) { postfix.add(stack[top]); top-- }
    return postfix
}
fun postfix2Answer (values: MutableList<String>): String {
    var top = -1
    val stack = mutableListOf<Int>()
    for(i in values.indices) {
        if (values[i].matches("[+-]?\\d+".toRegex())) { stack.add(values[i].toInt()); top++ }
        else if ( symbols.containsKey(values[i])) {
            stack[top - 1] = when(values[i]) {
                "+" -> stack[top] + stack[top - 1]
                "-" -> stack[top - 1] - stack[top]
                "*" -> stack[top] * stack[top - 1]
                "/" -> stack[top - 1] / stack[top]
                "^" -> stack[top].toDouble().pow(stack[top - 1]).toInt()
                else -> exitProcess(0)
            }
            stack.removeAt(top)
            top--
        }
    }
    return stack[0].toString()
}
