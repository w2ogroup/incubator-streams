package org.apache.streams.datasift.provider;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.streams.datasift.config.Facebook;
import org.apache.streams.datasift.interaction.Interaction;
import org.apache.streams.datasift.serializer.StreamsDatasiftMapper;
import org.apache.streams.datasift.twitter.Twitter;
import org.apache.streams.datasift.youtube.YouTube;

import java.io.IOException;

/**
 * Created by sblackmon on 12/13/13.
 */
public class DatasiftEventClassifier {

    public static Class detectClass( String json ) {

        Preconditions.checkNotNull(json);
        Preconditions.checkArgument(StringUtils.isNotEmpty(json));

        ObjectNode objectNode;
        try {
            objectNode = (ObjectNode) StreamsDatasiftMapper.getInstance().readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if( objectNode.findValue("twitter") != null )
            return Twitter.class;
        else if( objectNode.findValue("youtube") != null )
            return YouTube.class;
        else if( objectNode.findValue("facebook") != null )
            return Facebook.class;
        else
            return Interaction.class;
    }
}
