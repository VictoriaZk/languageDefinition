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

    private String word;

    Double probability;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Enumerated(EnumType.STRING)
    private Method method;
}
