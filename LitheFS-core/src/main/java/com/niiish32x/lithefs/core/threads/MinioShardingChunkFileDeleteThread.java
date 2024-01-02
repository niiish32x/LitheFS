package com.niiish32x.lithefs.core.threads;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Data
public class MinioShardingChunkFileDeleteThread implements Runnable{
    private CopyOnWriteArrayList<String> chunkFileList;

    @Override
    public void run() {
        for (String chunkFile : chunkFileList){
            File file = new File(chunkFile);
            if (file.exists()){
                if (file.delete()){
//                    System.out.println("删除分片" + chunkFile);
                }else {
                    System.out.println("分片"+chunkFile + "删除失败");
                }
            }else {
                System.out.println("分片" + chunkFile + "不存在");
            }
        }
        log.info("完成所有分片的删除");
    }
}
