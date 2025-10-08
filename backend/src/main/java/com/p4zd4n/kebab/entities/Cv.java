package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cvs")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Cv extends WithTimestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "file_name")
  private String fileName;

  @Lob
  @Column(name = "data")
  private byte[] data;

  @OneToOne
  @JoinColumn(name = "job_application_id")
  private JobApplication jobApplication;

  @Builder
  public Cv(String fileName, byte[] data) {
    this.fileName = fileName;
    this.data = data;
  }
}
