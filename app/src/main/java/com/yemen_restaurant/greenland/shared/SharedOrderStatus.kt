package com.yemen_restaurant.greenland.shared

class SharedOrderStatus {
    companion object{
        val ORDER_INWAY = "30"
        val ASSIGN_DELIVERY_MAN = "20"
        val ORDER_VIEWED = "3"
        val ORDER_PREPARED = "25"
        val ORDER_COMPLETED = "1"
        val ORDER_CENCELED = "2"
    }
}

class SharedOrderPaid {
    companion object{
        val NOT_PAID = null
        val PAID_ON_DELIVERY = "1"
        val ELECTEONIC_PAID = "2"
        val PAID_FROM_WALLET = "3"
        val PAID_IN_STORE = "4"
    }
}

class SharedOrderINRest {
    companion object{
        val DELIVERY = null
        val SAFARY = "1"
        val MAHALY = "2"
        val FAMILY = "3"
        val CAR = "4"
    }
}