package com.servicenow.math.service;

import org.apache.tinkerpop.gremlin.structure.io.graphson.AbstractObjectDeserializer;
import org.apache.tinkerpop.gremlin.util.message.RequestMessage;
import org.apache.tinkerpop.gremlin.util.ser.SerTokens;

import java.util.Map;
import java.util.UUID;

public final class RequestMessageDeserializer extends AbstractObjectDeserializer<RequestMessage> {
    protected RequestMessageDeserializer() {
        super(RequestMessage.class);
    }

    @Override
    public RequestMessage createObject(final Map<String, Object> data) {
        final Map<String, Object> args = (Map<String, Object>) data.get(SerTokens.TOKEN_ARGS);
        RequestMessage.Builder builder = RequestMessage.build(data.get(SerTokens.TOKEN_OP).toString())
                .overrideRequestId(UUID.fromString(data.get(SerTokens.TOKEN_REQUEST).toString()));

        if (data.containsKey(SerTokens.TOKEN_PROCESSOR))
            builder = builder.processor(data.get(SerTokens.TOKEN_PROCESSOR).toString());

        if (args != null) {
            for (Map.Entry<String, Object> kv : args.entrySet()) {
                builder = builder.addArg(kv.getKey(), kv.getValue());
            }
        }

        return builder.create();
    }
}
