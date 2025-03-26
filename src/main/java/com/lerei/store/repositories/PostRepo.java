package com.lerei.store.repositories;

import com.lerei.store.entities.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepo extends JpaRepository<Post,Integer> {
    List<Post> findByUserId(Integer userId);
    List<Post> findByCategoryId(Integer categoryId);
    List<Post> findByCategoryId(Integer categoryId, Sort sort);

    @Modifying
    @Transactional
    @Query(value="UPDATE Post p SET p.first_photo_path = :path WHERE p.id = :id",nativeQuery = true)
    void updateFirstPhotoPathById(@Param("id") Integer id, @Param("path") String path);

    @Modifying
    @Transactional
    @Query(value="UPDATE Post p SET p.second_photo_path = :secondPhotoPath WHERE p.id = :id",nativeQuery = true)
    void updateSecondPhotoPathById(@Param("id") Integer id, @Param("secondPhotoPath") String secondPhotoPath);

    @Modifying
    @Transactional
    @Query(value="UPDATE Post p SET p.third_photo_path = :path WHERE p.id = :id",nativeQuery = true)
    void updateThirdPhotoPathById(@Param("id") Integer id, @Param("path") String path);

    @Modifying
    @Transactional
    @Query(value="UPDATE Post p SET p.fourth_photo_path = :path WHERE p.id = :id",nativeQuery = true)
    void updateFourthPhotoPathById(@Param("id") Integer id, @Param("path") String path);

    @Modifying
    @Transactional
    @Query(value="UPDATE post p SET p.main_photo_path = :mainPhotoPath WHERE p.id = :id ",nativeQuery = true)
    void updateMainPhotoPathById(@Param("id") Integer id, @Param("mainPhotoPath") String mainPhotoPath);

    @Query("SELECT p FROM Post p WHERE p.userId <> :userId ORDER BY p.id DESC")
    List<Post> findPostsNotByUserId(@Param("userId") Integer userId);

    @Query("SELECT p FROM Post p WHERE p.userId = :userId AND p.categoryId = :categoryId ORDER BY p.id DESC")
    List<Post> findByUserIdAndCategoryId(@Param("userId") Integer userId, @Param("categoryId") Integer categoryId);

    @Query("SELECT p FROM Post p WHERE p.userId <> :userId AND p.categoryId = :categoryId ORDER BY p.id DESC")
    List<Post> findByNotUserIdAndCategoryId(@Param("userId") Integer userId, @Param("categoryId") Integer categoryId);
    // findByNotUserIdAndCategoryId

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(:title)")
    List<Post> searchByName(@Param("title") String title);

}
