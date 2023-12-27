package com.niiish32x.lithefs.threads;


import lombok.Data;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;


/**
 * 执行合并文件的线程
 */
@Data
public class MinioShardingFileMergeThread implements Runnable{
    private final CountDownLatch latch;

    private CopyOnWriteArrayList<String> chunkFileList;
    private String mergeFile;

    public MinioShardingFileMergeThread(CountDownLatch latch) {
        this.latch = latch;
    }



    @SneakyThrows
    @Override
    public void run() {
        FileOutputStream fos = new FileOutputStream(mergeFile);
        Collections.sort(chunkFileList);
        for (String chunkFile : chunkFileList){
            FileInputStream fis = new FileInputStream(chunkFile);
            byte [] buffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1){
                fos.write(buffer,0,bytesRead);
            }

            fis.close();
        }
        fos.close();

        latch.countDown();
    }
}
