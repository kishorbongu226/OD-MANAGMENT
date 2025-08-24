package org.studyeasy.SpringRestdemo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long accountId;
    private Long eventId;

      @ManyToOne
    @JoinColumn(name="registerid", referencedColumnName = "register_no", nullable = false)
    private Account account;

    private String certificateUrl;
}
