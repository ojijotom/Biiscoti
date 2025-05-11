package com.aphfiwiwi.biiscoti.navigation

// NavRoutes.kt
const val ROUT_HOME = "home"
const val ROUT_SPLASH = "splash"
const val ROUT_ITEM = "item"
const val ROUT_THRIFT = "thrift"
const val ROUT_BARKERY = "bakery"
const val ROUT_CONTACT = "contact"
const val ROUT_HAIR = "hair"
const val ROUT_GROCERY = "grocery"
const val ROUT_HORTICULTURE = "horticulture"
const val ROUT_JEWELRY = "jewelry"
const val ROUT_PROFILE = "profile"
const val ROUT_RESTAURANT = "restaurant"
const val ROUT_SEARCH = "search"
const val ROUT_ABOUT = "about"



//auth

const val ROUT_REGISTER = "Register"
const val ROUT_LOGIN = "Login"

//Products

const val ROUT_ADD_PRODUCT = "add_product"
const val ROUT_PRODUCT_LIST = "product_list"
const val ROUT_EDIT_PRODUCT = "edit_product/{productId}"

// ✅ Helper function for navigation
fun editProductRoute(productId: Int) = "edit_product/$productId"
