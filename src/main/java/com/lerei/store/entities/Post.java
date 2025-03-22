package com.lerei.store.entities;

import com.lerei.store.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name="`post`")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String title;
    @NotNull
    @Column(name = "category_id")
    private Integer categoryId;
    private String description;
    @Column(name = "phone_no")
    private String phoneNo;
    private String email;
    @Column(name = "published_date")
    private Timestamp publishedDate;
    @Column(name = "last_edit_date")
    private Timestamp lastEditDate;
    private Integer price;
    private String currency;
    @Column(name="user_id")
    private Integer userId;
    private Integer views;
    @Column(name="is_pre_order")
    private Boolean isPreOrder;
    @Enumerated(EnumType.STRING)
    private PostStatus status;
    @Column(name="user_name")
    private String vendor;

    @Column(name = "main_photo_path")
    private String mainPhotoPath;
    @Column(name = "first_photo_path")
    private String firstPhotoPath;
    @Column(name = "second_photo_path")
    private String secondPhotoPath;
    @Column(name = "third_photo_path")
    private String thirdPhotoPath;
    @Column(name = "fourth_photo_path")
    private String fourthPhotoPath;

}
