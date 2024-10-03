package ninja.happyhacking.springaiplayground.embedding

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class DataRetrievalService @Autowired constructor(
    private val vectorStore: VectorStore
) {
    fun searchData(query: String?): List<Document> {
        return vectorStore.similaritySearch(query)
    }
}
