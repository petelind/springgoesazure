package com.servicenow.math.service;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.message.ResponseResult;
import org.apache.tinkerpop.shaded.jackson.core.JacksonException;
import org.apache.tinkerpop.shaded.jackson.core.JsonParser;
import org.apache.tinkerpop.shaded.jackson.core.JsonToken;
import org.apache.tinkerpop.shaded.jackson.core.type.TypeReference;
import org.apache.tinkerpop.shaded.jackson.databind.DeserializationContext;
import org.apache.tinkerpop.shaded.jackson.databind.JavaType;
import org.apache.tinkerpop.shaded.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomResponseResultDeserializer extends StdDeserializer<ResponseResult> {
    CustomResponseResultDeserializer() {
        super(ResponseResult.class);
    }

    @Override
    public ResponseResult deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JacksonException {
        Object data = null;
        Map<String, Object> meta = new HashMap<>();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            if (jp.getCurrentName().equals("data")) {
                jp.nextToken();
                if (jp.currentToken() == JsonToken.VALUE_NULL) {
                    // do nothing
                } else if (jp.currentToken() == JsonToken.START_ARRAY) {
                    final JavaType javaType = ctx.getTypeFactory().constructCollectionType(
                            List.class,
                            Vertex.class
                    );
                    data = ctx.readValue(jp, javaType);
                } else {
                    throw new UnsupportedOperationException();
                }
            } else if (jp.getCurrentName().equals("meta")) {
                jp.nextToken();
                meta = ctx.readValue(jp, Map.class);
            }
        }

        return new ResponseResult(data, meta);
    }
}
