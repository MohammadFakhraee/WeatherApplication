package com.example.weatherapplication.usecase

interface UseCase<in Q : UseCase.RequestValues, out P: UseCase.ResponseValue> {

    /**
     * runs the request which would be handled in repository class
     * @param requestValues required values in order to run the request. A generic value which implements [RequestValues]
     * @param callback listens for the repository request updates.
     */
    suspend fun run(requestValues: Q, callback: (response: UseCaseResponseWrapper<P>, stillLoading: Boolean) -> Unit)

    /**
     * Data passed to a request
     */
    interface RequestValues

    /**
     * Data received from a request
     */
    interface ResponseValue
}