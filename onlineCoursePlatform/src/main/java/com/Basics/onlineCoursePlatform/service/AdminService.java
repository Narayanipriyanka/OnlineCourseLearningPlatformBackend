package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.CourseDTO;
import com.Basics.onlineCoursePlatform.DTO.InstructorDTO;
import com.Basics.onlineCoursePlatform.DTO.StudentDTO;
import com.Basics.onlineCoursePlatform.entity.Role;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.exception.EmailAlreadyExistsException;
import com.Basics.onlineCoursePlatform.exception.ForbiddenException;
import com.Basics.onlineCoursePlatform.exception.NotFoundException;

import com.Basics.onlineCoursePlatform.repository.CourseRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;


    @Value("${file.upload-dir}")
    private String uploadDir;
    public List<InstructorDTO> getInstructors(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return userRepository.findByRole(Role.INSTRUCTOR).stream()
                .map(instructor -> modelMapper.map(instructor, InstructorDTO.class))
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getInstructorCourses(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        User instructor = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Instructor not found"));
        return courseRepository.findByInstructor(instructor).stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }
    public List<StudentDTO> getStudents(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return userRepository.findByRole(Role.STUDENT).stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }




    public ResponseEntity<InstructorDTO> addInstructor(MultipartFile avatar, String name, String email, String password, String bio) throws IOException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = modelMapper.map(new InstructorDTO(name, email,passwordEncoder.encode(password),bio,avatar.getOriginalFilename()),User.class);
        user.setRole(Role.INSTRUCTOR);

        String avatarFilename = avatar.getOriginalFilename();

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        avatar.transferTo(new File(uploadDir + avatarFilename));
        User savedUser = userRepository.save(user);



        return ResponseEntity.ok(modelMapper.map(savedUser, InstructorDTO.class));
    }

    private String saveAvatar(MultipartFile avatar) {
        // Implementation...
        try {
            // Create the upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename for the avatar
            String filename = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();

            // Save the avatar file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(avatar.getInputStream(), filePath);

            // Return the URL of the saved avatar file
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save avatar file", e);
        }
    }
    public void deleteInstructor(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Instructor not found");
        }
        userRepository.deleteById(id);
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByEmail(username).orElseThrow(() -> new ForbiddenException("No data found"));
    }
    public void deleteStudent(Long id) {
        User student = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Student not found"));
        if (!student.getRole().equals(Role.STUDENT)) {
            throw new ForbiddenException("Only students can be deleted");
        }
        userRepository.delete(student);
    }

}


