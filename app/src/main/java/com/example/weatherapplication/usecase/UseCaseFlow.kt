package com.example.weatherapplication.usecase

import kotlinx.coroutines.flow.Flow

interface UseCaseFlow<in Q : UseCaseFlow.RequestValues, out P : UseCaseFlow.ResponseValue> {

    fun run(requestValue: Q): Flow<P>

    /**
     * Data passed to a request
     */
    interface RequestValues

    /**
     * Data received from a request
     */
    interface ResponseValue
}