import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.MongoException
import uk.me.jstott.jcoord.OSRef

/**
 * Groovy script to geocode OS grid references (from ONS PD) to lat/lon for Bristol postcodes
 * Matthew Bates, MongoDB
 * November 2014
 */

def mongoClient

try {
    mongoClient = new MongoClient('localhost', 27017)
    def allPostcodes = mongoClient.getDB('demo').getCollection('postcodes')
    def bristolPostcodes = mongoClient.getDB('demo').getCollection('bristol_postcodes')

    // lookup all Bristol postcodes using a regex
    allPostcodes.find(new BasicDBObject([pcd : java.util.regex.Pattern.compile("^BS")])).each { doc ->
        try {
            def id = doc.get('pcd').replaceAll('\\s', '')
            def gridref = new OSRef((int) doc.get('oseast1m'), (int) doc.get('osnrth1m'))
            bristolPostcodes.update(new BasicDBObject('_id', id),
                    new BasicDBObject('$set', [location: [coordinates: [gridref.toLatLng().lng,
                                                                        gridref.toLatLng().lat],
                                                          type: 'Point']]), true, false)
        }
        catch (ClassCastException e) { // ignore postcodes with empty string grid refs
        }
    }
}
catch (MongoException e) { System.err.println(String.format('Database error: %s'.(e.getMessage()))) }
finally {
    if (mongoClient != null) mongoClient.close()
}