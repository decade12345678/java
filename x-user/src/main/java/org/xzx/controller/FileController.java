package org.xzx.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource; // 正确
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xzx.Result.Result;
import org.xzx.entity.dto.FileMessageDTO;
import org.xzx.exception.BaseException;
import org.xzx.service.FileService;


import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Api(tags = "文件多媒体消息管理接口")
@RequestMapping("/file")
public class FileController {

    @Value("${upload.dir}")
    private String uploadDir;
    @javax.annotation.Resource
    private FileService fileService;
    @ApiOperation(value = "文件发送")
    @PostMapping("/FileSend")
    public Result sendFile(@RequestParam("file") MultipartFile file,
                           @RequestParam("send_id") Long send_id,
                           @RequestParam("hear_id") Long hear_id,
                           @RequestParam("file_name") String file_name,
                           @RequestParam("file_size") String file_size,
                           @RequestParam("file_type") String file_type,
                           @RequestParam("send_image") String send_image,
                           @RequestParam("send_name") String send_name) throws IOException {
        // 1. 文件保存到服务器指定目录
        Path path = Paths.get(uploadDir, file_name);
        String preview_url = uploadDir + "/" + file_name;
        Files.createDirectories(path.getParent());
        file.transferTo(path);
        //2.交给业务层进行一系列操作
        fileService.FileSend(send_id, hear_id, file_name, file_size, file_type, preview_url, send_image, send_name);
        return Result.success();
        
    }

    @ApiOperation(value = "文件接收")
    @GetMapping("/download/{file_url}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String file_url,
            @RequestParam(required = false) String name) throws IOException {
        //从服务器当前指定目录下找文件
        Path filePath = Paths.get(uploadDir, file_url).normalize();
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        //找到这个resource返回出去
        Resource resource = new FileSystemResource(filePath);
        String contentType = Files.probeContentType(filePath);
        String filename = name != null ? name : filePath.getFileName().toString();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        Optional.ofNullable(contentType)
                                .orElse("application/octet-stream")
                ))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + URLEncoder.encode(filename, "UTF-8"))
                .body(resource);
    }

}