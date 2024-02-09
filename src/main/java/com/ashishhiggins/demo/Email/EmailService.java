package com.ashishhiggins.demo.Email;

import com.ashishhiggins.demo.Employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmailToManager(String managerEmail, Employee newEmployee) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(managerEmail);
        message.setSubject("New Employee Added");
        message.setText(newEmployee.getEmployeeName() + " will now work under you. Mobile number is "
                + newEmployee.getPhoneNumber() + " and email is " + newEmployee.getEmail());
        emailSender.send(message);
    }
}
