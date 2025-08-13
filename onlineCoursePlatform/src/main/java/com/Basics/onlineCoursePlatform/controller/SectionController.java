package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.DTO.SectionDTO;

import com.Basics.onlineCoursePlatform.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/sections")
@SecurityRequirement(name = "bearerAuth")
public class SectionController {
    @Autowired
    private SectionService sectionService;


    @Operation(summary = "Get sections", description = "Get sections of a course")
    @GetMapping
    public ResponseEntity<List<SectionDTO>> getSections(@PathVariable Long courseId, Principal principal) {
        return  sectionService.getSections(courseId,principal);
    }

    @Operation(summary = "Add section", description = "Add a new section to a course")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<SectionDTO> addSection(
            @PathVariable Long courseId,
            @RequestParam("sectionName") String sectionName,
            @RequestParam("description") String description,
            @RequestParam("File") MultipartFile videoFile,
            Principal principal) throws IOException {


        return sectionService.addSection(courseId,sectionName, description,videoFile,principal);
    }

    @Operation(summary = "Update section", description = "Update an existing section")
    @PutMapping(value = "/{sectionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')and @courseSecurityService.isCourseOwner(authentication.name, #id)")
    public ResponseEntity<SectionDTO> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @ModelAttribute SectionDTO sectionDTO,
            @RequestParam(value = "File", required = false) MultipartFile videoFile,
            Principal principal) throws IOException {
      return sectionService.updateSection(courseId,sectionId,sectionDTO,videoFile,principal);
    }




    @Operation(summary = "Delete section", description = "Delete a section")
    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasRole('INSTRUCTOR')and @courseSecurityService.isCourseOwner(authentication.name, #id)")
    public ResponseEntity<String> deleteSection(@PathVariable Long courseId, @PathVariable Long sectionId, Principal principal) {
       return sectionService.deleteSection(courseId,sectionId,principal);
    }
    @Operation(summary = "Get a section")
    @GetMapping("/{sectionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('INSTRUCTOR')")
    public ResponseEntity<SectionDTO> getSection(@PathVariable Long courseId, @PathVariable Long sectionId, Authentication authentication) throws BadRequestException {
        return sectionService.getSection(courseId, sectionId, authentication);
    }

}
