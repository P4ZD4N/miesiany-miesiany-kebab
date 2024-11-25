package com.p4zd4n.kebab.responses.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record JobApplicationResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("applicant_first_name") String applicantFirstName,
        @JsonProperty("applicant_last_name") String applicantLastName,
        @JsonProperty("applicant_email") String applicantEmail,
        @JsonProperty("applicant_telephone") String applicantTelephone,
        @JsonProperty("additional_message") String additionalMessage,
        @JsonProperty("is_student") boolean isStudent,
        @JsonProperty("id_cv") Long idCv
) {}
