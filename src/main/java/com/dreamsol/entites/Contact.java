package com.dreamsol.entites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends CommonAutoIdEntity {

    @Column(nullable = false, length = 100, unique = true)
    private String employeeId;

    @Column(nullable = false, length = 50, unique = true)
    private long mobileNumber;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String contactName;

    @Column(nullable = false, length = 50)
    private String communicationName;

    @ManyToOne
    @JoinColumn(name = "unitId")
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "departmentId")
    private Department department;
}
