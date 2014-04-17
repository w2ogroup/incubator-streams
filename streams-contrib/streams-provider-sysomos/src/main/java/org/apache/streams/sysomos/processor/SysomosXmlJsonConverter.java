package org.apache.streams.sysomos.processor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang.NotImplementedException;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.core.StreamsProcessor;
import org.apache.streams.data.ActivitySerializer;
import org.apache.streams.exceptions.ActivitySerializerException;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.pojo.json.Provider;

import java.util.List;
import java.util.Map;

/**
 * Created by sblackmon on 3/26/14.
 */
public class SysomosXmlJsonConverter implements StreamsProcessor
{

    public SysomosXmlJsonConverter() {

    }

    @Override
    public List<StreamsDatum> process(StreamsDatum entry) {

        // this class converts individual XML fragments from heartbeat API
        //   into beans that match the sysomos jsonschema, one per post

        return Lists.newArrayList();
    }

    @Override
    public void prepare(Object configurationObject) {

    }

    @Override
    public void cleanUp() {

    }
}
