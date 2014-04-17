package org.apache.streams.sysomos.serializer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang.NotImplementedException;
import org.apache.streams.data.ActivitySerializer;
import org.apache.streams.exceptions.ActivitySerializerException;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.pojo.json.Provider;

import java.util.List;
import java.util.Map;

/**
 * Created by sblackmon on 3/26/14.
 */
public class SysomosXmlActivitySerializer implements ActivitySerializer<String>
{

    public SysomosXmlActivitySerializer() {

    }

    @Override
    public String serializationFormat() {
        return null;
    }

    @Override
    public String serialize(Activity deserialized) throws ActivitySerializerException {
        throw new NotImplementedException();
    }

    @Override
    public Activity deserialize(String serialized) throws ActivitySerializerException {

        return null;
    }

    @Override
    public List<Activity> deserializeAll(List<String> serializedList) {
        throw new NotImplementedException();
    }

    public static Provider getProvider() {
        Provider provider = new Provider();
        provider.setId(formatId("id:sysomos"));
        return provider;
    }

    public static void addSysomosExtension(Activity activity, ObjectNode event) {
        Map<String, Object> extensions = org.apache.streams.data.util.ActivityUtil.ensureExtensions(activity);
        extensions.put("sysomos", event);
    }

    public static String formatId(String... idparts) {
        return Joiner.on(":").join(Lists.asList("id:sysomos", idparts));
    }
}
