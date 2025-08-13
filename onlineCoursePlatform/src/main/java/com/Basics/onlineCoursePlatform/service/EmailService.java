package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.Role;
import com.Basics.onlineCoursePlatform.entity.Section;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

@Service
public class EmailService {
@Autowired
private UserRepository userRepository;
    @Value("${app.url}")
    private String appUrl;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        javaMailSender.send(mailMessage);
    }
    public void sendEmailToStudents(Course course, String action) {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        String subject = getSubject(course, action);
        String body = getEmailBody(course, action);
        students.forEach(student -> {
            try {
                sendHtmlEmail(student.getEmail(), subject, body);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getSubject(Course course, String action) {
        String actionMessage = getActionMessage(action);
        return "Course " + actionMessage + ": " + course.getTitle();
    }

    private String getActionMessage(String action) {
        switch (action) {
            case "add":
                return "Added";
            case "update":
                return "Updated";
            case "publish":
                return "Published";
            default:
                return "";
        }
    }

    private String getEmailBody(Course course, String action) {
        String actionMessage = getActionMessage(action);
        return "<html><body>" +
                "<h2>Course " + actionMessage + ": " + course.getTitle() + "</h2>" +
                "<p>The course details are as follows:</p>" +
                "<ul>" +
                "<li><b>Course Name:</b> " + course.getTitle() + "</li>" +
                "<li><b>Price:</b> " + course.getPrice() + "</li>" +
                "<li><b>Description:</b> " + course.getDescription() + "</li>" +
                "<li><b>Category:</b> " + course.getCategory() + "</li>" +
                "<li><b>Level:</b> " + course.getLevel() + "</li>" +
                "<li><b>Instructor:</b> " + course.getInstructor().getName() + "</li>" +
                "</ul>" +
                "<h3>Course Sections:</h3>" +
                getCourseSections(course) +
                "<a href='" + appUrl + "/courses/" + course.getId() + "' style='background-color: #4CAF50; color: #fff; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; text-decoration: none;'>View Course</a>" +
                "</body></html>";
    }

    private String getCourseSections(Course course) {
        List<Section> sections = course.getSections();
        if (sections == null || sections.isEmpty()) {
            return "<p>No sections available.</p>";
        }
        StringBuilder sectionHtml = new StringBuilder("<ul>");

        for (Section section : sections) {
            sectionHtml.append("<li>").append(section.getSectionName()).append("</li>");
        }
        sectionHtml.append("</ul>");
        return sectionHtml.toString();
    }

    public void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            // Handle exception
        }
    }
    public void sendRegistrationEmail(User user) {
        String subject = "Registration Successful";
        String body = "Dear " + user.getName() + ",\n\n" +
                "You have successfully registered with our platform.\n\n" +
                "Best regards,\n" +
                "Your Platform Team";
        sendEmail(user.getEmail(), subject, body);
    }

    public void sendPasswordResetEmail(User user) {
        String subject = "Password Reset Successful";
        String body = "Dear " + user.getName() + ",\n\n" +
                "Your password has been reset successfully.\n\n" +
                "If you did not perform this action, please contact us immediately.\n\n" +
                "Best regards,\n" +
                "Your Platform Team";
        sendEmail(user.getEmail(), subject, body);
    }

    public void sendPasswordChangeEmail(User user) {
        String subject = "Password Changed Successfully";
        String body = "Dear " + user.getName() + ",\n\n" +
                "Your password has been changed successfully.\n\n" +
                "You can now login with your new password.\n\n" +
                "Best regards,\n" +
                "Your Platform Team";
        sendEmail(user.getEmail(), subject, body);
    }



}
