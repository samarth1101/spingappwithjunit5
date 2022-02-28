package com.review.service;

import com.review.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Optional<Review> findById(String id);

    Optional<Review> findByProductId(Integer id);

    List<Review> findAll();

    Review save(Review review);

    void delete(String id);

}
