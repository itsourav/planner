package com.example.embabel.planner.controller;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.example.embabel.planner.model.KycModels.ApiKycRequest;
import com.example.embabel.planner.model.KycModels.KycAssessment;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    private final AgentPlatform agentPlatform;

    public KycController(AgentPlatform agentPlatform) {
        this.agentPlatform = agentPlatform;
    }

    @PostMapping("/verify")
    public KycAssessment verify(@Valid @RequestBody ApiKycRequest request) {
        return AgentInvocation.create(agentPlatform, KycAssessment.class).invoke(request.toDomain());
    }

}
