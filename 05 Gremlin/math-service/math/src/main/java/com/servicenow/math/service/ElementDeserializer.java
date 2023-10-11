package com.servicenow.math.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.shaded.jackson.core.JacksonException;
import org.apache.tinkerpop.shaded.jackson.core.JsonParser;
import org.apache.tinkerpop.shaded.jackson.core.JsonToken;
import org.apache.tinkerpop.shaded.jackson.core.TreeNode;
import org.apache.tinkerpop.shaded.jackson.databind.DeserializationContext;
import org.apache.tinkerpop.shaded.jackson.databind.JavaType;
import org.apache.tinkerpop.shaded.jackson.databind.deser.std.StdDeserializer;
import org.apache.tinkerpop.shaded.jackson.databind.node.TextNode;
import org.apache.tinkerpop.shaded.jackson.databind.util.TokenBuffer;

import java.io.IOException;

public class ElementDeserializer extends StdDeserializer<Element> {
    ElementDeserializer() {
        super(Element.class);
    }

    @Override
    public Element deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JacksonException {
        final TreeNode tree = jp.getCodec().readTree(jp);
        final String type = TextNode.class.cast(tree.get("type")).textValue();
        final Class<? extends Element> targetType;
        if (StringUtils.equalsIgnoreCase(type, "vertex")) {
            targetType = Vertex.class;
        } else {
            throw new UnsupportedOperationException();
        }
        return ctx.readValue(jp, targetType);
    }
}
