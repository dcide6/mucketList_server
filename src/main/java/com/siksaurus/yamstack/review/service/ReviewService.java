package com.siksaurus.yamstack.review.service;


import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.repository.AccountRepository;
import com.siksaurus.yamstack.review.controller.ReviewDTO;
import com.siksaurus.yamstack.review.controller.ReviewVO;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.domain.repository.ReviewRepository;
import com.siksaurus.yamstack.review.domain.repository.ReviewQueryRepository;
import com.siksaurus.yamstack.review.s3upload.S3Uploader;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final YamService yamService;
    private final S3Uploader s3Uploader;

    /* 여기얌 - 리뷰 리스트 조회*/
    public Page<ReviewVO> getReviewList(Pageable pageable, String email) {
        return reviewQueryRepository.findReviewsDynamicQuery(pageable, email);
    }

    /* 얌/여기얌 - 리뷰 상세 조회*/
    public ReviewVO getReviewById(Long id, String email) {
        return reviewQueryRepository.findReviewDynamicQuery(id, email);
    }

    /* 얌 - 리뷰 등록*/
    @Transactional
    public Long createReview(ReviewDTO.CreateReviewDTO dto, MultipartFile multipartFile) throws IOException {
        String filePath = s3Uploader.upload(multipartFile, "user-upload");
        dto.setImagePath(filePath);
        Yam yam = yamService.getYamById(dto.getYam().getId());
        dto.setYam(yam);
        return reviewRepository.save(dto.toEntity()).getId();
    }

    /* 얌 - 리뷰 삭제*/
    @Transactional
    public String deleteReview(Long review_id, String email) {
        Account account = accountRepository.findByEmail(email).get();
        Optional<Review> review = reviewRepository.findById(review_id);
        if (!review.isPresent()){
            return "Bad request: The review does not exist.";
        }
        if (account.equals(review.get().getYam().getAccount())) {
            String filePath = review.get().getImagePath();
            String msg = "";
            if (filePath != null){
                String fileName = filePath.substring(filePath.lastIndexOf("/"));
                String dirName = filePath.substring(0, filePath.lastIndexOf("/"));
                dirName = dirName.substring(dirName.lastIndexOf("/")+1);
                boolean deletedFile = s3Uploader.delete(dirName+fileName);
                if (deletedFile) msg = "\nImage file [" + dirName + fileName + "] has been deleted from S3.";
                else msg = "\nFailed to delete the image file.";
            }

            reviewRepository.deleteById(review_id);
            return "Review [ " + review_id + " ] has been deleted." + msg;
        }else{
            return "Bad request: Only reviewers can delete reviews.";
        }
    }
}
