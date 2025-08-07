package com.example.AIProject.responses;

   import com.fasterxml.jackson.annotation.JsonProperty;
   import lombok.AllArgsConstructor;
   import lombok.Builder;
   import lombok.Data;
   import lombok.NoArgsConstructor;

   @Data
   @Builder
   @NoArgsConstructor
   @AllArgsConstructor
   public class SimilarityResponse {

       @JsonProperty("similarity_score")
       private double similarityScore;

       @JsonProperty("similarity_raw")
       private double similarityRaw;

       @JsonProperty("processing_time_ms")
       private long processingTimeMs;

       private String status;
   }