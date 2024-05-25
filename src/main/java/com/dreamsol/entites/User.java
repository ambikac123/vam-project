package com.dreamsol.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends CommonAutoIdEntity
{
    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 100, nullable = false,unique = true)
    private String email;

    @Column(length = 10, nullable = false,unique = true)
    private Long mobile;

    @Column(nullable = false)
    private String password;
}
