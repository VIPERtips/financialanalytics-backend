package com.tadiwa.financialanalytics.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSender {

    @Autowired
    private JavaMailSender javaMailSender;
    
    private static final String SUPPORT_EMAIL = "noreply@finpulse.com";
    private static final String SUPPORT_LINK = "https://fin-pulse-tracker.vercel.app";

    public void sendEmail(String toEmail, String subject, String body) {
        
    }
    
    public void sendPasswordResetEmail(String userEmail, String username, String resetToken) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress(SUPPORT_EMAIL,"FinPulse"));
            helper.setTo(userEmail);
            helper.setSubject("FinPulse - Password Reset Request");

            String resetLink = SUPPORT_LINK + "/reset-password?token=" + resetToken;

           
            String htmlMsg = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                "  <div style='max-width: 600px; margin: auto; background: #ffffff; border: 1px solid #dddddd; border-radius: 8px; padding: 30px;'>" +
                "    <h2 style='color: #2c3e50; text-align: center;'>FinPulse</h2>" +
                "    <p>Dear " + username + ",</p>" +
                "    <p>We received a request to reset your FinPulse password. Click the button below to set a new password. This link will expire in 24 hours for security reasons.</p>" +
                "    <div style='text-align: center; margin: 30px 0;'>" +
                "      <a href='" + resetLink + "' style='background-color: #3498db; color: #ffffff; padding: 12px 20px; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                "    </div>" +
                "    <p>If you did not request a password reset, please ignore this email or contact our support team.</p>" +
                "    <hr style='border: none; border-top: 1px solid #eeeeee; margin: 20px 0;'/>" +
                "    <p style='font-size: 12px; color: #999999; text-align: center;'>Best regards,<br/>FinPulse Support Team<br/><a href='" + SUPPORT_LINK + "' style='color: #3498db; text-decoration: none;'>" + SUPPORT_LINK + "</a></p>" +
                "  </div>" +
                "</body>" +
                "</html>";

            helper.setText(htmlMsg, true); // Enable HTML

            javaMailSender.send(message);
            System.out.println("Email sent successfully to " + userEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // You can update other methods similarly if you need HTML formatting.
    
    public void sendAdminNotification(String userEmail) {
        // Update if needed for HTML design
        String subject = "FinPulse - Password Reset Requested";
        String body = String.format(
            "An account password reset has been requested for the following user:<br/><br/>" +
            "<strong>Email:</strong> %s<br/><br/>" +
            "If this was not initiated by the user, please review their account for any suspicious activity.<br/><br/>" +
            "Best regards,<br/>FinPulse System",
            userEmail
        );
        // For simplicity, we'll use sendEmail if plain text is acceptable or update similarly.
        // sendEmail("mytipstadiwa@gmail.com", subject, body);
    }
    
    public void sendRegistrationEmail(String userEmail, String username, String password, String fullname) {
        // Update similarly if HTML is needed
        String subject = "Welcome to FinPulse - Account Created Successfully";

        String body = String.format(
            "Dear %s,<br/><br/>" +
            "Welcome to FinPulse! Your account has been successfully created. Here are your login details:<br/><br/>" +
            "<strong>Username:</strong> %s<br/>" +
            "<strong>Password:</strong> %s<br/><br/>" +
            "To access your account, visit:<br/><a href='%s'>%s</a><br/><br/>" +
            "If you need any assistance, feel free to contact our support team.<br/><br/>" +
            "Best regards,<br/>FinPulse Support Team<br/><a href='%s'>%s</a>",
            fullname, username, password, SUPPORT_LINK, SUPPORT_LINK, SUPPORT_LINK, SUPPORT_LINK
        );

        
    }
}
