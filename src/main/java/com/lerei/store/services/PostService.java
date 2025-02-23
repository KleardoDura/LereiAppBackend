package com.lerei.store.services;

import com.lerei.store.dto.PostDto;
import com.lerei.store.entities.Post;
import com.lerei.store.repositories.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepo postRepo;

    @Autowired
    private EmailSenderService senderService;
    public Integer save(Post post){
        Post savedPost = postRepo.save(post); // Save the entity and get the saved instance
        return savedPost.getId(); // Return the generated ID
    }
    public List<Post> getPostsByUserId(Integer userId) {
        List<Post> posts = postRepo.findByUserId(userId);
        return posts.stream()
                .sorted(Comparator.comparing(Post::getId).reversed())
                .collect(Collectors.toList());
    }


    public List<Post> getAllPost(){
        List<Post> post=postRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return post;
    }

    public List<Post>getPostByCategorySorted (Integer categoryId, String ordering){
        List<Post> posts = new ArrayList<>();
        if(ordering.equals("default")){
           posts=postRepo.findByCategoryId(categoryId, Sort.by(Sort.Direction.DESC, "id"));
        }
        else if(ordering.equals("nameA2Z")){
            posts=postRepo.findByCategoryId(categoryId,Sort.by(Sort.Direction.ASC, "title"));
        }
        else if(ordering.equals("nameZ2A")){
            posts=postRepo.findByCategoryId(categoryId, Sort.by(Sort.Direction.DESC, "title"));
        }
        else if(ordering.equals("priceLow2High")){
            posts=postRepo.findByCategoryId(categoryId,Sort.by(Sort.Direction.ASC, "price"));
        }
        else if(ordering.equals("priceHigh2Low")){
            posts=postRepo.findByCategoryId(categoryId, Sort.by(Sort.Direction.DESC, "price"));
        }
        else{
            posts=postRepo.findByCategoryId(categoryId,Sort.by(Sort.Direction.DESC, "id"));
        }
        return posts;
    }

    public List<Post> getAllPostsOrderBy(String ordering){
        List<Post> posts = new ArrayList<>();
        if(ordering.equals("default")){
            posts=postRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        else if(ordering.equals("nameA2Z")){
            posts=postRepo.findAll(Sort.by(Sort.Direction.ASC, "title"));
        }
        else if(ordering.equals("nameZ2A")){
            posts=postRepo.findAll(Sort.by(Sort.Direction.DESC, "title"));
        }
        else if(ordering.equals("priceLow2High")){
            posts=postRepo.findAll(Sort.by(Sort.Direction.ASC, "price"));
        }
        else if(ordering.equals("priceHigh2Low")){
            posts=postRepo.findAll(Sort.by(Sort.Direction.DESC, "price"));
        }
        else{
            posts=postRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return posts;
    }

    public List<Post> getPostsNotByUserId(Integer userId) {
        return postRepo.findPostsNotByUserId(userId);
    }

    public void sendEmailNewPostAdded(Integer postId, String username){

        // lereimusic@gmail.com
        senderService.sendEmail("kleardodura13@gmail.com",
                "POSTIM I RI",
                "Pershendetje,\n\n" +
                        "Perdoruesi " + username + " sapo shtoje nje postim te ri.\n" +
                        "Per te kontrolluar apo fshire kete postim te ri ju lutem hyni ne portal dhe kilkoni linkun e " +
                        "meposhtem:\n" +
                        "http://localhost:3000/myproduct/"+postId+" \n" +
                        "Faleminderit\n" +
                        "");

    }


}
