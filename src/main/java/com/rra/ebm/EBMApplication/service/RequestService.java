package com.rra.ebm.EBMApplication.service;

import com.rra.ebm.EBMApplication.domain.Requests;
import com.rra.ebm.EBMApplication.repository.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class RequestService {
    @Value("${upload.path}")
    private String uploadPath;
    private RequestRepo requestRepo;

    @Autowired
    public RequestService(RequestRepo requestRepo) {
        this.requestRepo = requestRepo;
    }

    public void saveRequest(Requests requests, MultipartFile letter, MultipartFile certifcate,
                            MultipartFile vatCertificate, MultipartFile idCard){
        Requests req=requestRepo.save(requests);

        try{
            if(letter!=null){
                String letterPath=saveFile(letter);
                req.setLetterPath(letterPath);

            }
            if(certifcate!=null){
                String certPath=saveFile(certifcate);
                req.setCertPath(certPath);
            }
            if(vatCertificate!=null){
                String vathPath=saveFile(vatCertificate);
                req.setVatPath(vathPath);

            }
            if(idCard!=null){
                String idPath=saveFile(idCard);
                req.setIdPath(idPath);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();


        }
    }
    private String saveFile(MultipartFile file) throws IOException {
        String fileName= UUID.randomUUID().toString()+""+file.getOriginalFilename();
        Path imagesDirectory= Paths.get(uploadPath);
        if(!Files.exists(imagesDirectory)){
            Files.createDirectories(imagesDirectory);
        }
        Path filePath=imagesDirectory.resolve(fileName);
        Files.copy(file.getInputStream(),filePath);
        return fileName;

    }
}
