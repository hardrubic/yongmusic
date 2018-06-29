package com.hardrubic.music.network.response

class LoginResponse : BaseResponse() {
    var account: Account? = null
    var profile: Profile? = null

    class Account {
        var id = -1L
        var userName = ""
    }

    class Profile {
        var nickname = ""
    }
}

