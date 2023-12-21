package com.niiish32x.lithefs.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.niiish32x.lithefs.common.param.InitTaskParam;
import com.niiish32x.lithefs.dao.entity.SysUploadTask;
import com.niiish32x.lithefs.dto.TaskInfoDTO;

import java.util.Map;

/**
 * 分片上传-分片任务记录(SysUploadTask)表服务接口
 */
public interface SysUploadTaskService extends IService<SysUploadTask> {

    /**
     * 根据md5标识获取分片上传任务
     * @param identifier
     * @return
     */
    SysUploadTask getByIdentifier (String identifier);

    /**
     * 初始化一个任务
     */
    TaskInfoDTO initTask (InitTaskParam param);

    /**
     * 获取文件地址
     * @param bucket
     * @param objectKey
     * @return
     */
    String getPath (String bucket, String objectKey);

    /**
     * 获取上传进度
     * @param identifier
     * @return
     */
    TaskInfoDTO getTaskInfo (String identifier);

    /**
     * 生成预签名上传url
     * @param bucket 桶名
     * @param objectKey 对象的key
     * @param params 额外的参数
     * @return
     */
    String genPreSignUploadUrl (String bucket, String objectKey, Map<String, String> params);

    /**
     * 合并分片
     * @param identifier
     */
    void merge (String identifier);
}