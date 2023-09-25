package com.servicenow.math.service;

import org.apache.tinkerpop.gremlin.structure.io.graphson.AbstractObjectDeserializer;
import org.apache.tinkerpop.gremlin.util.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.util.message.ResponseStatusCode;
import org.apache.tinkerpop.gremlin.util.ser.SerTokens;

import java.util.Map;
import java.util.UUID;

public final class ResponseMessageDeserializer extends AbstractObjectDeserializer<ResponseMessage> {
    protected ResponseMessageDeserializer() {
        super(ResponseMessage.class);
    }

    @Override
    public ResponseMessage createObject(final Map<String, Object> data) {
        final Map<String, Object> status = (Map<String, Object>) data.get(SerTokens.TOKEN_STATUS);
        final Map<String, Object> result = (Map<String, Object>) data.get(SerTokens.TOKEN_RESULT);
        return ResponseMessage.build(UUID.fromString(data.get(SerTokens.TOKEN_REQUEST).toString()))
                .code(ResponseStatusCode.getFromValue((Integer) status.get(SerTokens.TOKEN_CODE)))
                .statusMessage(String.valueOf(status.get(SerTokens.TOKEN_MESSAGE)))
                .statusAttributes((Map<String, Object>) status.get(SerTokens.TOKEN_ATTRIBUTES))
                .result(result.get(SerTokens.TOKEN_DATA))
                .responseMetaData((Map<String, Object>) result.get(SerTokens.TOKEN_META))
                .create();
    }
}
