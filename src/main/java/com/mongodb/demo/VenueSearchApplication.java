package com.mongodb.demo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.sun.jersey.api.client.Client;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.vz.mongodb.jackson.JacksonDBCollection;

public class VenueSearchApplication extends Application<VenueSearchConfiguration> {

    public static void main(String[] args) throws Exception {
        new VenueSearchApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<VenueSearchConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/"));
    }

    @Override
    public void run(VenueSearchConfiguration config, Environment environment) throws Exception {
        environment.jersey().setUrlPattern("/api/*");

        // initiate db connections
        MongoClient client = new MongoClient(config.mongohost, config.mongoport);
        DB db = client.getDB("demo");
        DBCollection venuesColl = db.getCollection("venues");
        JacksonDBCollection<Venue, String> venues = JacksonDBCollection.wrap(venuesColl, Venue.class, String.class);
        DBCollection postcodes = db.getCollection("bristol_postcodes");

        // resources for the postcode, venue and rating microservices
        final PostcodeResource postcodeResource = new PostcodeResource(postcodes);
        final VenueResource venueResource = new VenueResource(venues);
        final RatingResource ratingResource = new RatingResource(venuesColl);

        // jersey http client
        final Client jerseyClient = new JerseyClientBuilder(environment).using(config.getJerseyClientConfiguration())
                .build(getName());
        // api gateway search resource
        final VenueApiResource searchResource = new VenueApiResource(jerseyClient, config.apphost, config.appport);

        // register all the resources
        environment.jersey().register(postcodeResource);
        environment.jersey().register(venueResource);
        environment.jersey().register(ratingResource);
        environment.jersey().register(searchResource);
    }
}