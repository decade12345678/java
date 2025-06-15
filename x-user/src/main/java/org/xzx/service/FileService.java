package org.xzx.service;

import java.nio.file.Path;

public interface FileService {

    void FileSend(Long sendId, Long hearId, String fileName, String fileSize, String fileType, String preview_url,String send_image,String send_name);
}
