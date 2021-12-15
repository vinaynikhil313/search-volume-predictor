package com.vinaypabba.searchvolumepredictor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class AmazonAutocompleteService {

    private final RestTemplate restTemplate;
    private static final String AUTOCOMPLETE_URL = "https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=";

    @Autowired
    public AmazonAutocompleteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Queries the Amazon autocomplete API and checks if the provided keyword
     * is returned as part of the possible predictions.
     * @param partialText the partial text to be queried from the API (equivalent to typing text in Amazon's search box)
     * @param keyword the actual keyword for which the search volume has to be predicted
     * @return a boolean stating if the keyword is found when partially queried for the text of the keyword itself
     */
    private boolean isTextPredicted(String partialText, String keyword) {
        ResponseEntity<Object[]> response = restTemplate.getForEntity(AUTOCOMPLETE_URL + partialText, Object[].class);
        Object[] body = response.getBody();
        if (Objects.nonNull(body) && body.length > 2) {
            Set<String> predictedKeywords = new HashSet<>((List<String>) body[1]);
            return predictedKeywords.contains(keyword);
        }
        return false;
    }

    /**
     * Queries the Amazon autocomplete API for various combinations
     * of the keyword provided in order to compute the search volume prediction score
     * @param keyword the keyword for which the prediction score has to be calculated
     * @return the calculated score
     */
    public Double calculatePredictionScore(String keyword) {
        keyword = keyword.toLowerCase(Locale.ROOT).trim();
        int first = 1, last = keyword.length(), mid = 1;
        while (first <= last) {
            mid = (first + last)/2;
            String partialWord = keyword.substring(0, mid).trim();
            boolean isPredicted = isTextPredicted(partialWord, keyword);
            if (isPredicted) {
                last = mid - 1;
            } else {
                first = mid + 1;
            }
        }
        log.info("Prediction found for substring - {}", keyword.substring(0, mid));
        if (mid == 1) {
            return 100d;
        } else {
            double score = (keyword.length() - mid);
            return 100 * score/keyword.length();
        }
    }

}
