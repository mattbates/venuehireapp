package com.mongodb.demo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/venue")
@Produces(value = MediaType.APPLICATION_JSON)
public class VenueResource {

    private JacksonDBCollection<Venue, String> collection;

    public VenueResource(JacksonDBCollection<Venue, String> collection) {
        this.collection = collection;
    }

    @GET
    public List<Venue> getVenues() {
        try {
            BasicDBObject query = new BasicDBObject();
            DBCursor<Venue> cur = collection.find();
            List<Venue> venues = new ArrayList<Venue>();
            while (cur.hasNext()) {
                Venue venue = cur.next();
                venues.add(venue);
            }
            return venues;
        }
        catch (MongoException e) {
            throw new WebApplicationException(500);
        }
    }

    @GET
    @Path("/by-location")
    public List<Venue> getVenuesByLocation(@QueryParam("latitude") String latitude,
                                           @QueryParam("longitude") String longitude) {
        try {
            BasicDBObject query = new BasicDBObject();
            query.append("location", new BasicDBObject("$near",
                    new BasicDBObject("$geometry", new BasicDBObject().
                            append("type", "Point").
                            append("coordinates", new Double[]{Double.parseDouble(longitude), Double.parseDouble(latitude)})
                    )));

            DBCursor<Venue> cur = collection.find(query);
            List<Venue> venues = new ArrayList<Venue>();
            while (cur.hasNext()) {
                Venue venue = cur.next();
                venues.add(venue);
            }
            return venues;
        }
        catch (MongoException e) {
            throw new WebApplicationException(500);
        }
    }

    @GET
    @Path("/by-text")
    public List<Venue> getVenuesByText(@QueryParam("searchtext") String searchText) {
        try {
            BasicDBObject query = new BasicDBObject();
            if (searchText != null) {
                query.append("$text", new BasicDBObject("$search", searchText));
            }
            DBCursor<Venue> cur = collection.find(query);
            List<Venue> venues = new ArrayList<Venue>();
            while (cur.hasNext()) {
                Venue venue = cur.next();
                venues.add(venue);
            }
            return venues;
        }
        catch (MongoException e) {
            throw new WebApplicationException(500);
        }
    }
}