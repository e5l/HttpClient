import http.call.HttpCall
import http.request.makeRequest
import http.response.makeResponse

inline suspend fun <reified T> execute(call: HttpCall): T = call.makeRequest().let { (request, response) ->
    call.makeResponse<T>(request, response).response as? T
            ?: error("Fail to process call: $call \n" + "Expected type: ${T::class}")
}
