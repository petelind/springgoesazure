package com.servicenow.math.service;

import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONUtil;
import org.apache.tinkerpop.gremlin.util.message.RequestMessage;
import org.apache.tinkerpop.gremlin.util.ser.SerTokens;
import org.apache.tinkerpop.shaded.jackson.core.JsonGenerator;
import org.apache.tinkerpop.shaded.jackson.databind.SerializerProvider;
import org.apache.tinkerpop.shaded.jackson.databind.jsontype.TypeSerializer;
import org.apache.tinkerpop.shaded.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public final class RequestMessageSerializer extends StdSerializer<RequestMessage> {
    public RequestMessageSerializer() {
        super(RequestMessage.class);
    }

    @Override
    public void serialize(final RequestMessage requestMessage, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        ser(requestMessage, jsonGenerator, serializerProvider, null);
    }

    @Override
    public void serializeWithType(final RequestMessage requestMessage, final JsonGenerator jsonGenerator,
                                  final SerializerProvider serializerProvider,
                                  final TypeSerializer typeSerializer) throws IOException {
        ser(requestMessage, jsonGenerator, serializerProvider, typeSerializer);
    }

    public void ser(final RequestMessage requestMessage, final JsonGenerator jsonGenerator,
                    final SerializerProvider serializerProvider,
                    final TypeSerializer typeSerializer) throws IOException {
        GraphSONUtil.writeStartObject(requestMessage, jsonGenerator, typeSerializer);

        jsonGenerator.writeStringField(SerTokens.TOKEN_REQUEST, requestMessage.getRequestId().toString());
        jsonGenerator.writeStringField(SerTokens.TOKEN_OP, requestMessage.getOp());
        jsonGenerator.writeStringField(SerTokens.TOKEN_PROCESSOR, requestMessage.getProcessor());
        jsonGenerator.writeObjectField(SerTokens.TOKEN_ARGS, requestMessage.getArgs());

        GraphSONUtil.writeEndObject(requestMessage, jsonGenerator, typeSerializer);
    }
}
