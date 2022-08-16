package calculator

fun main() {
    while(true){
        val input = readln()
        if (input == "") continue
        if (input == "/exit") {
            println("Bye!")
            return
        }
        var sum = 0
        for (element in input.split(" ")) sum += element.toInt()
        println(sum)
    }
}
