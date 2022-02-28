package com.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEntry {
    /**
     * UserName of the reviewer
     */
    private String userName;

    /**
     * the date that the review was written
     */
    private Date date;

    /**
     * review content
     */
    private String review;

}
