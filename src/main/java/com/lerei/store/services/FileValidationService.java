package com.lerei.store.services;

import com.lerei.store.entities.Message;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class FileValidationService   {

    // List of valid image MIME types
    private static final String[] IMAGE_MIME_TYPES = {
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/tiff",
            "image/webp",
            "image/heif",
            "image/heic"
    };

    public static Message areAllImageFiles( MultipartFile mainFile,
                                            MultipartFile firstFile,
                                            MultipartFile secondFile,
                                            MultipartFile thirdFile,
                                            MultipartFile fourthFile){

        if (mainFile!=null && (!mainFile.isEmpty()) && !isImageFile(mainFile)) {
            System.out.println(mainFile.getOriginalFilename()+" is not a valid image!");
            return new Message(false,mainFile.getOriginalFilename());
        }
        if (firstFile!=null && (!firstFile.isEmpty()) && !isImageFile(firstFile)){
            System.out.println(firstFile.getOriginalFilename()+" is not a valid image!");
            return new Message(false,firstFile.getOriginalFilename());
        }
        if (secondFile!=null && (!secondFile.isEmpty())  && !isImageFile(secondFile)) {
            System.out.println(secondFile.getOriginalFilename()+" is not a valid image!");
            return new Message(false,secondFile.getOriginalFilename());
        }
        if (thirdFile!=null && (!thirdFile.isEmpty())  && !isImageFile(thirdFile)) {
            System.out.println(thirdFile.getOriginalFilename()+" is not a valid image!");
            return new Message(false,thirdFile.getOriginalFilename());
        }
        if (fourthFile!=null && (!fourthFile.isEmpty())  && !isImageFile(fourthFile)) {
            System.out.println(fourthFile.getOriginalFilename()+" is not a valid image!");
            return new Message(false,fourthFile.getOriginalFilename());
        }
        System.out.println("success");
        return  new Message(true,"All valid images");
    }

    public static boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String mimeType = file.getContentType();
        boolean validMimeType = false;
        for (String type : IMAGE_MIME_TYPES) {
            if (type.equalsIgnoreCase(mimeType)) {
                validMimeType = true;
                break;
            }
        }

        // Perform additional check using file header (magic number)
        return validMimeType && hasValidImageHeader(file);
    }



    // Check for valid image header (magic numbers)
    private static boolean hasValidImageHeader(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = new byte[12];
            if (inputStream.read(header) != -1) {
                return isJpeg(header) || isPng(header) ||
                        isHeic(header) || isHeif(header) || isWebp(header) || isBmp(header); //isGif(header); // Add more as needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if header matches JPEG format (magic number: FF D8 FF)
    private static boolean isJpeg(byte[] header) {
        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF){
            System.out.println("Jpeg format");
            return true;
        }
        return false;
    }

    // Check if header matches PNG format (magic number: 89 50 4E 47)
    private static boolean isPng(byte[] header) {
        if ((header[0] & 0xFF) == 0x89 && (header[1] & 0xFF) == 0x50 && (header[2] & 0xFF) == 0x4E && (header[3] & 0xFF) == 0x47){
            System.out.println("Png format");
            return true;
        }
        return false;
    }

     private static boolean isHeif(byte[] header) {
        if ((header[4] & 0xFF) == 0x66 && (header[5] & 0xFF) == 0x74 && (header[6] & 0xFF) == 0x79 && (header[7] & 0xFF) == 0x70
                && (header[8] & 0xFF) == 0x68 && (header[9] & 0xFF) == 0x65 && (header[10] & 0xFF) == 0x69 && (header[11] & 0xFF) == 0x63){
            System.out.println("Heif format");
            return true;
        }
        return false;
    }

    private static boolean isHeic(byte[] header) {
        if ((header[4] & 0xFF) == 0x66 && (header[5] & 0xFF) == 0x74 && (header[6] & 0xFF) == 0x79 && (header[7] & 0xFF) == 0x70
                && (header[8] & 0xFF) == 0x68 && (header[9] & 0xFF) == 0x65 && (header[10] & 0xFF) == 0x69 && (header[11] & 0xFF) == 0x78){
            System.out.println("Heic format");
            return true;
        }
        return false;
    }

    private static boolean isWebp(byte[] header) {
        if ((header[0] & 0xFF) == 0x52 && (header[1] & 0xFF) == 0x49 && (header[2] & 0xFF) == 0x46 && (header[3] & 0xFF) == 0x46
                && (header[8] & 0xFF) == 0x57 && (header[9] & 0xFF) == 0x45 && (header[10] & 0xFF) == 0x42 && (header[11] & 0xFF) == 0x50) {
            System.out.println("Webp format");
            return true;
        }
        return false;
    }
    private static boolean isBmp(byte[] header) {
        if ((header[0] & 0xFF) == 0x42 && (header[1] & 0xFF) == 0x4D){
            System.out.println("BMP format");
            return true;
        }
        return false;
    }
    // Not used
    private static boolean isGif(byte[] header) {
        return (header[0] & 0xFF) == 0x47 && (header[1] & 0xFF) == 0x49 && (header[2] & 0xFF) == 0x46;
    }

}