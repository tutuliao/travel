package model


data class ItemDetailResponse(
    val httpStatus: Int,
    val code: Int,
    val msg: String,
    val data: ItemDetail
)
data class ItemDetail(
    val id: Int,
    val title: String,
    val content: String,
    val startTime: String?, // 时间可能为null，因此使用可空类型
    val endTime: String?,
    val address: String,
    val fee: Double,
    val imageIds: List<Int> // 假设 imageIds 是整型列表，即使列表为空
)

class ItemDetailManager {
      private var itemDetailResponse:ItemDetailResponse = ItemDetailResponse(200,200,"", data = ItemDetail(1,"","","","","",1.0,
          listOf()
      )
    )

    companion object{
        private var INSTANCE : ItemDetailManager? = null
        fun getInstance() : ItemDetailManager{
            if(INSTANCE==null){
                INSTANCE = ItemDetailManager()
            }
            return INSTANCE!!
        }
    }

    fun getItemDetailResponse(): ItemDetailResponse{
        return itemDetailResponse
    }

    fun setItemDetailResponse(itemDetailResponse: ItemDetailResponse){
        this.itemDetailResponse = itemDetailResponse
    }
}