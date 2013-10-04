package org.apache.streams.messaging.aggregation;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.streams.messaging.service.impl.CassandraActivityService;
import org.apache.streams.osgi.components.activitysubscriber.ActivityStreamsSubscriber;
import org.apache.streams.osgi.components.activitysubscriber.ActivityStreamsSubscriberWarehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ActivityAggregator {

    private ActivityStreamsSubscriberWarehouse activityStreamsSubscriberWarehouse;
    private CassandraActivityService activityService;
    private static final transient Log LOG = LogFactory.getLog(ActivityAggregator.class);

    @Autowired
    public ActivityAggregator(ActivityStreamsSubscriberWarehouse activityStreamsSubscriberWarehouse, CassandraActivityService activityService){
        this.activityStreamsSubscriberWarehouse = activityStreamsSubscriberWarehouse;
        this.activityService = activityService;
    }

    @Scheduled(fixedRate=30000)
    public void distributeToSubscribers() {
        for (ActivityStreamsSubscriber subscriber : activityStreamsSubscriberWarehouse.getAllSubscribers()) {
              updateSubscriber(subscriber);
        }
    }

    public void updateSubscriber(ActivityStreamsSubscriber subscriber){
        Set<String> activities = new TreeSet<String>();
        activities.addAll(activityService.getActivitiesForFilters(subscriber.getActivityStreamsSubscriberConfiguration().getFilters(), subscriber.getLastUpdated()));
        //TODO: an activity posted in between the cql query and setting the lastUpdated field will be lost
        subscriber.setLastUpdated(new Date());
        subscriber.receive(new ArrayList<String>(activities));
    }
}