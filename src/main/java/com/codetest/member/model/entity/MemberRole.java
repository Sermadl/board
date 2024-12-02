package com.codetest.member.model.entity;

import lombok.Getter;

@Getter
public enum MemberRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String role;

    MemberRole(String role) {
        this.role = role;
    }
}
