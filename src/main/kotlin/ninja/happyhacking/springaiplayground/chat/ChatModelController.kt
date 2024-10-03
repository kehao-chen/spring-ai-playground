package ninja.happyhacking.springaiplayground.chat

import ninja.happyhacking.springaiplayground.embedding.DataRetrievalService
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux


@RestController
class ChatClientController @Autowired constructor(
    private val chatModel: ChatModel,
    private val dataRetrievalService: DataRetrievalService
) {
    private val askPromptBlueprint = """
        Provide a response to the query while strictly referring to the provided context:
        {context}
        
        Query:
        {query}
        
        When providing the answer, make sure to include a relevant portion of the context to support the response. 
        If you don't have any answer from the context provided, simply say:
        I'm sorry, I don't have the information you are looking for.
    """.trimIndent()

    @GetMapping("/ai/generate")
    fun generate(
        @RequestParam(
            value = "message",
            defaultValue = "Tell me a joke"
        ) message: String?
    ): Map<*, *> {
        return java.util.Map.of("generation", chatModel.call(message))
    }

    @GetMapping("/ai/generateStream")
    fun generateStream(
        @RequestParam(
            value = "message",
            defaultValue = "Tell me a joke"
        ) message: String?
    ): Flux<ChatResponse> {
        val prompt = Prompt(UserMessage(message))
        return chatModel.stream(prompt)
    }

    @GetMapping("/ai/ask")
    fun ask(
        @RequestParam(
            value = "message",
            defaultValue = "Tell me a joke"
        ) message: String?
    ): Map<*, *> {
        val context = this.dataRetrievalService.searchData(message)
        val promptTemplate = PromptTemplate(askPromptBlueprint)
        val prompt = promptTemplate.create(mapOf("context" to context, "query" to message))
        return java.util.Map.of("answer", chatModel.call(prompt))
    }
}
