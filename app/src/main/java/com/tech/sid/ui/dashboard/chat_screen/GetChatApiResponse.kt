package com.tech.sid.ui.dashboard.chat_screen

data class GetChatApiResponse(
    var `data`: Data?,
    var message: String?,
    var success: Boolean?
) {
    data class Data(
        var chat: List<Chat?>?,
        var simulationData: SimulationData?
    ) {
        data class Chat(
            var __v: Int?,
            var _id: String?,
            var createdAt: String?,
            var messages: List<Message>?,
            var simulationId: String?,
            var updatedAt: String?,
            var userId: String?
        ) {
            data class Message(
                var from: String?,
                var message: String?
            )
        }

        data class SimulationData(
            var __v: Int?,
            var _id: String?,
            var chatId: String?,
            var createdAt: String?,
            var customMomentText: String?,
            var customScenarioText: String?,
            var momentId: String?,
            var momentTitle: String?,
            var relation: String?,
            var responseStyle: String?,
            var scenarioId: String?,
            var scenarioTitle: String?,
            var updatedAt: String?,
            var userId: String?
        )
    }
}