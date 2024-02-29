package model

data class ItemResponse(
    val httpStatus: Int,
    val code: Int,
    val msg: String,
    val data: List<Activity>
)

data class Activity(
    val id: Int,
    val title: String,
    val content: String,
    val startTime: String?, // 假设时间是字符串类型，如果你使用的是日期时间类型，请相应地修改
    val endTime: String?, // 同上
    val address: String,
    val fee: Double,
    val imageIds: List<Int> // 假设图片ID是整型列表，根据实际情况调整类型
)

class ItemListManager {
    companion object {
        private var INSTANCE: ItemListManager? = null
        fun getInstance(): ItemListManager {
            if (INSTANCE == null) {
                INSTANCE = ItemListManager()
            }
            return INSTANCE!!
        }
    }

    private var itemResponse: ItemResponse? = null

    fun getItemResponse(): ItemResponse {
        if (itemResponse == null) {
            val activities = listOf(
                Activity(1, "活动1", "活动1内容", null, null, "活动1地址", 12.30, listOf())
            )
            itemResponse = ItemResponse(httpStatus = 200, code = 200, msg = "OK", data = activities)
        }
        return itemResponse!!
    }

    fun setItemResponse(itemResponse: ItemResponse){
        this.itemResponse = itemResponse
    }

}
