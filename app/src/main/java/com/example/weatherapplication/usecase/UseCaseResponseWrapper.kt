package com.example.weatherapplication.usecase

/**
 * Wrapper interface which stores data layer response or data layer error in the specified classes.
 * These sealed classes are used in mid layer of application:
 * 1. Presenter layer: UseCase classes
 * 2. View layer: ViewModel classes
 * @param T type of returned response value. Classes only can return this type, not get it in their functions.
 */
sealed interface UseCaseResponseWrapper<out T> {
    data class Success<out T>(val result: T) : UseCaseResponseWrapper<T>
    data class Error(val t: Throwable) : UseCaseResponseWrapper<Nothing>
}