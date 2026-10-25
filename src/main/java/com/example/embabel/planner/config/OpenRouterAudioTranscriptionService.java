package com.example.embabel.planner.config;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Base64;

/**
 * Service for transcribing audio via OpenRouter's Speech-to-Text API
 * using models such as openai/whisper-large-v3.
 */
public class OpenRouterAudioTranscriptionService {

    private final RestClient restClient;
    private final String model;

    public OpenRouterAudioTranscriptionService(String baseUrl, String apiKey, String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : ""))
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String transcribe(byte[] audioBytes, String format) {
        String base64 = Base64.getEncoder().encodeToString(audioBytes);
        return transcribeBase64(base64, format);
    }

    public String transcribeBase64(String base64Audio, String format) {
        var response = transcribeDetailed(base64Audio, format);
        return response != null ? response.text() : "";
    }

    public AudioTranscriptionResponse transcribeDetailed(String base64Audio, String format) {
        var request = new AudioTranscriptionRequest(
                this.model,
                new AudioTranscriptionRequest.InputAudio(base64Audio, format != null ? format : "wav")
        );
        return restClient.post()
                .uri("/audio/transcriptions")
                .body(request)
                .retrieve()
                .body(AudioTranscriptionResponse.class);
    }

    public String getModel() {
        return model;
    }

    public record AudioTranscriptionRequest(
            String model,
            InputAudio input_audio
    ) {
        public record InputAudio(String data, String format) {
        }
    }

    public record AudioTranscriptionResponse(
            String text,
            Usage usage
    ) {
        public record Usage(Double seconds, Integer total_tokens, Double cost) {
        }
    }
}
