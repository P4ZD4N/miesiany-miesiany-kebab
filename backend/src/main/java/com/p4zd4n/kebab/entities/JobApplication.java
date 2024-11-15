package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobApplication extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "applicant_first_name", nullable = false)
    private String applicantFirstName;

    @Column(name = "applicant_last_name", nullable = false)
    private String applicantLastName;

    @Column(name = "applicant_email", nullable = false)
    private String applicantEmail;

    @Column(name = "applicant_telephone", nullable = false)
    private String applicantTelephone;

    @Column(name = "additional_message")
    private String additionalMessage;

    @Column(name = "is_student")
    private boolean isStudent;

    @OneToOne(mappedBy = "jobApplication")
    private Cv cv;

    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    private JobOffer jobOffer;

    @Builder
    public JobApplication(
            String applicantFirstName,
            String applicantLastName,
            String applicantEmail,
            String applicantTelephone,
            String additionalMessage,
            boolean isStudent
    ) {
        this.applicantFirstName = applicantFirstName;
        this.applicantLastName = applicantLastName;
        this.applicantEmail = applicantEmail;
        this.applicantTelephone = applicantTelephone;
        this.additionalMessage = additionalMessage;
        this.isStudent = isStudent;
    }
}
