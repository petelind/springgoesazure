package com.servicenow.math.service;

import io.netty.buffer.ByteBufAllocator;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper;
import org.apache.tinkerpop.gremlin.structure.io.graphson.TypeInfo;
import org.apache.tinkerpop.gremlin.util.message.RequestMessage;
import org.apache.tinkerpop.gremlin.util.message.ResponseMessage;
import org.apache.tinkerpop.gremlin.util.ser.AbstractGraphSONMessageSerializerV2;
import org.apache.tinkerpop.gremlin.util.ser.GraphSONMessageSerializerV2;
import org.apache.tinkerpop.gremlin.util.ser.MessageTextSerializer;
import org.apache.tinkerpop.gremlin.util.ser.SerTokens;
import org.apache.tinkerpop.gremlin.util.ser.SerializationException;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class CustomMessageSerializerV2 extends AbstractCustomMessageSerializerV2 implements MessageTextSerializer<ObjectMapper> {
    private static final Logger logger = LoggerFactory.getLogger(GraphSONMessageSerializerV2.class);
    private static final String MIME_TYPE = SerTokens.MIME_GRAPHSON_V2;

    private static byte[] header;

    static {
        final ByteBuffer buffer = ByteBuffer.allocate(MIME_TYPE.length() + 1);
        buffer.put((byte) MIME_TYPE.length());
        buffer.put(MIME_TYPE.getBytes());
        header = buffer.array();
    }

    public CustomMessageSerializerV2() {
        super();
    }

    @Deprecated
    public CustomMessageSerializerV2(final GraphSONMapper mapper) {
        super(mapper);
    }

    /**
     * Create a GraphSONMessageSerializer with a provided {@link GraphSONMapper.Builder}.
     *
     * Note that to make this mapper usable in the context of request messages and responses,
     * this method will automatically register a {@link GremlinServerModule} to the provided
     * mapper.
     */
    public CustomMessageSerializerV2(final GraphSONMapper.Builder mapperBuilder) {
        super(mapperBuilder);
    }

    @Override
    public String[] mimeTypesSupported() {
        return new String[]{MIME_TYPE, SerTokens.MIME_JSON};
    }

    @Override
    public GraphSONMapper.Builder configureBuilder(final GraphSONMapper.Builder builder) {
        // already set to 2.0 in AbstractGraphSONMessageSerializerV2
        return builder.typeInfo(TypeInfo.NO_TYPES).addCustomModule(new GremlinServerModule());
    }

    @Override
    public byte[] obtainHeader() {
        return header;
    }

    @Override
    public ResponseMessage deserializeResponse(final String msg) throws SerializationException {
        try {
            return mapper.readValue(msg, ResponseMessage.class);
        } catch (Exception ex) {
            logger.warn("Response [{}] could not be deserialized by {}.", msg, AbstractGraphSONMessageSerializerV2.class.getName());
            throw new SerializationException(ex);
        }
    }

    @Override
    public String serializeResponseAsString(final ResponseMessage responseMessage, final ByteBufAllocator allocator) throws SerializationException {
        try {
            return mapper.writeValueAsString(responseMessage);
        } catch (Exception ex) {
            logger.warn("Response [{}] could not be serialized by {}.", responseMessage.toString(), AbstractGraphSONMessageSerializerV2.class.getName());
            throw new SerializationException(ex);
        }
    }

    @Override
    public RequestMessage deserializeRequest(final String msg) throws SerializationException {
        try {
            return mapper.readValue(msg, RequestMessage.class);
        } catch (Exception ex) {
            logger.warn("Request [{}] could not be deserialized by {}.", msg, AbstractGraphSONMessageSerializerV2.class.getName());
            throw new SerializationException(ex);
        }
    }

    @Override
    public String serializeRequestAsString(final RequestMessage requestMessage, final ByteBufAllocator allocator) throws SerializationException {
        try {
            return mapper.writeValueAsString(requestMessage);
        } catch (Exception ex) {
            logger.warn("Request [{}] could not be serialized by {}.", requestMessage.toString(), AbstractGraphSONMessageSerializerV2.class.getName());
            throw new SerializationException(ex);
        }
    }
}
