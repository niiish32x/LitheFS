package com.niiish32x.lithefs.threads;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

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

    }
}
