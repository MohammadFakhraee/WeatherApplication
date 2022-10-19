package com.example.weatherapplication.data.source

import com.example.weatherapplication.data.DataSourceResponseWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton class used to safely call api or local requests.
 */
@Singleton
class SafeDataSourceCalls @Inject constructor() {

    /**
     * Takes request and safely launches the request in a try/catch block. Th
     * @param dispatcher thread which the class wants to call request on.
     * @param dataSourceCall requested lambda to safely call. It should be a suspend function because it's going to call a suspend function
     * from api or local data source.
     * @return [DataSourceResponseWrapper.Success] if the request completed successfully
     * @return [DataSourceResponseWrapper.Error] if the request faced an error
     */
    suspend fun <T> run(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        dataSourceCall: suspend CoroutineScope.() -> T
    ): DataSourceResponseWrapper<T> =
        withContext(dispatcher) {
            try {
                DataSourceResponseWrapper.Success(dataSourceCall.invoke(this))
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                DataSourceResponseWrapper.Error(throwable)
            }
        }
}

