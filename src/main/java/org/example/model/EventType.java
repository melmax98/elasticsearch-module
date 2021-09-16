package org.example.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventType {
    WORKSHOP("workshop"), TECH_TALK("tech-talk");

    @Getter
    private final String value;
}
