package com.gpch.login.controller;


import com.gpch.login.Storage.UploadFileResponse;
import com.gpch.login.Storage.FileStorageService;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gpch.login.keys.Message;
import com.gpch.login.keys.GenerateKeys;
import sun.text.normalizer.UTF16;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;


@RestController
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/up")
    public ModelAndView func(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("uploadToCrypt");
        return modelAndView;
    }

    @GetMapping("/upl")
    public ModelAndView func2(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("uploadToD");
        return modelAndView;
    }

    @PostMapping("/downloadUncrypted")
    public void uploadFileD(@RequestParam("file") MultipartFile file,HttpServletResponse httpResponse) throws IOException {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("MyData/")
                .path(fileName)
                .toUriString();



        new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());

        String fi = fileName;

        httpResponse.sendRedirect("/downloadUncrypted?data="+fi);
    }




    @PostMapping("/uploadFileCrypt")
    public void uploadFileDec(@RequestParam("file") MultipartFile file,HttpServletResponse httpResponse) throws IOException {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("MyData/")
                .path(fileName)
                .toUriString();



        new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());

        byte[] encoded = file.getBytes();
        String re ="";

        Path p = Paths.get("MyData/"+fileName);
        List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
        for(int i=0;i<lines.size();i++){
            re=re+lines.get(i);
        }
        httpResponse.sendRedirect("/downloadCrypt?data="+re);
    }













    /*@PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }*/

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}