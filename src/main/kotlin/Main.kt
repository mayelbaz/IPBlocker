import kotlin.system.exitProcess


fun main() {
    val blocker = IPBlocker()
    val cidrListPath :String = "src/main/resources/cidr_list.txt"
    val result = blocker.loadBlockedIPsFromFile(cidrListPath)
    if(result.isFailure){
        println("Loading of the CIDR list was unsuccessful, program is stopping.")
        exitProcess(0);
    }
    println(blocker.isAllowed("1.2.3.4")) //true
    println(blocker.isAllowed("10.0.255.255")) //false
    println(blocker.isAllowed("10.255.1.1")) //true
    println(blocker.isAllowed("7.255.1.1")) //false

}

