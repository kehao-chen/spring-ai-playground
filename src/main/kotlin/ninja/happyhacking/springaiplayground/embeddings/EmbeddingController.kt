package ninja.happyhacking.springaiplayground.embeddings

import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class EmbeddingController @Autowired constructor(
    private val embeddingModel: EmbeddingModel,
    private val vectorStore: VectorStore
) {
    @GetMapping("/ai/embedding")
    fun embed(
        @RequestParam(
            value = "message",
            defaultValue = "Tell me a joke"
        ) message: String
    ): Map<*, *> {
        val embeddingResponse: EmbeddingResponse = embeddingModel.embedForResponse(listOf(message))
        return java.util.Map.of("embedding", embeddingResponse)
    }

    @GetMapping("/ai/vector")
    fun vector(
        @RequestParam(
            value = "message",
            defaultValue = "Tell me a joke"
        ) message: String
    ): Map<*, *> {
        val documents: List<Document> = listOf(Document(message))
        vectorStore.add(documents)
        return java.util.Map.of("vector", "ok")
    }
}
