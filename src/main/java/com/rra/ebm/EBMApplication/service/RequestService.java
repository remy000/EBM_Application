package com.rra.ebm.EBMApplication.service;

import com.rra.ebm.EBMApplication.domain.Requests;
import com.rra.ebm.EBMApplication.repository.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RequestService {
    @Value("${upload.path}")
    private String uploadPath;
    private final RequestRepo requestRepo;

    @Autowired
    public RequestService(RequestRepo requestRepo) {
        this.requestRepo = requestRepo;
    }

    public void saveRequest(Requests requests, MultipartFile letter, MultipartFile certifcate,
                            MultipartFile vatCertificate, MultipartFile idCard) throws IOException {
        String letterPath=saveFile(letter);
        String certPath=saveFile(certifcate);
        String vathPath=saveFile(vatCertificate);
        String idPath=saveFile(idCard);
        Requests req=new Requests();

        try{
            req.setTinNumber(requests.getTinNumber());
            req.setSerialNumber(requests.getSerialNumber());
            req.setOwner(requests.getOwner());
            req.setEbmType(requests.getEbmType());
            req.setStatus(requests.getStatus());
            req.setRequestDate(LocalDate.now());
            req.setLetterPath(letterPath);
            req.setCertPath(certPath);
            req.setVatPath(vathPath);
            req.setIdPath(idPath);
            requestRepo.save(req);

        }
        catch (Exception ex){
            ex.printStackTrace();


        }
    }
    private String saveFile(MultipartFile file) throws IOException {
        String fileName= UUID.randomUUID().toString()+""+file.getOriginalFilename();
        Path filesDirectory= Paths.get(uploadPath);
        if(!Files.exists(filesDirectory)){
            Files.createDirectories(filesDirectory);
        }
        Path filePath=filesDirectory.resolve(fileName);
        Files.copy(file.getInputStream(),filePath);
        return fileName;
    }

    public List<Requests>allRequests(){
        return requestRepo.findAll();
    }

    public Requests findRequest(int id){
        return requestRepo.findById(id).orElse(null);
    }
    public Requests findByTin(int tin){
        return requestRepo.findByTinNumber(tin).orElse(null);
    }

    @Transactional
    public void updateRequest(int tin, String status){
        Requests req=requestRepo.findByTinNumber(tin).orElse(null);
        if(req!=null){
            req.setStatus(status);
            requestRepo.save(req);

        }
    }
}






















