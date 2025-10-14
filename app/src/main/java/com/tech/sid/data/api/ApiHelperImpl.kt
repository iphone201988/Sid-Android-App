package com.tech.sid.data.api

import com.tech.sid.base.local.SharedPrefManager
import com.google.gson.JsonObject
import com.tech.sid.CommonFunctionClass
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.ui.auth.AuthModelLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService,
    private val sharedPrefManager: SharedPrefManager
) :
    ApiHelper {

    override suspend fun apiForRawBody(
        request: HashMap<String, Any>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiForRawBody(request, url)
    }

    override suspend fun apiPostForRawBody(
        url: String,
        request: HashMap<String, Any>,
    ): Response<JsonObject> {
        return apiService.apiPostForRawBody(getTokenFromPref(), url, request)
    }

    override suspend fun apiPostNoBody(
        url: String,

        ): Response<JsonObject> {
        return apiService.apiPostNoBody(getTokenFromPref(), url)
    }

    override suspend fun apiDelete(
        url: String,

        ): Response<JsonObject> {
        return apiService.apiDelete(getTokenFromPref(), url)
    }

    override suspend fun apiForFormData(
        data: HashMap<String, Any>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiForFormData(data, url)
    }

    override suspend fun apiForFormDataPut(
        data: HashMap<String, Any>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiForFormDataPut(data, url, getTokenFromPref())
    }

    override suspend fun apiGetOutWithQuery(url: String): Response<JsonObject> {
        return apiService.apiGetOutWithQuery(url)
    }

    override suspend fun apiGetOnlyAuthToken(url: String): Response<JsonObject> {
        return apiService.apiGetOnlyAuthToken(url, getTokenFromPref())
    }

    override suspend fun apiGetWithQuery(
        data: HashMap<String, String>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiGetWithQuery(url, data)
    }

    override suspend fun apiForPostMultipart(
        url: String, map: HashMap<String, RequestBody>,
        part: MutableList<MultipartBody.Part>
    ): Response<JsonObject> {
        return apiService.apiForPostMultipart(url, getTokenFromPref(), map, part)
    }

    override suspend fun apiForPostMultipart(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?,
    ): Response<JsonObject> {
        return apiService.apiForPostMultipart(url, getTokenFromPref(), map, part)
    }

    override suspend fun apiForMultipartPut(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?
    ): Response<JsonObject> {
        return apiService.apiForMultipartPut(url, getTokenFromPref(), map, part)
    }

    override suspend fun apiPutForRawBody(
        url: String,
        map: HashMap<String, Any>,
    ): Response<JsonObject> {
        return apiService.apiPutForRawBody(url, getTokenFromPref(), map)
    }

    private fun getTokenFromPref(): String {
        if (bearer == null) {
            val rawJsonString: String = sharedPrefManager.getLoginData() ?: ""
            val cleanJsonString = if (rawJsonString.startsWith("\"") && rawJsonString.endsWith("\"")) {
                rawJsonString.substring(1, rawJsonString.length - 1).replace("\\\"", "\"")
            } else {
                rawJsonString
            }
            val jsonObject = JSONObject(cleanJsonString)
            val loginModel: AuthModelLogin? =
                BindingUtils.parseJson(jsonObject.toString())
            CommonFunctionClass.logPrint(
                tag = "BEARER_TOKEN",
                response = "${loginModel?.token}"
            )
            return "Bearer ${loginModel?.token}"
        } else {
            return "Bearer $bearer"
        }

    }

    companion object {
        var bearer: String? = null
    }

}