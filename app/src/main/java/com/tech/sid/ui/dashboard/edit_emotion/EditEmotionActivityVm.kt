package com.tech.sid.ui.dashboard.edit_emotion

import com.google.gson.JsonObject
import com.tech.sid.CommonFunctionClass
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.Resource
import com.tech.sid.base.utils.event.SingleRequestEvent
import com.tech.sid.data.api.ApiHelper
import com.tech.sid.data.api.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditEmotionActivityVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {
    val observeCommon = SingleRequestEvent<JsonObject>()
    fun getProfileApi() {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                val response = apiHelper.apiGetOnlyAuthToken(Constants.GET_PROFILE)
                if (response.isSuccessful && response.body() != null) {
                    observeCommon.postValue(
                        Resource.success(
                            Constants.GET_PROFILE,
                            response.body()
                        )
                    )
                } else if (response.code() == Constants.UN_AUTHORISED_CODE || Constants.UN_AUTHORISED_STRING == CommonFunctionClass.jsonMessage(
                        response.errorBody()
                    )
                ) {
                    observeCommon.postValue(
                        Resource.un_authorize(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                } else {
                    observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                }

            } catch (e: java.lang.Exception) {
                observeCommon.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }


    fun getTagsApi() {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                val response = apiHelper.apiGetOnlyAuthToken(Constants.GET_TAGS)
                if (response.isSuccessful && response.body() != null) {
                    observeCommon.postValue(
                        Resource.success(
                            Constants.GET_TAGS,
                            response.body()
                        )
                    )
                } else if (response.code() == Constants.UN_AUTHORISED_CODE || Constants.UN_AUTHORISED_STRING == CommonFunctionClass.jsonMessage(
                        response.errorBody()
                    )
                ) {
                    observeCommon.postValue(
                        Resource.un_authorize(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                } else {
                    observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                }

            } catch (e: java.lang.Exception) {
                observeCommon.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }

    fun postOnboardingApi(data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                val response = apiHelper.apiPostForRawBody(Constants.POST_ONBOARDING_API, data)
                if (response.isSuccessful && response.body() != null) {
                    observeCommon.postValue(
                        Resource.success(
                            Constants.POST_ONBOARDING_API,
                            response.body()
                        )
                    )
                } else if (response.code() == Constants.UN_AUTHORISED_CODE || Constants.UN_AUTHORISED_STRING == CommonFunctionClass.jsonMessage(
                        response.errorBody()
                    )
                ) {
                    observeCommon.postValue(
                        Resource.un_authorize(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                } else {
                    observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                }
            } catch (e: java.lang.Exception) {
                observeCommon.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }

}