package model

class RegisterData {
    var role : Int = 1
    var username : String = "11111"
    var password : String = "11111"

    constructor()

    constructor(role: Int, username: String, password: String) {
        this.role = role
        this.username = username
        this.password = password
    }
}