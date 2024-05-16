package com.voicera;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//FileController.java
@RestController
@RequestMapping("/api")
@CrossOrigin
public class FileController {
    // Define the directory path where you want to store the uploaded files
     private final  String uploadDir = "D:/javapracticevoicera/attendencesheet/uploads";

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file) {
	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("Please select a file to upload.");
	    }

	    try {
	  
	        // Get the original filename
	        String originalFileName = file.getOriginalFilename();

	        // Create the directory if it doesn't exist
	        File uploadDirFile = new File(uploadDir);
	        if (!uploadDirFile.exists()) {
	            uploadDirFile.mkdirs();
	        }

	        // Create a Path to the new file location
	        Path filePath = Paths.get(uploadDir, originalFileName);

	        // Save the file to the specified directory
	        Files.write(filePath, file.getBytes());

	        return ResponseEntity.ok("File uploaded successfully");
	    } catch (Exception e) {
	        System.err.println(e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
	    }
	}
	

@GetMapping("/download/{fileName}")
public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
    // Implement file download logic here
    try {
        File file = new File(uploadDir, fileName);
        
        if (file.exists()) {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}




@GetMapping("/list")
public List<String> listFiles() throws IOException {
    // List all files in the "uploads" folder
    try {
        return Files.list(Paths.get(uploadDir))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    } catch (IOException e) {
        throw new IOException("Failed to list files");
    }
}
}

