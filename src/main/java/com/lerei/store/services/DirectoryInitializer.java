package com.lerei.store.services;


import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class DirectoryInitializer {

    private final String baseLocation = "/opt/storage/files/"; // Adjust to your desired location

    @PostConstruct //This annotation ensures that the initializeDirectories() method runs after the Spring Boot application context is initialized.
    public void initializeDirectories() {
        for (int i = 0; i < 10; i++) {
            String dirName = "F" + i; // e.g., F0, F1, ..., F9
            File directory = new File(baseLocation, dirName);

            if (!directory.exists()) {
                boolean created = directory.mkdirs(); // Create directory if it doesn’t exist
                if (created) {
                    System.out.println("Created directory: " + directory.getAbsolutePath());
                } else {
                    System.err.println("Failed to create directory: " + directory.getAbsolutePath());
                }
            } else {
                System.out.println("Directory already exists: " + directory.getAbsolutePath());
            }
        }
    }
}
