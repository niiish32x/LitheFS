package com.niiish32x.lithefs.core.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor // 无参构造
@AllArgsConstructor // 带参构造
@TableName("t_ upload_task")
public class UploadFileTaskDO {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件需要上传的桶
     */
    @TableField(value = "bucket_name")
    private String bucketName;

    /**
     * 文件名
     */
    @TableField(value = "object_name")
    private String objectName;

    /**
     * 上传文件的原路径
     */
    private String sourceFilePath;

    /**
     * 文件上传后在Minio中的路径
     */
    private String destFilePath;


    /**
     * 文件大小 单位MB
     */
    private int fileSize;

    /**
     * 是否使用了分片上传
     */
    boolean isMultiPart;

    /**
     * 分片大小
     */
    int partSize;

    /**
     * 日期
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date uploadDate;

    /**
     * 上传具体的时间精确到秒
     */
    @JsonFormat(locale="zh",pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime uploadTime;
}
