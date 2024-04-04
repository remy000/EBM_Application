package com.rra.ebm.EBMApplication.controller;

import com.rra.ebm.EBMApplication.domain.Requests;
import com.rra.ebm.EBMApplication.domain.Users;
import com.rra.ebm.EBMApplication.service.EmailService;
import com.rra.ebm.EBMApplication.service.RequestService;
import com.rra.ebm.EBMApplication.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
public class RequestController {
    private final RequestService requestService;
    private final UsersService usersService;
    private final EmailService emailService;

    @Autowired
    public RequestController(RequestService requestService, UsersService usersService, EmailService emailService) {
        this.requestService = requestService;
        this.usersService = usersService;
        this.emailService = emailService;
    }




    @PostMapping(value = "/saveApplication")
    @PreAuthorize("hasAuthority('taxpayer')")
    public ResponseEntity<?>saveRequest(  @ModelAttribute Requests requests,
                                          @RequestParam(name = "letter", required = false) MultipartFile letter,
                                          @RequestParam(name = "certificate", required = false) MultipartFile certificate,
                                          @RequestParam(name = "vatCertificate", required = false) MultipartFile vatCertificate,
                                          @RequestParam(name = "idCard", required = false) MultipartFile idCard){
        try{
            int tin=requests.getTinNumber();
            Users user=usersService.findUser(tin);
            String subject="Application Received";
            String text= "Thank you for applying to get EBM Software, your application has been received " +
                    "we will review it and we will be back to you";
            String email = user.getEmail();
            requestService.saveRequest(requests,letter,certificate,vatCertificate,idCard);

                emailService.sendingEmails(email,subject,text);

            return new ResponseEntity<>("application saved", HttpStatus.OK);


        }
        catch (Exception ex){
            return  new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }
    @GetMapping(value = "/allApplications")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?>allRequests(){
        try {
            List<Requests> allRequests = requestService.allRequests();
            return new ResponseEntity<>(allRequests,HttpStatus.OK);
        }
        catch (Exception ex){
            return  new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping(value = "/findApplication/{tin}")
    @PreAuthorize("hasAnyAuthority('admin','taxpayer')")
    public ResponseEntity<?>findRequest(@PathVariable("tin") int tin){
        try {
            Requests req=requestService.findByTin(tin);
            if(req!=null){
               String letterPath="http://localhost:8080/files/" + req.getLetterPath();
                String certificatePath="http://localhost:8080/files/"+req.getCertPath();
                String vatPath="http://localhost:8080/files/"+req.getVatPath();
                String idPath="http://localhost:8080/files/"+req.getIdPath();
               req.setLetterPath(letterPath);
                req.setCertPath(certificatePath);
                req.setVatPath(vatPath);
                req.setIdPath(idPath);
                return  new ResponseEntity<>(req,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("No requests",HttpStatus.NOT_FOUND);

            }
        }
        catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    @PostMapping(value = "/approve/{tin}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?>confirmRequest(@PathVariable int tin,  @RequestBody Map<String, String> requestBody){
        Users users=usersService.findUser(tin);
        String feedback = requestBody.get("feedback");
        if(users!=null){
            Requests req=requestService.findByTin(tin);
            String email=users.getEmail();
            String subject="Application Approved";
            if(req!=null){
                req.setStatus("approved");
                requestService.updateRequest(tin,"approved");
                emailService.sendingEmails(email,subject,feedback);
                return new ResponseEntity<>("application approved",HttpStatus.OK);

            }
        }
        return new ResponseEntity<>("User is null", HttpStatus.NOT_FOUND);

    }

    @PostMapping(value = "/reject")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?>rejectRequest(@RequestParam int tin, @RequestParam String feedback){
        Users users=usersService.findUser(tin);
        if(users!=null){
            Requests req=requestService.findByTin(tin);
            String email=users.getEmail();
            String subject="Application Approved";
            if(req!=null){
                req.setStatus("approved");
                requestService.updateRequest(tin,"rejected");
                emailService.sendingEmails(email,subject,feedback);
                return new ResponseEntity<>("application rejected",HttpStatus.OK);

            }
        }
        return new ResponseEntity<>("User is null", HttpStatus.NOT_FOUND);

    }

}
