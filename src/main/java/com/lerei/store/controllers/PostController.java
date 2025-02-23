package com.lerei.store.controllers;

import com.lerei.store.dto.PostDto;
import com.lerei.store.entities.Message;
import com.lerei.store.entities.Post;
import com.lerei.store.repositories.PostRepo;
import com.lerei.store.security.entities.User;
import com.lerei.store.security.respositories.UserRepository;
import com.lerei.store.services.FileValidationService;
import com.lerei.store.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

@RestController
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepo postRepo;
    private FileController fileController = new FileController();
    private final String baseLocation = "src/main/resources/static/files";
    private final String filesFolder = "/files";
    @Transactional
    @PostMapping(value = "/uploadPost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addNewPost(
            @RequestParam(value = "mainFile", required = false) MultipartFile mainFile,
            @RequestParam(value = "firstFile", required = false) MultipartFile firstFile,
            @RequestParam(value = "secondFile", required = false) MultipartFile secondFile,
            @RequestParam(value = "thirdFile", required = false) MultipartFile thirdFile,
            @RequestParam(value = "fourthFile", required = false) MultipartFile fourthFile,
            @RequestPart(value = "post", required = false) Post post,
            Locale locale) throws IOException {
        Message message=FileValidationService.areAllImageFiles(mainFile, firstFile, secondFile, thirdFile, fourthFile);
        if(message.getIsWorking()==false){
            String messageBody[]= {message.getInfoMessage()};//filename
            String errorMessage = messageSource.getMessage("invalid.file",messageBody,locale);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();
        post.setUserId(user.getId());


        post.setPublishedDate(new Timestamp(System.currentTimeMillis()));
        Integer newPostId=postService.save(post);// JOOOO
        post.setViews(0);
        String sms;
        if(mainFile!=null && (!mainFile.isEmpty())){
            sms=uploadFileOneByOne(mainFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString());
            System.out.println(sms);
            post.setMainPhotoPath(sms);
        }
        else{
          //  vendos referencen ne DB tek nje dummy file si 0.png
        }
        if(firstFile!=null && (!firstFile.isEmpty())){
            sms=uploadFileOneByOne(firstFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString()+".1");
            System.out.println(sms);
            post.setFirstPhotoPath(sms);
        }
        if(secondFile!=null && (!secondFile.isEmpty())){
            sms=uploadFileOneByOne(secondFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString()+".2");
            System.out.println(sms);
            post.setSecondPhotoPath(sms);
        }
        if(thirdFile!=null && (!thirdFile.isEmpty())){
            sms=uploadFileOneByOne(thirdFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString()+".3");
            System.out.println(sms);
            post.setThirdPhotoPath(sms);
        }
        if(fourthFile!=null && (!fourthFile.isEmpty())){
            sms=uploadFileOneByOne(fourthFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString()+".4");
            System.out.println(sms);
            post.setFourthPhotoPath(sms);
        }
        if(user.getId()!=1) {
            postService.sendEmailNewPostAdded(post.getId(), post.getVendor());
        }
        String returnMessage = messageSource.getMessage("post.upload.success",null,locale);
        return new ResponseEntity<>(returnMessage, HttpStatus.OK);

    }

    @Transactional
    @PostMapping(value = "/updatePost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(
            @RequestParam(value = "mainFile", required = false) MultipartFile mainFile,
            @RequestParam(value = "firstFile", required = false) MultipartFile firstFile,
            @RequestParam(value = "secondFile", required = false) MultipartFile secondFile,
            @RequestParam(value = "thirdFile", required = false) MultipartFile thirdFile,
            @RequestParam(value = "fourthFile", required = false) MultipartFile fourthFile,
            @RequestPart(value = "post", required = false) Post updatedPost,
            Locale locale) throws IOException {
        Message message=FileValidationService.areAllImageFiles(mainFile, firstFile, secondFile, thirdFile, fourthFile);
        if(message.getIsWorking()==false){
            String messageBody[]= {message.getInfoMessage()};//filename
            String errorMessage = messageSource.getMessage("invalid.file",messageBody,locale);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();
        Post post = postRepo.findById(updatedPost.getId()).orElseThrow();

        post.setLastEditDate(new Timestamp(System.currentTimeMillis()));
        post.setTitle(updatedPost.getTitle());
        post.setPrice(updatedPost.getPrice());
        post.setCurrency(updatedPost.getCurrency());
        post.setVendor(updatedPost.getVendor());
        post.setPhoneNo(updatedPost.getPhoneNo());
        post.setEmail(updatedPost.getEmail());
        post.setCategoryId(updatedPost.getCategoryId());
        post.setDescription(updatedPost.getDescription());
        post.setIsPreOrder(updatedPost.getIsPreOrder());
        postService.save(post);
        //Integer newPostId=postService.save(post);// JOOOO
        Integer newPostId = post.getId();
        String sms;
        if(mainFile!=null && (!mainFile.isEmpty())){
            sms=uploadFileOneByOne(mainFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString());
            System.out.println(sms);
            post.setMainPhotoPath(sms);
        }
        else{
            //  vendos referencen ne DB tek nje dummy file si 0.png
        }
        if(firstFile!=null && (!firstFile.isEmpty())){
            sms=uploadFileOneByOne(firstFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString()+".1");
            System.out.println(sms);
            post.setFirstPhotoPath(sms);
        }
        if(secondFile!=null && (!secondFile.isEmpty())){
            sms=uploadFileOneByOne(secondFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString()+".2");
            System.out.println(sms);
            post.setSecondPhotoPath(sms);
        }
        if(thirdFile!=null && (!thirdFile.isEmpty())){
            sms=uploadFileOneByOne(thirdFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString()+".3");
            System.out.println(sms);
            post.setThirdPhotoPath(sms);
        }
        if(fourthFile!=null && (!fourthFile.isEmpty())){
            sms=uploadFileOneByOne(fourthFile,"F"+newPostId%10,newPostId.toString(),newPostId.toString()+".4");
            System.out.println(sms);
            post.setFourthPhotoPath(sms);
        }



        //String returnMessage = messageSource.getMessage("post.upload.success",null,locale);
        return new ResponseEntity<>("Postimi u perditesua me sukses", HttpStatus.OK);

    }

    @GetMapping("/get-all-my-posts")
    public List<Post> getAllMyPosts(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();

        return postService.getPostsByUserId(user.getId());
    }

    @GetMapping("/get-all-others-posts")
    public List<Post> getAllOthersPosts(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();
        if (user.getId()!=1) return null;
        return postService.getPostsNotByUserId(1);
    }
    @GetMapping("/get-all-posts")
    public List<Post> getAllPosts(){
        return postService.getAllPost();
    }

    @GetMapping("/posts/order/{order}")
    public List<Post> getAllPostsOrderBy(@PathVariable String order){
        return postService.getAllPostsOrderBy(order);
    }
    @GetMapping("/get-post-by-id/{postId}")
    public PostDto getPostById(@PathVariable Integer postId){
        Post post = postRepo.findById(postId).orElse(null);
        if (post==null) return null;
        User user = userRepository.findById(post.getUserId()).orElseThrow();
        String userName;
        if(user.getId()==1) userName=user.getUserName();
        else {
            //User admin=userRepository.findById(1).orElseThrow();
            //userName=admin.getUserName();
            userName="Lerei Music Store";
        }
        post.setViews(post.getViews()+1);
        postRepo.save(post);
        PostDto postDto=new PostDto(post,userName);
        return postDto;
    }

    @GetMapping("/get-my-post-by-id/{postId}")
    public Post getMyPostById(@PathVariable Integer postId){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();
        Post post = postRepo.findById(postId).orElse(null);
        if (post==null) return null;
        if (post.getUserId()!=user.getId()  &&  user.getId()!=1) return null;

        if(user.getId()==1) userName=user.getUserName();
        else {
            //User admin=userRepository.findById(1).orElseThrow();
            //userName=admin.getUserName();
            userName="Lerei Music Store";
        }
        return post;
    }
    @GetMapping("/get-post-by-category-id/{categoryId}")
    public List<Post> getPostByCategory(@PathVariable Integer categoryId){
        return postRepo.findByCategoryId(categoryId);
    }

    @GetMapping("/posts/category/{categoryId}/order/{order}")
    public List<Post> getPostByCategorySorted(@PathVariable Integer categoryId, @PathVariable String order){
        return postService.getPostByCategorySorted(categoryId, order);
    }


    @GetMapping("/get-lerei-post-by-category-id/{categoryId}")
    public List<Post> getLereiPostByCategory(@PathVariable Integer categoryId){
        return postRepo.findByUserIdAndCategoryId(1,categoryId);
    }
    @GetMapping("/get-others-post-by-category-id/{categoryId}")
    public List<Post> getOthersPostByCategory(@PathVariable Integer categoryId){
        return postRepo.findByNotUserIdAndCategoryId(1,categoryId);
    }
    @PostMapping("/uploadTest2")
    public String addOnePost(@RequestParam("file") MultipartFile mainFile) throws IOException {
        String status=uploadFileOneByOne(mainFile,"F1","1","1.1");

        return  status.toString();
    }

    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<?> deletePostByID(@PathVariable Integer postId){


        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = userDetails.getUsername();
        User user = userRepository.findByUserName(userName).orElseThrow();
        if(user.getId()==1){
            postRepo.deleteById(postId);
            return new ResponseEntity<String>("Postimi u fshi me sukses",HttpStatus.OK);
        }
        Post post = postRepo.findById(postId).orElseThrow();
        if(post.getUserId()==user.getId()){
            postRepo.deleteById(postId);
            return new ResponseEntity<String>("Postimi u fshi me sukses",HttpStatus.OK);
        }

        return new ResponseEntity<>("Ju nuk keni te drejte ta fshini kete postim",HttpStatus.NOT_ACCEPTABLE);
    }

    public String uploadFileOneByOne(@RequestParam("file") MultipartFile file,String F_Folder, String subfolder, String fileName) throws IOException {

        /* Po e kontrollojme kete tek metoda thirrese
        if (!FileValidationService.isImageFile(file)) {
            return false;
        }
         */
        File directory = new File(baseLocation);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directories if they don't exist
        }

        String fullSubfolderPath=directory+"/"+F_Folder+"/"+subfolder;
        directory=new File(fullSubfolderPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directories if they don't exist
        }

        // Generate a unique name for the file
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String newFileName = fileName + fileExtension; // e.g., "a1b2c3d4.jpg"

        // Define the target path with the new name
        Path targetPath = Paths.get(fullSubfolderPath).resolve(newFileName);

        // Copy the file to the target directory
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        String pathStoredInDB = filesFolder+"/"+F_Folder+"/"+subfolder+"/"+newFileName;
        //return targetPath.toString();
        return pathStoredInDB;
    }
    public Boolean uploadFileOneByOne1(@RequestParam("file") MultipartFile file) throws IOException {

        if(!FileValidationService.isImageFile(file)) {
            return false;
        }

        File directory = new File(baseLocation);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directories if they don't exist
        }
        Path targetPath = Paths.get(baseLocation).resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    @PostMapping("/testPost")
    public Integer testPost( @RequestBody Post post){
        Integer i=postService.save(post);
        System.out.println(i);
        return i;
    }
}
