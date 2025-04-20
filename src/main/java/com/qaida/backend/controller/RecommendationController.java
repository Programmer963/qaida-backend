package com.qaida.backend.controller;

import com.qaida.backend.dto.RecommendationResponse;
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

    @GetMapping("/city/{city}")
    public RecommendationResponse getRecommendations(@PathVariable String city) {
        return service.getRecommendations(city);
    }
    @GetMapping("/address/{address}")
    public RecommendationResponse getRecommendationsByAddress(@PathVariable String address) {
        return service.getRecommendationsByAddress(address);
    }
}
