package org.swag.vyom

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform