package com.mongodb.demo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/venue/{id}/rating")
@Produces(value = MediaType.APPLICATION_JSON)
public class RatingResource {
    private DBCollection coll;
    public RatingResource(DBCollection coll) {
        this.coll = coll;
    }
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Map<String,Boolean> getLocation(@PathParam("id") String venueId, @QueryParam("rating") int ratingValue) {
        try {
            WriteResult res = coll.update(
                    new BasicDBObject("_id", venueId),
                    new BasicDBObject().append("$inc",
                            new BasicDBObject().append("num_ratings", 1)
                                    .append("sum_ratings", ratingValue)
                    ));
            if (res.getN() != 1) {
                throw new WebApplicationException(404);
            }
        }
        catch (MongoException e) {
            throw new WebApplicationException(500);
        }
        HashMap<String,Boolean> responseMap = new HashMap<String,Boolean>();
        responseMap.put("updated", true);
        return responseMap;
    }
}