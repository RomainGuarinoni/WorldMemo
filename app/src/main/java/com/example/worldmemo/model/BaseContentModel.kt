package com.example.worldmemo.model

abstract class BaseContentModel {
    abstract val id: String
    abstract val country: String
    abstract val countryCode: String
    abstract val path: String
    abstract val createdDate: String
}