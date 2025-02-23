package com.lerei.store.controllers;


import com.lerei.store.repositories.PostRepo;
import com.lerei.store.services.FileValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class FileController {
    private final String baseLocation = "src/main/resources/static/files";
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PostRepo postRepo;

    @GetMapping("/files/{folder}/{subfolder}/{imageName}")
    @ResponseBody
    public Resource serveImage(@PathVariable String folder, @PathVariable String subfolder, @PathVariable String imageName) throws IOException {
        Path imagePath = Paths.get("src/main/resources/static/files/" + folder + "/"+subfolder+"/" + imageName);
        Resource resource = new UrlResource(imagePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Image not found: " + imagePath.toString());
        }
    }
    @PostMapping("/upload")//shto id e postit
    @Transactional
    public String uploadFile(@RequestParam("file") MultipartFile file, Locale locale) throws IOException {
        String message;
        if(!FileValidationService.isImageFile(file)) {
            message = messageSource.getMessage("invalid.file.type",null, locale);
            return message;
        }

        File directory = new File(baseLocation);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directories if they don't exist
        }
        Path targetPath = Paths.get(baseLocation).resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        message = messageSource.getMessage("file.upload.success", null,locale);
        return message;
    }



    public Boolean uploadFileOneByOne(@RequestParam("file") MultipartFile file) throws IOException {

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
    @DeleteMapping("/delete-file")
    public String deleteFile(@RequestParam String filePath, Locale locale) {
        filePath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        try {
            Path fullPath = Paths.get("src/main/resources/static").resolve(filePath).normalize();
            Files.delete(fullPath);  // delete the file if it exists

            // Remove the file extension ".png"
            String input = filePath;
            String withoutExtension = input.substring(0, input.lastIndexOf("."));
            String[] parts = withoutExtension.split("/");
            String lastPart = parts[parts.length - 1];
            String[] numbers = lastPart.split("\\.");

            // 33.1.png
            String postId = numbers[0];  // 33
            // If there's a second part after the dot, use it; otherwise, set to "0"
            String photoPosition = (numbers.length > 1) ? numbers[1] : "0";  // 0 if no second part

            System.out.println(filePath);
            System.out.println(postId);
            System.out.println(photoPosition);
            if(photoPosition.equals("0")) {
                System.out.println("out: 0");
                postRepo.updateMainPhotoPathById(Integer.parseInt(postId), null);
            }
            if(photoPosition.equals("1")) {
                System.out.println("out: 1");
                postRepo.updateFirstPhotoPathById(Integer.parseInt(postId), null);
            }
            if(photoPosition.equals("2"))
                postRepo.updateSecondPhotoPathById(Integer.parseInt(postId), null);
            if(photoPosition.equals("3"))
                postRepo.updateThirdPhotoPathById(Integer.parseInt(postId), null);
            if(photoPosition.equals("4"))
                postRepo.updateFourthPhotoPathById(Integer.parseInt(postId), null);




            String params[] = {filePath};
            return messageSource.getMessage("file.delete.success", params, locale);
        } catch (NoSuchFileException e) {
            return messageSource.getMessage("file.not.found", null, locale);
        } catch (IOException e) {
            return messageSource.getMessage("file.delete.error", null, locale);
        }
    }


    @GetMapping("/list-files") //specify the directory
    public List<String> listFilesInDirectory() {
        try (Stream<Path> paths = Files.walk(Paths.get(baseLocation))) {
            return paths
                    .filter(Files::isRegularFile) // Filter to include only files, excluding directories
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
