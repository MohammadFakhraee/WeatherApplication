package com.example.weatherapplication.data

/**
 * Wrapper interface which stores api response or api error in the specified classes.
 * They are medians between api (or local) requests.
 * These sealed classes are used in deep layer of application:
 * 1. Data layer: repository classes
 * 2. Presenter layer: UseCase classes
 * @param T type of returned response value. Classes only can return this type, not get it in their functions.
 */
sealed interface DataSourceResponseWrapper<out T> {
    data class Success<out T>(val result: T) : DataSourceResponseWrapper<T>
    data class Error(val throwable: Throwable) : DataSourceResponseWrapper<Nothing>
}