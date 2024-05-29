package com.dreamsol.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DrivingLicenceAttachment extends CommonAutoIdEntity {

    private String originalFileName;
    private String generatedFileName;
    private String fileType;

}
