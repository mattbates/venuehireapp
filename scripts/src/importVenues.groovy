import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.MongoException

/**
 * Groovy script to import Bristol's community venues from Council XML web service
 * Matthew Bates, MongoDB
 * November 2014
 */

def mongoClient

try {
    mongoClient = new MongoClient("localhost", 27017)
    def venues = mongoClient.getDB('demo').getCollection('venues')
    venues.drop()
    def postcodes = mongoClient.getDB('demo').getCollection('bristol_postcodes')

    def xmlSlurper = new XmlSlurper().parse('http://www.bristol.gov.uk/1149/')

    xmlSlurper.CommunityCentres.CommunityCentre.each { node ->
        node.each { child ->
            // create a map to represent the doc
            def doc = [_id    : child.Id.text(),
                       name   : child.Name.text(),
                       address: [line1   : child.AddressOne.text(),
                                 line2   : child.AddressTwo.text(),
                                 city    : child.AddressThree.text(),
                                 postcode: child.Postcode.text().replaceAll("\\s", "")]]
            // lookup the full location (with lat/lon) based on the postcode
            def location = postcodes.findOne(new BasicDBObject('_id', child.Postcode.text().replaceAll("\\s", "")))
            if (location) doc.location = location.location
            venues.insert(doc as BasicDBObject)
        }
    }

    // create indexes
    venues.createIndex(new BasicDBObject('location', '2dsphere'))
    venues.createIndex(new BasicDBObject('$**', 'text'))
}
catch (ClassCastException e) {}
catch (MongoException e) { System.err.println(String.format("Database error: %s".(e.getMessage()))) }
finally {
    if (mongoClient != null) mongoClient.close()
}
