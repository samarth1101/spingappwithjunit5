package com.review.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.review.dao.ReviewRepository;
import com.review.domain.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@DataMongoTest
public class ReviewRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReviewRepository reviewRepository;

    private ObjectMapper mapper = new ObjectMapper();

    private static File SAMPLE_JSON = Paths.get("src", "test", "resources", "data", "sample.json").toFile();


    @BeforeEach
    void beforeEach() throws Exception {
        //Deserialize JSON file to array of reviews
        Review[] objects = mapper.readValue(SAMPLE_JSON, Review[].class);

        // Load each Review into MongoDB
        Arrays.stream(objects).forEach(mongoTemplate::save);
    }

    @AfterEach
    void afterEach() {
        //Drop the reviews collection so we can start fresh
        mongoTemplate.dropCollection("Reviews");
    }

    @Test
    void testFindAll() {
        List<Review> reviews =  reviewRepository.findAll();
        Assertions.assertEquals(2, reviews.size(), "should be 2 reviews in the DB");
    }
}
