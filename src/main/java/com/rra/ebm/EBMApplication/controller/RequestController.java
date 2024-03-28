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
            String text= "Thank you for applying to get EBM, your application has been received " +
                    "we will review it and we will be back to you shortly";
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
    @PreAuthorize("hasAuthority('taxpayer')")
    public ResponseEntity<?>findRequest(@PathVariable("tin") int tin){
        try {
            Requests req=requestService.findByTin(tin);
            if(req!=null){
                return  new ResponseEntity<>(req,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("No requests",HttpStatus.BAD_REQUEST);

            }
        }
        catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
