package com.gpch.login.controller;

import javax.validation.Valid;

import com.gpch.login.keys.Message;
import com.gpch.login.model.User;
import com.gpch.login.repository.UserRepository;
import com.gpch.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.gpch.login.keys.GenerateKeys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.gpch.login.keys.VerifyMessage;

import java.io.*;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Controller
public class SignatureController {
    @Autowired
    private UserService userService;
    private UserRepository ur;

    /*@RequestMapping(value = "/admin/cryptString",method = RequestMethod.GET)
    public @ResponseBody String test(@RequestParam String data ) throws Exception {
        if(data != null){
        GenerateKeys gk = new GenerateKeys(1024);
        gk.init();

        Message m = new Message(data,"KeyPair/privateKey");
        m.writeToFile("MyData/SignedData.txt");
        return "dqzd";
        }
    }*/

    @RequestMapping(path = "/downloadCrypt", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloader(String param,@RequestParam String data) throws Exception {
        if(data != null) {
            GenerateKeys gk = new GenerateKeys(1024);
            gk.init();

            Message m = new Message(data, "KeyPair/privateKey");
            m.writeToFile("MyData/SignedData.txt");


            File file = new File("MyData/SignedData.txt");
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename="+file.getName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))

                    .body(resource);
        }
        else
            return null;
    }

    @RequestMapping(value = "/downloadUncrypted",method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDec(String param,@RequestParam String data) throws Exception {
        String path = "MyData/"+data;
        VerifyMessage m = new VerifyMessage("MyData/SignedData.txt","KeyPair/publicKey");
        File f = new File(path);
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        System.out.println(m.getOutput());
        writer.write(m.getOutput());
        writer.close();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(f));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=uncryp.txt");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(f.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))

                .body(resource);
    }
}
