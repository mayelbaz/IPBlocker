class RangeList {
    private val ranges: MutableList<Pair<Long, Long>> = mutableListOf()

    fun addRange(start: Long, end: Long) {
        if (start > end) {
            throw IllegalArgumentException("Invalid range: start value ($start) is greater than end value ($end).")
        }
        ranges.add(start to end)
    }

    fun isInRange(value: Long): Boolean {
        for (range in ranges) {
            if (value in range.first..range.second) {
                return true
            }
        }
        return false
    }

    fun getRanges(): List<Pair<Long, Long>> {
        return ranges.toList()
    }
}
