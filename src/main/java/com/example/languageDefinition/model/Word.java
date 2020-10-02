package com.example.languageDefinition.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder

@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="WORD")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private String word;

    @Column
    @Enumerated(EnumType.STRING)
    private Language language;
}
