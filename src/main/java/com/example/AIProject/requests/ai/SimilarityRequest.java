package com.example.AIProject.requests.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityRequest {

    @NotBlank(message = "Le texte du CV ne peut pas être vide")
    @JsonProperty("cv_text")
    private String cvText;

    @NotBlank(message = "Le texte du job ne peut pas être vide")
    @JsonProperty("job_text")
    private String jobText;

    @JsonProperty("additional_factors")
    private AdditionalFactors additionalFactors;
}