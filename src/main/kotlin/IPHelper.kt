object IPHelper {

    @Throws(Exception::class)
    fun calculateIPRange(cidr: String): List<List<Int>> {
            val (ipAddress, subnetMask) = parseCIDR(cidr)
            val ipParts = parseIPAddress(ipAddress)
            val subnetMaskLength = parseSubnetMaskLength(subnetMask,cidr)
            val subnetMaskInt = calculateSubnetMaskInt(subnetMaskLength)

            val startIpList = calculateStartIPList(ipParts, subnetMaskInt)
            val endIpList = calculateEndIPList(startIpList, subnetMaskInt)

            return getIpRangeFromStartAndEndIps(startIpList, endIpList)
    }

    fun parseCIDR(cidr: String): Pair<String, String> {
        val parts = cidr.split("/")
        require(parts.size == 2) { "Invalid CIDR format: $cidr" }
        return parts[0] to parts[1]
    }

    private fun parseIPAddress(ipAddress: String): List<Int> {
        val ipParts = ipAddress.split(".").map { it.toIntOrNull() }
        require(ipParts.size == 4 && ipParts.all { it != null }) { "Invalid IP address format: $ipAddress" }
        return ipParts.requireNoNulls()
    }

    private fun parseSubnetMaskLength(subnetMask: String, cidr: String): Int {
        return subnetMask.toIntOrNull()?.takeIf { it in 16..32 }
            ?: throw IllegalArgumentException("Invalid subnet: $subnetMask in cidr: $cidr. Subnet must be between 16 and 32")
    }

    private fun calculateSubnetMaskInt(subnetMaskLength: Int): Int {
        return (0 until 4).fold(0) { acc, i ->
                acc or (255 shl (24 - i * 8))
            } and ((0xFFFFFFFF shl (32 - subnetMaskLength)).toInt())
    }

    private fun calculateStartIPList(ipParts: List<Int>, subnetMaskInt: Int): List<Int> {
        return ipParts.mapIndexed { index, ipPart -> ipPart and (subnetMaskInt shr (24 - index * 8)) }
    }

    private fun calculateEndIPList(startIpList: List<Int>, subnetMaskInt: Int): List<Int> {
        return startIpList.mapIndexed { index, startIpPart -> startIpPart or (255 and (subnetMaskInt.inv() shr (24 - index * 8))) }

    }

    @Throws(Exception::class)
    fun getEndIPfromStartIP(ipAddress:String, subnet:String) :Long{
        val ipParts = parseIPAddress(ipAddress)
        val subnetMask = calculateSubnetMaskInt(subnet.toInt())
        val endIpAsList = calculateEndIPList(ipParts,subnetMask)
        return transformIPToLong(endIpAsList.joinToString(separator = "."))
    }

    private fun getIpRangeFromStartAndEndIps(startIpList: List<Int>, endIpList: List<Int>): List<List<Int>> {
        val ipRange = mutableListOf<List<Int>>()
        ipRange.add(startIpList)

        var currentIp = startIpList
        while (currentIp != endIpList) {
            val nextIp = currentIp.toMutableList()
            for (i in 3 downTo 0) {
                nextIp[i]++
                if (nextIp[i] <= 255) {
                    break
                } else {
                    nextIp[i] = 0
                }
            }
            ipRange.add(nextIp)
            currentIp = nextIp
        }
        return ipRange
    }

    fun transformIPToLong(ip: String): Long {
        val ipParts = ip.split(".")
        require(ipParts.size == 4 && ipParts.all { true }) { "Invalid IP address format: $ip." }
        val transformedParts = ipParts.map { String.format("%03d", it.toInt()) }
        return transformedParts.joinToString("").toLong()
    }
}

