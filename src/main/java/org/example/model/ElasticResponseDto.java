package org.example.model;

import lombok.Data;

import java.util.Map;

@Data
public class ElasticResponseDto {
    private String _index;
    private String _type;
    private String _id;
    private double _score;
    private Map<String, Object> _source;
}
