package com.nuist_campuswall.controller.file;

import com.nuist_campuswall.common.Result;
import com.nuist_campuswall.domain.enums.FileType;
import com.nuist_campuswall.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

  @PostMapping("/upload")
    public Result<Long> upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("fileType") FileType fileType) {
      return Result.success(fileService.upload(file, fileType));
  }
}
