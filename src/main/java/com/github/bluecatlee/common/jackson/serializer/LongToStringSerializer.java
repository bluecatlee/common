package com.github.bluecatlee.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Jackson Long转String序列化器
 *      前端js中Number类型的长度与Java的Long类型不一致。 Java中过大的Long类型值在js中不支持，需要处理转成String
 *      可以在字段上使用@JsonSerialize(using= LongToStringSerializer.class)
 * @deprecated 自带的ToStringSerializer可以直接使用，即@JsonSerialize(using= ToStringSerializer.class)
 * @see <a href="https://github.com/bluecatlee/common/tree/master/src/main/java/com/github/bluecatlee/common/configuration/LongToStringSerializerConfiguration.java">全局方式</>
 */
@Deprecated
public class LongToStringSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(String.valueOf(value));
    }
}
