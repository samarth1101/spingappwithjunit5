package com.review.api;

import com.review.domain.Review;
import com.review.domain.ReviewEntry;
import com.review.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    /**
     * Returns the review with the specified ID
     *
     * @param id The ID of the review to return
     * @return      The review with the specified ID, or 404 Not Found
     */

    @GetMapping("/review/{id}")
    public ResponseEntity<?> getReview(@PathVariable String id) {
        return service.findById(id)
                .map(review -> {
                    try{
                        return ResponseEntity
                                .ok()
                                .eTag(Integer.toString(review.getVersion()))
                                .location((new URI("/review/" + review.getId())))
                                .body(review);
                    }catch (URISyntaxException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns either all reviews or the review of the specified productId
     * @param productId     The productId for the review to return. This request parameter is optional,
     *                      if it is omitted then all reviews are returned
     * @return          List of reviews
     */
    @GetMapping("/reviews")
    public Iterable<Review> getReviews(@RequestParam(value="productId", required = false) Optional<Integer> productId) {
        return productId.map(pid -> service.findByProductId(pid)
                .map(Arrays::asList)
                .orElseGet(ArrayList::new)).orElse(service.findAll());
    }

    @PostMapping("/review")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        logger.info("Creating new review for the product id: {}, {}", review.getProductId(), review);

        //Set the date for any entries in the review to now since we are creating the review now
        review.getEntries().forEach(reviewEntry -> reviewEntry.setDate(new Date()));

        // Save the review to the database
        Review newReview = service.save(review);
        logger.info("Saved Review {}",review);

        try{
            //Build a created response
            return ResponseEntity
                    .created(new URI("/review/" + newReview.getId()))
                    .eTag(Integer.toString(newReview.getVersion()))
                    .body(newReview);

        }catch(URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adds entry in the review of specified product ID
     * @param productId product ID which review needs to be updated
     * @param entry     review entry to be added in the review of the specific product ID
     * @return          updated review
     */

    @PostMapping("/review/{productId}/entry")
    public ResponseEntity<Review> addEntryToReview(@PathVariable Integer productId, @RequestBody ReviewEntry entry) {
        logger.info("Add review entry to product id: {}, {}", productId, entry);

        //Retrieve the review for the specified productId; if there is no review , create a new one
        Review review = service.findByProductId(productId).orElseGet(() -> new Review(productId));
        entry.setDate(new Date());
        review.getEntries().add(entry);

        //Save the review
        Review updatedReview = service.save(review);
        logger.info("Update Review: {}", review);

        try{
            return ResponseEntity
                    .ok()
                    .location(new URI("/review/" + updatedReview.getId()))
                    .eTag(Integer.toString(updatedReview.getVersion()))
                    .body(updatedReview);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/review/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id) {
        logger.info("Deleting review with ID {}", id);

        //get the existing Review
        Optional<Review> existingReview = service.findById(id);

        //delete the review if it exists in the database
        return existingReview.map(review -> {
            service.delete(review.getId());
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }



}
