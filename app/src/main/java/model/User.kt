package model


object User  {
      private var id : Int = 0
      private var password : String = ""
      private var username : String = ""
      private var avatarId : Int = 0
      private var roleId : Int= 1
      private var isDelete : Int = 0

      fun getId(): Int {
            return id
      }

      fun setId(id : Int){
            this.id = id
      }

      fun getPassword(): String {
            return password
      }

      fun setPassword(password : String){
            this.password = password
      }

      fun getUsername(): String {
            return username
      }

      fun setUser(username : String){
            this.username = username
      }


      fun getAvatarId(): Int{
            return avatarId
      }

      fun setAvatarId(avatarId : Int){
            this.avatarId = avatarId
      }
}