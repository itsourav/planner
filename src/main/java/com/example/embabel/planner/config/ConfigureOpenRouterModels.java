package com.example.embabel.planner.config;

import com.embabel.agent.openai.OpenAiCompatibleModelFactory;
import com.embabel.agent.spi.LlmService;
import com.embabel.common.ai.model.PricingModel;
import com.embabel.common.util.ObjectProviders;
import io.micrometer.observation.ObservationRegistry;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Map;

@Configuration
public class ConfigureOpenRouterModels extends OpenAiCompatibleModelFactory {

    public ConfigureOpenRouterModels(
            @Value("${embabel.openrouter.base-url:https://openrouter.ai/api/v1}") String baseUrl,
            @Value("${OPENROUTER_API_KEY:}") String apiKey,
            @NonNull ObservationRegistry observationRegistry) {
        super(baseUrl,
                apiKey,
                null,
                null,
                Map.of(),
                observationRegistry,
                ObjectProviders.INSTANCE.empty(),
                ObjectProviders.INSTANCE.empty());
    }

    @Bean
    public LlmService<?> gemini_2_5_flash() {
        return openAiCompatibleLlm(
                "google/gemini-2.5-flash",
                PricingModel.usdPer1MTokens(0.15, 0.60),
                "OpenRouter",
                LocalDate.of(2025, 12, 28));
    }

}
