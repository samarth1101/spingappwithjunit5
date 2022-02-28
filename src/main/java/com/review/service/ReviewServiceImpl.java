package com.review.service;

import com.review.dao.ReviewRepository;
import com.review.domain.Review;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Optional<Review> findById(String id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Optional<Review> findByProductId(Integer id) {
        return reviewRepository.findByProductId(id);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review save(Review review) {
        review.setVersion(1);
        return reviewRepository.save(review);
    }

    public Review update(Review review) {
        review.setVersion(review.getVersion()+1);
        return reviewRepository.save(review);
    }

    public void delete(String id) {
        reviewRepository.deleteById(id);
    }

}
