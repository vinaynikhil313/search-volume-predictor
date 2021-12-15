package com.vinaypabba.searchvolumepredictor.controller;

import com.vinaypabba.searchvolumepredictor.model.SearchVolumePredictionResponse;
import com.vinaypabba.searchvolumepredictor.service.AmazonAutocompleteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SearchVolumeController {

    private final AmazonAutocompleteService service;

    @Autowired
    public SearchVolumeController(AmazonAutocompleteService service) {
        this.service = service;
    }

    @GetMapping("/estimate")
    public SearchVolumePredictionResponse getSearchVolumePrediction(@RequestParam("keyword") String keyword) {
        SearchVolumePredictionResponse response = new SearchVolumePredictionResponse();
        response.setKeyword(keyword);
        Double score = service.calculatePredictionScore(keyword);
        response.setScore(score);
        log.info("score - " + score);
        return response;
    }

}
