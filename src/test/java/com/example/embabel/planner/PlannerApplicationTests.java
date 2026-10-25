package com.example.embabel.planner;

import com.embabel.agent.spi.support.springai.SpringAiLlmService;
import com.embabel.common.ai.model.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlannerApplicationTests {

    @Autowired
    @Qualifier("openRouterBestLlm")
    private SpringAiLlmService bestLlm;

    @Autowired
    @Qualifier("openRouterLiteLlm")
    private SpringAiLlmService liteLlm;

    @Autowired
    @Qualifier("openrouter_embedding")
    private EmbeddingService embeddingService;

    @Autowired
    private com.example.embabel.planner.config.OpenRouterAudioTranscriptionService audioTranscriptionService;

    @Autowired
    private com.embabel.agent.core.AgentPlatform agentPlatform;

    @Test
    void contextLoads() {
        assertThat(bestLlm).isNotNull();
        assertThat(bestLlm.getProvider()).isEqualTo("OPENROUTER");
        assertThat(liteLlm).isNotNull();
        assertThat(liteLlm.getProvider()).isEqualTo("OPENROUTER");
        assertThat(embeddingService).isNotNull();
        assertThat(embeddingService.getProvider()).isEqualTo("OPENROUTER");
        assertThat(audioTranscriptionService).isNotNull();
        assertThat(audioTranscriptionService.getModel()).isEqualTo("openai/whisper-large-v3");
    }

    @Test
    @org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "OPENROUTER_API_KEY", matches = ".*\\S.*")
    void testLiveKycVerificationWithOpenRouter() {
        var request = new com.example.embabel.planner.model.KycModels.KycRequest(
                "CUST-001", "Maria Müller", "1985-03-15",
                "Swiss", "Software Engineer", "Employment salary");
        var assessment = com.embabel.agent.api.invocation.AgentInvocation.create(agentPlatform,
                com.example.embabel.planner.model.KycModels.KycAssessment.class).invoke(request);
        assertThat(assessment).isNotNull();
        assertThat(assessment.riskLevel()).isNotNull();
        assertThat(assessment.recommendation()).isNotNull();
        System.out.println(">> OpenRouter KYC Verification Success: " + assessment);
    }

}
