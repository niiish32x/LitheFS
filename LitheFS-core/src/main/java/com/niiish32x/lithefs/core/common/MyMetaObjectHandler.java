package com.niiish32x.lithefs.core.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 自动生成mybatis的数据项
 */
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject,"uploadDate", Date::new,Date.class);
        strictInsertFill(metaObject,"uploadTime", LocalDateTime::now,LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
