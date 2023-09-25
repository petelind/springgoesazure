package com.servicenow.math.service;

import org.apache.tinkerpop.gremlin.util.message.ResponseStatus;
import org.apache.tinkerpop.gremlin.util.message.ResponseStatusCode;
import org.apache.tinkerpop.shaded.jackson.core.JacksonException;
import org.apache.tinkerpop.shaded.jackson.core.JsonParser;
import org.apache.tinkerpop.shaded.jackson.core.JsonToken;
import org.apache.tinkerpop.shaded.jackson.databind.DeserializationContext;
import org.apache.tinkerpop.shaded.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class CustomResponseStatusDeserializer extends StdDeserializer<ResponseStatus> {
    CustomResponseStatusDeserializer() {
        super(ResponseStatus.class);
    }

    @Override
    public ResponseStatus deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JacksonException {
        ResponseStatusCode code = null;
        String message = null;
        Map<String, Object> attributes = new HashMap<>();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            if (jp.getCurrentName().equals("code")) {
                jp.nextToken();
                code = ResponseStatusCode.getFromValue(jp.getIntValue());
            } else if (jp.getCurrentName().equals("message")) {
                jp.nextToken();
                message = jp.getText();
            } else if (jp.getCurrentName().equals("attributes")) {
                jp.nextToken();
                attributes = ctx.readValue(jp, Map.class);
            }
        }

        return new ResponseStatus(
                code,
                message,
                attributes
        );
    }
}
