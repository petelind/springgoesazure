package com.servicenow.math.service;

import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONUtil;
import org.apache.tinkerpop.gremlin.util.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.util.ser.SerTokens;
import org.apache.tinkerpop.shaded.jackson.core.JsonGenerator;
import org.apache.tinkerpop.shaded.jackson.databind.SerializerProvider;
import org.apache.tinkerpop.shaded.jackson.databind.jsontype.TypeSerializer;
import org.apache.tinkerpop.shaded.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public final class ResponseMessageSerializer extends StdSerializer<ResponseMessage> {
    public ResponseMessageSerializer() {
        super(ResponseMessage.class);
    }

    @Override
    public void serialize(final ResponseMessage responseMessage, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        ser(responseMessage, jsonGenerator, serializerProvider, null);
    }

    @Override
    public void serializeWithType(final ResponseMessage responseMessage, final JsonGenerator jsonGenerator,
                                  final SerializerProvider serializerProvider,
                                  final TypeSerializer typeSerializer) throws IOException {
        ser(responseMessage, jsonGenerator, serializerProvider, typeSerializer);
    }

    public void ser(final ResponseMessage responseMessage, final JsonGenerator jsonGenerator,
                    final SerializerProvider serializerProvider,
                    final TypeSerializer typeSerializer) throws IOException {
        GraphSONUtil.writeStartObject(responseMessage, jsonGenerator, typeSerializer);

        jsonGenerator.writeStringField(SerTokens.TOKEN_REQUEST, responseMessage.getRequestId() != null ? responseMessage.getRequestId().toString() : null);
        jsonGenerator.writeFieldName(SerTokens.TOKEN_STATUS);

        GraphSONUtil.writeStartObject(responseMessage, jsonGenerator, typeSerializer);
        jsonGenerator.writeStringField(SerTokens.TOKEN_MESSAGE, responseMessage.getStatus().getMessage());
        jsonGenerator.writeNumberField(SerTokens.TOKEN_CODE, responseMessage.getStatus().getCode().getValue());
        jsonGenerator.writeObjectField(SerTokens.TOKEN_ATTRIBUTES, responseMessage.getStatus().getAttributes());
        GraphSONUtil.writeEndObject(responseMessage, jsonGenerator, typeSerializer);

        jsonGenerator.writeFieldName(SerTokens.TOKEN_RESULT);

        GraphSONUtil.writeStartObject(responseMessage, jsonGenerator, typeSerializer);

        if (null == responseMessage.getResult().getData()) {
            jsonGenerator.writeNullField(SerTokens.TOKEN_DATA);
        } else {
            jsonGenerator.writeFieldName(SerTokens.TOKEN_DATA);
            final Object result = responseMessage.getResult().getData();
            serializerProvider.findTypedValueSerializer(result.getClass(), true, null).serialize(result, jsonGenerator, serializerProvider);
        }

        jsonGenerator.writeObjectField(SerTokens.TOKEN_META, responseMessage.getResult().getMeta());
        GraphSONUtil.writeEndObject(responseMessage, jsonGenerator, typeSerializer);

        GraphSONUtil.writeEndObject(responseMessage, jsonGenerator, typeSerializer);
    }
}
