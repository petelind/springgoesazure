package com.servicenow.math.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONTypeIdResolver;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONVersion;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONXModuleV2;
import org.apache.tinkerpop.gremlin.util.message.RequestMessage;
import org.apache.tinkerpop.gremlin.util.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.util.ser.AbstractGraphSONMessageSerializerV2;
import org.apache.tinkerpop.gremlin.util.ser.AbstractMessageSerializer;
import org.apache.tinkerpop.gremlin.util.ser.SerializationException;
import org.apache.tinkerpop.shaded.jackson.annotation.JsonTypeInfo;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.apache.tinkerpop.shaded.jackson.databind.jsontype.TypeIdResolver;
import org.apache.tinkerpop.shaded.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public abstract class AbstractCustomMessageSerializerV2 extends AbstractMessageSerializer<ObjectMapper> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractGraphSONMessageSerializerV2.class);

    protected ObjectMapper mapper;

    public AbstractCustomMessageSerializerV2() {
        final GraphSONMapper.Builder builder = configureBuilder(initBuilder(null));
        mapper = builder.create().createMapper();
    }

    @Deprecated
    public AbstractCustomMessageSerializerV2(final GraphSONMapper mapper) {
        this.mapper = mapper.createMapper();
    }

    public AbstractCustomMessageSerializerV2(final GraphSONMapper.Builder mapperBuilder) {
        this.mapper = configureBuilder(mapperBuilder)
                .create()
                .createMapper();
    }

    public abstract byte[] obtainHeader();

    public abstract GraphSONMapper.Builder configureBuilder(final GraphSONMapper.Builder builder);

    @Override
    public void configure(final Map<String, Object> config, final Map<String, Graph> graphs) {
        final GraphSONMapper.Builder initialBuilder = initBuilder(null);
        addIoRegistries(config, initialBuilder);
        applyMaxTokenLimits(initialBuilder, config);
        mapper = configureBuilder(initialBuilder)
                .create()
                .createMapper();

        // this is custom code
//        final TypeIdResolver typeIdResolver = new GraphSONTypeIdResolver();
//        final StdTypeResolverBuilder builder = new CustomTypeResolverBuilder()
//                .init(JsonTypeInfo.Id.CUSTOM, typeIdResolver);
//
//        registerTypes(typeIdResolver);
//
//        mapper.setDefaultTyping(builder);
    }

    @Override
    public ByteBuf serializeResponseAsBinary(final ResponseMessage responseMessage, final ByteBufAllocator allocator) throws SerializationException {
        ByteBuf encodedMessage = null;
        try {
            final byte[] payload = mapper.writeValueAsBytes(responseMessage);
            encodedMessage = allocator.buffer(payload.length);
            encodedMessage.writeBytes(payload);

            return encodedMessage;
        } catch (Exception ex) {
            if (encodedMessage != null) ReferenceCountUtil.release(encodedMessage);

            logger.warn(String.format("Response [%s] could not be serialized by %s.", responseMessage, AbstractGraphSONMessageSerializerV2.class.getName()), ex);
            throw new SerializationException(ex);
        }
    }

    @Override
    public ByteBuf serializeRequestAsBinary(final RequestMessage requestMessage, final ByteBufAllocator allocator) throws SerializationException {
        ByteBuf encodedMessage = null;
        try {
            final byte[] header = obtainHeader();
            final byte[] payload = mapper.writeValueAsBytes(requestMessage);

            encodedMessage = allocator.buffer(header.length + payload.length);
            encodedMessage.writeBytes(header);
            encodedMessage.writeBytes(payload);

            return encodedMessage;
        } catch (Exception ex) {
            if (encodedMessage != null) ReferenceCountUtil.release(encodedMessage);

            logger.warn(String.format("Request [%s] could not be serialized by %s.", requestMessage, AbstractGraphSONMessageSerializerV2.class.getName()), ex);
            throw new SerializationException(ex);
        }
    }

    @Override
    public RequestMessage deserializeRequest(final ByteBuf msg) throws SerializationException {
        try {
            final byte[] payload = new byte[msg.readableBytes()];
            msg.readBytes(payload);
            return mapper.readValue(payload, RequestMessage.class);
        } catch (Exception ex) {
            logger.warn(String.format("Request [%s] could not be deserialized by %s.", msg, AbstractGraphSONMessageSerializerV2.class.getName()), ex);
            throw new SerializationException(ex);
        }
    }

    @Override
    public ResponseMessage deserializeResponse(final ByteBuf msg) throws SerializationException {
        try {
            final byte[] payload = new byte[msg.readableBytes()];
            msg.readBytes(payload);
            return mapper.readValue(payload, ResponseMessage.class);
        } catch (Exception ex) {
            logger.warn(String.format("Response [%s] could not be deserialized by %s.", msg, AbstractGraphSONMessageSerializerV2.class.getName()), ex);
            throw new SerializationException(ex);
        }
    }

    private GraphSONMapper.Builder initBuilder(final GraphSONMapper.Builder builder) {
        final GraphSONMapper.Builder b = null == builder ? GraphSONMapper.build() : builder;
        return b.addCustomModule(GraphSONXModuleV2.build())
                .version(GraphSONVersion.V2_0);
    }

    private GraphSONMapper.Builder applyMaxTokenLimits(final GraphSONMapper.Builder builder, final Map<String, Object> config) {
        if(config != null) {
            if(config.containsKey("maxNumberLength")) {
                builder.maxNumberLength((int)config.get("maxNumberLength"));
            }
            if(config.containsKey("maxStringLength")) {
                builder.maxStringLength((int)config.get("maxStringLength"));
            }
            if(config.containsKey("maxNestingDepth")) {
                builder.maxNestingDepth((int)config.get("maxNestingDepth"));
            }
        }
        return builder;
    }

    @Override
    public ObjectMapper getMapper() {
        return this.mapper;
    }

}
