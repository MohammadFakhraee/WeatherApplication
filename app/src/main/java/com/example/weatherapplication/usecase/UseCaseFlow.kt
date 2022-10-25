package com.example.weatherapplication.usecase

import kotlinx.coroutines.flow.Flow

interface UseCaseFlow<in Q : UseCaseFlow.RequestValues, out P : UseCaseFlow.ResponseValue> {

    /**
     * runs the request which would be handled in repository class
     * @param requestValue required values in order to run the request. A generic value which implements [RequestValues]
     * @return Flow<P> for the repository request Updates or Errors
     */
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