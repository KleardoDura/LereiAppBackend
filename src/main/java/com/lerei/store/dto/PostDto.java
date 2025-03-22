package com.lerei.store.dto;

import com.lerei.store.entities.Post;
import com.lerei.store.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDto {
//PER MOM S DO PERDORET

        private Integer id;
        private String title;
        private Integer categoryId;
        private String description;
        private String phoneNo;
        private String email;
        private Timestamp publishedDate;
        private Timestamp lastEditDate;
        private Integer price;
        private String currency;
        private Integer userId;
        private Integer views;
        private Boolean isPreOrder;
        private PostStatus status;
        private String mainPhotoPath;
        private String firstPhotoPath;
        private String secondPhotoPath;
        private String thirdPhotoPath;
        private String fourthPhotoPath;

        private String vendor; //userName
        public PostDto(Post post, String vendor) {
                this.id = post.getId();
                this.title = post.getTitle();
                this.categoryId = post.getCategoryId();
                this.description = post.getDescription();
                this.phoneNo = post.getPhoneNo();
                this.email = post.getEmail();
                this.publishedDate = post.getPublishedDate();
                this.lastEditDate = post.getLastEditDate();
                this.price = post.getPrice();
                this.currency = post.getCurrency();
                this.userId = post.getUserId();
                this.views = post.getViews();
                this.isPreOrder = post.getIsPreOrder();
                this.status = post.getStatus();
                this.mainPhotoPath = post.getMainPhotoPath();
                this.firstPhotoPath = post.getFirstPhotoPath();
                this.secondPhotoPath = post.getSecondPhotoPath();
                this.thirdPhotoPath = post.getThirdPhotoPath();
                this.fourthPhotoPath = post.getFourthPhotoPath();
                this.vendor = vendor; // Assign vendor separately
        }


        public PostDto(Post post, String vendor, String email, String phoneNo) {
                this.id = post.getId();
                this.title = post.getTitle();
                this.categoryId = post.getCategoryId();
                this.description = post.getDescription();
                this.phoneNo = phoneNo;
                this.email = email;
                this.publishedDate = post.getPublishedDate();
                this.lastEditDate = post.getLastEditDate();
                this.price = post.getPrice();
                this.currency = post.getCurrency();
                this.userId = post.getUserId();
                this.views = post.getViews();
                this.isPreOrder = post.getIsPreOrder();
                this.status = post.getStatus();
                this.mainPhotoPath = post.getMainPhotoPath();
                this.firstPhotoPath = post.getFirstPhotoPath();
                this.secondPhotoPath = post.getSecondPhotoPath();
                this.thirdPhotoPath = post.getThirdPhotoPath();
                this.fourthPhotoPath = post.getFourthPhotoPath();
                this.vendor = vendor; // Assign vendor separately
        }


}
