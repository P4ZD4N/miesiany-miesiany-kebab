package com.p4zd4n.kebab.entities;

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
public class JobApplication {

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

    @Lob
    private byte[] cv;

    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    private JobOffer jobOffer;

    @Builder
    public JobApplication(
            String applicantFirstName,
            String applicantLastName,
            String applicantEmail,
            String applicantTelephone,
            byte[] cv
    ) {
        this.applicantFirstName = applicantFirstName;
        this.applicantLastName = applicantLastName;
        this.applicantEmail = applicantEmail;
        this.applicantTelephone = applicantTelephone;
        this.cv = cv;
    }
}
