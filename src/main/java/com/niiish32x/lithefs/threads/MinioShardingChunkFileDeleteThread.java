package com.niiish32x.lithefs.threads;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;

@Data
public class MinioShardingChunkFileDeleteThread implements Runnable{
    private ArrayList<String> chunkFileList;

    @Override
    public void run() {
        for (String chunkFile : chunkFileList){
            File file = new File(chunkFile);
            if (file.exists()){
                if (file.delete()){
                    System.out.println("删除分片" + chunkFile);
                }else {
                    System.out.println("分片"+chunkFile + "删除失败");
                }
            }else {
                System.out.println("分片" + chunkFile + "不存在");
            }
        }

    }
}
