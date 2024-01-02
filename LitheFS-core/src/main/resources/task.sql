-- 上传任务表
CREATE TABLE `t_ upload_task` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT, -- ID
                                  `bucket_name` varchar(255) DEFAULT NULL, -- 文件需要上传的桶
                                  `object_name` varchar(255) DEFAULT NULL, -- 文件名
                                  `source_file_path` varchar(255) DEFAULT NULL, -- 上传文件的原路径
                                  `dest_file_path` varchar(255) DEFAULT NULL, -- 文件上传后在Minio中的路径
                                  `file_size` int(11) DEFAULT NULL, -- 文件大小 单位MB
                                  `is_multi_part` tinyint(1) DEFAULT NULL, -- 是否使用了分片上传
                                  `part_size` int(11) DEFAULT NULL, -- 分片大小
                                  `upload_date` date DEFAULT NULL, -- 日期
                                  `upload_time` datetime DEFAULT NULL, -- 上传具体的时间精确到秒
                                  PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;