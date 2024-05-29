package com.dreamsol.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleLicenceAttachment extends CommonAutoIdEntity{


    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String generatedFileName;

    @Column(nullable = false)
    private String fileType;
}
