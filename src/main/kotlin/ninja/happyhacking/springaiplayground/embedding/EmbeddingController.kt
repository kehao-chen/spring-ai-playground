package ninja.happyhacking.springaiplayground.embedding

import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.reader.TextReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.nio.file.Files


@RestController
class EmbeddingController @Autowired constructor(
    private val embeddingModel: EmbeddingModel,
    private val vectorStore: VectorStore,
) {
    @GetMapping("/ai/embedding")
    fun embed(
        @RequestParam(
            value = "message", defaultValue = "Tell me a joke"
        ) message: String
    ): Map<*, *> {
        val embeddingResponse: EmbeddingResponse = embeddingModel.embedForResponse(listOf(message))
        return java.util.Map.of("embedding", embeddingResponse)
    }

    @GetMapping("/ai/vector")
    fun vector(
        @RequestParam(
            value = "message", defaultValue = "Tell me a joke"
        ) message: String
    ): Map<*, *> {
        val documents: List<Document> = listOf(Document(message))
        vectorStore.add(documents)
        return java.util.Map.of("vector", "ok")
    }

    @PostMapping("/ai/upload")
    fun uploadFileWithoutEntity(
        @RequestPart("file") filePart: Mono<FilePart>
    ): Mono<ResponseEntity<Map<String, String>>> {
        val tempFile = Files.createTempFile("upload-", ".txt")
        return filePart.flatMap { part ->
            part.transferTo(tempFile)
                .then(Mono.fromCallable { tempFile })
                .flatMap { file ->
                    Mono.fromCallable {
                        val textReader = TextReader(file.toUri().toString())
                        textReader.get()
                    }.flatMap { documents ->
                            Mono.fromCallable {
                                val tokenTextSplitter = TokenTextSplitter()
                                tokenTextSplitter.apply(documents)
                            }
                        }.flatMap { splitDocuments ->
                            Mono.fromCallable { vectorStore.add(splitDocuments) }
                        }.map { ResponseEntity.ok(mapOf("upload" to "ok")) }
                }
        }.onErrorResume { ex ->
            val errorMessage = "Error uploading file: ${ex.message}"
            Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to errorMessage)))
        }
    }
}
