package bim.bam.mediaservice.controller;

import bim.bam.mediaservice.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLConnection;

@Slf4j
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestParam("file") MultipartFile file) {
        return mediaService.uploadFile(file);
    }

    @GetMapping("/get-url/{fileName}")
    public String getFileUrl(@PathVariable String fileName) {
        return mediaService.getFileUrl(fileName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String fileName) {
        ByteArrayResource resource = mediaService.downloadFile(fileName);

        String contentType = URLConnection.guessContentTypeFromName(fileName);

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-type", contentType)
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

}
