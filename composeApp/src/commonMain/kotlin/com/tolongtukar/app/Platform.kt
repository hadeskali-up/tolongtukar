package com.tolongtukar.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
