package com.dreamsol.entites;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Purpose extends CommonAutoIdEntity {
    @Column(length = 50, nullable = false)
    private String purposeFor;

    @Column(length = 250, nullable = false)
    private String purposeBrief;

    private boolean alert;
}
