package com.mongodb.demo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/venuesearch")
@Produces(value = MediaType.APPLICATION_JSON)
public class VenueApiResource {

    private Client client;
    private String appserver;

    public VenueApiResource(Client client, String apphost, int appport) {
        this.client = client;
        this.appserver = String.format("%s://%s:%d", "http", apphost, appport);
    }

    @GET
    @Path("/by-postcode")
    public List<Venue> getVenuesByPostcode(@QueryParam("postcode") String postcode) {
        if (postcode == null || postcode.equals("")) {
            throw new WebApplicationException(400);
        }
        ArrayList<Venue> venues = new ArrayList<Venue>();
        // lookup location of the postcode using the postcode resource
        ClientResponse response = null;
        try {
            response = client.resource(appserver).
                    path(String.format("api/postcode/%s", postcode)).
                    get(ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new WebApplicationException();
            }
        }
        catch (Exception e) {
            throw new WebApplicationException(500);
        }
        Map location = response.getEntity(HashMap.class);
        if (location != null) {
            MultivaluedMap<String,String> params = new MultivaluedMapImpl();
            params.add("latitude", String.valueOf(location.get("latitude")));
            params.add("longitude", String.valueOf(location.get("longitude")));
            venues = client.resource(appserver).path("api/venue/by-location").queryParams(params).get(new GenericType<ArrayList<Venue>>(){});
        }

        return venues;
    }

    @GET
    @Path("/by-text")
    public List<Venue> getVenuesByText(@QueryParam("searchtext") String searchText) {
        ArrayList<Venue> venues = new ArrayList<Venue>();
        if (searchText == null || searchText.equals("")) {
            venues = client.resource(appserver).path("api/venue").get(new GenericType<ArrayList<Venue>>() {
            });
        }
        else {
            MultivaluedMap<String, String> params = new MultivaluedMapImpl();
            params.add("searchtext", searchText);
            venues = client.resource(appserver).path("api/venue/by-text").queryParams(params).get(new GenericType<ArrayList<Venue>>() {
            });
        }
        return venues;
    }
}