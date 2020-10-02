package com.example.languageDefinition.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

@Entity
@Table(name = "DOCUMENT")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(columnDefinition = "text")
    private String text;
}
