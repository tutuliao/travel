package model

data class ItemCollectedResponse(
    val httpStatus: Int,
    val code: Int,
    val msg: String,
    val data: List<ActivityCollected>
)

data class ActivityCollected(
    val id: Int,
    val title: String,
    val content: String,
    val startTime: String?,
    val endTime: String?,
    val address: String,
    val fee: Double,
    val image: String
)

class ItemCollectedListManager{
    companion object {
        private var INSTANCE: ItemCollectedListManager? = null
        fun getInstance(): ItemCollectedListManager {
            if (INSTANCE == null) {
                INSTANCE = ItemCollectedListManager()
            }
            return INSTANCE!!
        }
    }

    private var itemCollectedResponse : ItemCollectedResponse ? = null

    fun getItemCollectedResponse(): ItemCollectedResponse? {
        if(itemCollectedResponse==null){
           val ActivityCollected = listOf(
               ActivityCollected(1, "活动1", "活动1内容", null, null, "活动1地址", 12.30, "")
           )
            itemCollectedResponse = ItemCollectedResponse(200,200,"OK",ActivityCollected)
        }
        return itemCollectedResponse
    }

    fun setItemCollectedResponse(itemCollectedResponse: ItemCollectedResponse){
        this.itemCollectedResponse = itemCollectedResponse
    }
}