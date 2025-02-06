package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpgradeForm {
    @NotNull
    private Integer userId;

    @NotBlank
    private String role;

    @NotNull
    private long amount;
}