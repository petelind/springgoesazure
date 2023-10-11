package com.servicenow.math.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONTokens;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertex;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Component
public class GremlinConverter {
    public Collection<Result> convert(final Collection<Result> source) {
        return source.stream()
                .map(this::convert)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Result convert(final Result source) {
        final Object content = source.getObject();
        if (content instanceof Map map) {
            return fromMap(map);
        }
        return source;
    }

    private Result fromMap(final Map<String, Object> result) {
        final String type = (String) result.get(GraphSONTokens.TYPE);
        final Element content;
        if (StringUtils.equalsIgnoreCase(type, Vertex.DEFAULT_LABEL)) {
            content = new DetachedVertex(
                    result.get("id"),
                    (String) result.get("label"),
                    (Map) result.get("properties")
            );
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported type %s",
                    type
            ));
        }
        return new Result(content);
    }
}
