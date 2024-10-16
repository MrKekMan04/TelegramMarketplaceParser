package ru.overcode.gateway.model.rule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Правило
 */
@Entity
@Getter
@Setter
@Table(name = "rule")
public class Rule {

    /**
     * Идентфиикатор правила
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя правила
     */
    private String name;

    /**
     * Описание правила
     */
    private String description;
}
