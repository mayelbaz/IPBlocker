import IPHelper.calculateIPRange
import IPHelper.getEndIPfromStartIP
import IPHelper.parseCIDR
import IPHelper.transformIPToLong
import java.io.File

class IPBlocker {
    private val root: TrieNode = TrieNode()
    private val ranges :RangeList = RangeList()

    fun loadBlockedIPsFromFile(path: String): Result<Unit> {
        return try {
            val file = File(path)
            file.useLines { lines ->
                lines.forEach { line ->
                     handleLine(line)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            println("Error loading blocked IPs from file: ${e.message}")
            Result.failure(e)
        }
    }

    @Throws(Exception::class)
    private fun handleLine(line: String) {
        val (ipAddress, subnet) = parseCIDR(line)
        require(subnet.toInt()> 0) { "Subnet must be between 1 and 32: $line" }
        if(subnet.toInt()<16){
                val start = transformIPToLong(ipAddress)
                val end = getEndIPfromStartIP(ipAddress,subnet)
                ranges.addRange(start,end)
            } else{
                val ipSubnetList = calculateIPRange(line)
                ipSubnetList.forEach { addBlockedIPToTree(it) }
            }
        }

    private fun addBlockedIPToTree(ipAsList:List<Int>) {
        var currentNode = root
        for (ipPart in ipAsList) {
            val child = currentNode.children.getOrPut(ipPart) { TrieNode() }
            currentNode = child
        }
    }

    fun isAllowed(incomingIp:String): Boolean {
        val ipParts = incomingIp.split(".").map { it.toIntOrNull() }
        if(incomingIp.split(".").map { it.toIntOrNull() }.size!=4 || !ipParts.all { it != null } ){
            println("The incomingIp: $incomingIp is not in a correct format and therefore will not be checked, and considered not allowed")
            return false
        }
        return when(isIpInTrie(incomingIp)){
            true-> return false
            false-> !ranges.isInRange(transformIPToLong(incomingIp))
        }
    }

    private fun isIpInTrie(incomingIp:String): Boolean {
        val incomingIpParts = incomingIp.split(".").map { it.toInt() }
        var currentNode = root
        for (ipPart in incomingIpParts) {
            val child = currentNode.children[ipPart] ?: return false
            currentNode = child
        }
        return true
    }
}