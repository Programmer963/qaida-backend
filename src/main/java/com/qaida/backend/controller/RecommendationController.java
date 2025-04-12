package com.qaida.backend.controller;

import com.qaida.backend.model.RecommendationResponse;
import com.qaida.backend.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping
    public RecommendationResponse getRecommendations(@RequestParam String city) {
        return service.getRecommendations(city);
    }
}
