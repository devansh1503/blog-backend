FROM ubuntu:latest
LABEL authors="devansh"

ENTRYPOINT ["top", "-b"]


#mongodb+srv://devansh1503:<db_password>@cluster0.u1nfugm.mongodb.net/
#
#
#import com.mongodb.client.MongoClients;
#import org.springframework.data.annotation.Id;
#import org.springframework.data.mongodb.core.MongoTemplate;
#import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
#import org.springframework.data.mongodb.core.mapping.Document;
#import org.springframework.data.mongodb.core.query.Criteria;
#import org.springframework.data.mongodb.core.query.Query;
#
#@Document("movies")
#class Movie {
#    @Id String id;
#    String title;
#    int year;
#}
#
#public class QuickStart {
#    public static void main(String[] args) {
#        String uri = "mongodb+srv://devansh1503:<db_password>@cluster0.u1nfugm.mongodb.net/";
#        MongoTemplate mongoTemplate = new MongoTemplate(
#            new SimpleMongoClientDatabaseFactory(MongoClients.create(uri), "sample_mflix"));
#
#        Movie movie = mongoTemplate.findOne(
#            Query.query(Criteria.where("title").is("Back to the Future")), Movie.class);
#        System.out.println(movie != null ? movie.title + " (" + movie.year + ")" : "No matching movie found.");
#    }
#}