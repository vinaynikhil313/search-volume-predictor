package com.vinaypabba.searchvolumepredictor.model;

import lombok.Data;

@Data
public class SearchVolumePredictionResponse {

    private String keyword;

    private Double score;

}
