/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.streams.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.streams.core.*;
import org.apache.streams.jackson.StreamsJacksonMapper;
import org.apache.streams.util.ComponentUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ***********************************************************************************************************
 * Authors:
 * smashew
 * steveblackmon
 * ************************************************************************************************************
 */

public class ElasticsearchPersistReader implements StreamsPersistReader, Serializable {
    public static final String STREAMS_ID = "ElasticsearchPersistReader";

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchPersistReader.class);

    private final StreamsResultSet streamsResultSet;

    private ElasticsearchQuery elasticsearchQuery;
    private final ElasticsearchReaderConfiguration config;
    private final ElasticsearchClientManager elasticsearchClientManager;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private static ObjectMapper mapper = StreamsJacksonMapper.getInstance();

    public ElasticsearchPersistReader(ElasticsearchReaderConfiguration config) {
        this(config, new ElasticsearchClientManager(config));
    }

    public ElasticsearchPersistReader(ElasticsearchReaderConfiguration config, ElasticsearchClientManager escm) {
        this.config = config;
        this.elasticsearchClientManager = escm;
        int batchSize = config.getBatchSize().intValue() <= 0 ? 10 : config.getBatchSize().intValue();
        this.streamsResultSet = new StreamsResultSet(new ArrayBlockingQueue<StreamsDatum>(batchSize));
    }

    @Override
    public boolean isRunning() {
        return this.isRunning.get();
    }

    @Override
    public void startStream() {
        LOGGER.debug("startStream");
        this.isRunning.set(true);
        final ElasticsearchQuery query = this.elasticsearchQuery;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (query.hasNext() && !query.isCompleted()) {
                        SearchHit hit = query.next();
                        if(hit != null) {
                            ObjectNode jsonObject = mapper.readValue(hit.getSourceAsString(), ObjectNode.class);
                            StreamsDatum item = new StreamsDatum(jsonObject, hit.getId());
                            item.getMetadata().put("id", hit.getId());
                            item.getMetadata().put("index", hit.getIndex());
                            item.getMetadata().put("type", hit.getType());
                            ComponentUtils.offerUntilSuccess(item, streamsResultSet.getQueue());
                        }
                    }
                }
                catch(Throwable e) {
                    LOGGER.error("Unexpected issue: {}", e);
                }
                finally{
                    isRunning.set(false);
                }
            }
        }).start();
    }

    @Override
    public void prepare(Object o) {
        if(this.config == null) {
            throw new IllegalStateException("Unable to run without configuration");
        }

        elasticsearchQuery = new ElasticsearchQuery(config, this.elasticsearchClientManager);
        elasticsearchQuery.execute(o);
    }

    @Override
    public StreamsResultSet readAll() {
        return readCurrent();
    }

    @Override
    public StreamsResultSet readCurrent() {
        return this.streamsResultSet;
    }

    //TODO - This just reads current records and does not adjust any queries
    @Override
    public StreamsResultSet readNew(BigInteger sequence) {
        return readCurrent();
    }

    //TODO - This just reads current records and does not adjust any queries
    @Override
    public StreamsResultSet readRange(DateTime start, DateTime end) {
        return readCurrent();
    }

    @Override
    public void cleanUp() {
        LOGGER.info("PersistReader done");
    }
}