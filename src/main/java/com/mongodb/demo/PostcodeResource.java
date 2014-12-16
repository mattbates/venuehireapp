package com.mongodb.demo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/postcode/{postcode}")
@Produces(value = MediaType.APPLICATION_JSON)
public class PostcodeResource {
    private DBCollection coll;
    public PostcodeResource(DBCollection coll) {
        this.coll = coll;
    }
    @GET
    public Map getLocation(@PathParam("postcode") String postcode) {
        BasicDBObject doc = (BasicDBObject)coll.findOne(new BasicDBObject("_id", postcode.toUpperCase()));
        if (doc != null && doc.containsField("location")) {
            BasicDBList location = (BasicDBList) ((BasicDBObject)doc.get("location")).get("coordinates");
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("latitude", location.get(1));
            res.put("longitude", location.get(0));
            return res;
        }
        return null;
    }
}
