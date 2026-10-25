package com.example.embabel.planner.config;

import com.embabel.agent.openai.StandardOpenAiOptionsConverter;
import com.embabel.agent.spi.support.springai.SpringAiLlmService;
import com.embabel.common.ai.model.SpringAiEmbeddingService;
import com.embabel.common.ai.prompt.KnowledgeCutoffDate;
import com.openai.client.OpenAIClient;
import com.openai.client.OpenAIClientAsync;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.setup.OpenAiSetup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Configuration
public class LlmConfiguration {

    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(3);
    private static final String OPENROUTER_EMBEDDING = "openrouter_embedding";

    @Bean
    @Profile("openrouter")
    public SpringAiLlmService openRouterBestLlm(
            @Value("${OPENROUTER_API_KEY:${OPENAI_API_KEY:}}") String apiKey,
            @Value("${llm.openrouter.base-url:https://openrouter.ai/api/v1}") String baseUrl,
            @Value("${embabel.models.llms.best}") String model,
            @Value("${llm.openrouter.retry-attempts:3}") int maxRetries) {
        return openAiCompatibleLlm("OPENROUTER", baseUrl, apiKey, model, maxRetries);
    }

    @Bean
    @Profile("openrouter")
    public SpringAiLlmService openRouterLiteLlm(
            @Value("${OPENROUTER_API_KEY:${OPENAI_API_KEY:}}") String apiKey,
            @Value("${llm.openrouter.base-url:https://openrouter.ai/api/v1}") String baseUrl,
            @Value("${embabel.models.llms.lite}") String model,
            @Value("${llm.openrouter.retry-attempts:3}") int maxRetries) {
        return openAiCompatibleLlm("OPENROUTER", baseUrl, apiKey, model, maxRetries);
    }

    @Bean(OPENROUTER_EMBEDDING)
    @Profile("openrouter")
    public SpringAiEmbeddingService openRouterEmbeddingService(
            @Value("${OPENROUTER_API_KEY:${OPENAI_API_KEY:}}") String apiKey,
            @Value("${llm.openrouter.base-url:https://openrouter.ai/api/v1}") String baseUrl,
            @Value("${llm.openrouter.embedding.model:openai/text-embedding-3-small}") String model,
            @Value("${llm.openrouter.embedding.dimensions:#{null}}") Integer dimensions,
            @Value("${llm.openrouter.retry-attempts:3}") int maxRetries) {
        return openAiCompatibleEmbedding(OPENROUTER_EMBEDDING, "OPENROUTER", baseUrl, apiKey, model, dimensions, maxRetries);
    }

    @Bean
    @Profile("openrouter")
    public OpenRouterAudioTranscriptionService openRouterAudioTranscriptionService(
            @Value("${OPENROUTER_API_KEY:${OPENAI_API_KEY:}}") String apiKey,
            @Value("${llm.openrouter.base-url:https://openrouter.ai/api/v1}") String baseUrl,
            @Value("${llm.openrouter.audio.model:openai/whisper-large-v3}") String model) {
        return new OpenRouterAudioTranscriptionService(baseUrl, apiKey, model);
    }

    private static SpringAiEmbeddingService openAiCompatibleEmbedding(
            String name, String provider, String baseUrl, String apiKey, String model, Integer dimensions, int maxRetries) {
        return new SpringAiEmbeddingService(
                name,
                provider,
                OpenAiEmbeddingModel.builder()
                        .openAiClient(syncClient(baseUrl, apiKey, maxRetries))
                        .metadataMode(MetadataMode.ALL)
                        .options(OpenAiEmbeddingOptions.builder().model(model).dimensions(dimensions).build())
                        .build(),
                dimensions,
                null);
    }

    private static SpringAiLlmService openAiCompatibleLlm(
            String provider, String baseUrl, String apiKey, String model, int maxRetries) {
        LocalDate cutoff = LocalDate.now().minusDays(1);
        return new SpringAiLlmService(
                model,
                provider,
                OpenAiChatModel.builder()
                        .openAiClient(syncClient(baseUrl, apiKey, maxRetries))
                        .openAiClientAsync(asyncClient(baseUrl, apiKey, maxRetries))
                        .options(OpenAiChatOptions.builder().model(model).build())
                        .build(),
                StandardOpenAiOptionsConverter.INSTANCE,
                cutoff,
                Collections.singletonList(new KnowledgeCutoffDate(cutoff, DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
                null);
    }

    private static OpenAIClient syncClient(String baseUrl, String apiKey, int maxRetries) {
        return OpenAiSetup.setupSyncClient(
                baseUrl,
                apiKey != null ? apiKey : "",
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                REQUEST_TIMEOUT,
                maxRetries,
                null,
                null,
                ObservationRegistry.NOOP,
                null,
                Collections.emptyList());
    }

    private static OpenAIClientAsync asyncClient(String baseUrl, String apiKey, int maxRetries) {
        return OpenAiSetup.setupAsyncClient(
                baseUrl,
                apiKey != null ? apiKey : "",
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                REQUEST_TIMEOUT,
                maxRetries,
                null,
                null,
                ObservationRegistry.NOOP,
                null,
                Collections.emptyList());
    }
}
