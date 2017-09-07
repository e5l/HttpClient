import http.call.HttpClientCall
import http.response.makeResponse

inline suspend fun <reified T> HttpClientCall.execute(requestData: Any = Unit): T =
        request.pipeline.execute(this, requestData).let { responseData ->
            makeResponse<T>(responseData).response as? T
        } ?: error("Fail to process call: $this \n" + "Expected type: ${T::class}")
