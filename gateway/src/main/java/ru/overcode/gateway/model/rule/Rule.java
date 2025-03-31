package ru.overcode.gateway.model.rule;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
