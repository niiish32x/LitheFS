package com.niiish32x.lithefs.tools;

import lombok.extern.slf4j.Slf4j;

import java.io.File;


// 用于获取本地文件的相关信息
@Slf4j
public class FileUtils {
    // 获取需要分片上传文件总长度
    public static long getTotalSize(String filePath){
        File file = new File(filePath);

        if (file == null || !file.exists()) {
            log.info("文件不存在 无法取得分片文件的大小");
            return 0;
        }

        return file.length();
    }



}
