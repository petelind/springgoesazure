package com.servicenow.math.service;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.util.message.RequestMessage;
import org.apache.tinkerpop.gremlin.util.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.util.message.ResponseResult;
import org.apache.tinkerpop.gremlin.util.message.ResponseStatus;
import org.apache.tinkerpop.shaded.jackson.databind.module.SimpleModule;

public final class GremlinServerModule extends SimpleModule {
    public GremlinServerModule() {
        super("graphson-gremlin-server");

        // SERIALIZERS
        addSerializer(ResponseMessage.class, new ResponseMessageSerializer());
        addSerializer(RequestMessage.class, new RequestMessageSerializer());

        //DESERIALIZERS
        addDeserializer(ResponseMessage.class, new CustomResponseMessageDeserializer());
        addDeserializer(ResponseStatus.class, new CustomResponseStatusDeserializer());
        addDeserializer(ResponseResult.class, new CustomResponseResultDeserializer());
//        addDeserializer(Element.class, new ElementDeserializer());
//            addDeserializer(ResponseMessage.class, new ResponseMessageDeserializer());
        addDeserializer(RequestMessage.class, new RequestMessageDeserializer());
    }
}
