package com.nada.tech.listener

interface ClickListener<T> {
    fun onViewClick(id: Int, position: Int, t: T?, string: String? = null)
}