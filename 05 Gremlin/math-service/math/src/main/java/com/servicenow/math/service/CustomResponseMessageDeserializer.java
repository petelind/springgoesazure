package com.servicenow.math.service;

import org.apache.tinkerpop.gremlin.util.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.util.message.ResponseResult;
import org.apache.tinkerpop.gremlin.util.message.ResponseStatus;
import org.apache.tinkerpop.shaded.jackson.core.JacksonException;
import org.apache.tinkerpop.shaded.jackson.core.JsonParser;
import org.apache.tinkerpop.shaded.jackson.core.JsonToken;
import org.apache.tinkerpop.shaded.jackson.databind.DeserializationContext;
import org.apache.tinkerpop.shaded.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.UUID;

public final class CustomResponseMessageDeserializer extends StdDeserializer<ResponseMessage> {
    CustomResponseMessageDeserializer() {
        super(ResponseMessage.class);
    }

    @Override
    public ResponseMessage deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ResponseMessage.Builder builder = null;

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            if (jp.getCurrentName().equals("requestId")) {
                jp.nextToken();
                builder = ResponseMessage.build(UUID.fromString(jp.getText()));
            } else if (jp.getCurrentName().equals("status")) {
                jp.nextToken();

                final ResponseStatus status = deserializationContext.readValue(jp, ResponseStatus.class);
                builder.statusMessage(status.getMessage());
                builder.code(status.getCode());
                builder.statusAttributes(status.getAttributes());
            } else if (jp.getCurrentName().equals("result")) {
                jp.nextToken();

                final ResponseResult result = deserializationContext.readValue(jp, ResponseResult.class);
                builder.result(result);
            }
        }

        return builder.create();
    }
}
