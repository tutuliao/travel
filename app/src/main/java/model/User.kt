package model

data class UserResponse(
      val httpStatus: Int,
      val code: Int,
      val msg: String,
      val data: data
)

data class data(
      val token: String,
      val id: Int,
      var username: String,
      val avatar: Int,
      val roleId: Int
)
class UserManager {

      private var userResponse: UserResponse? = null

      companion object {
            private var INSTANCE: UserManager? = null
            fun getInstance(): UserManager {
                  if (INSTANCE == null) {
                        INSTANCE = UserManager()
                  }
                  return INSTANCE!!
            }
      }

      fun getLoginResponse(): UserResponse? = userResponse

      fun setLoginResponse(newUserResponse: UserResponse) {
            userResponse = newUserResponse
      }

      fun setUsername(username: String){
            userResponse?.data?.username = username
      }

}

