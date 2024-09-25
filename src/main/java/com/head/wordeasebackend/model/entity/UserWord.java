package com.head.wordeasebackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户单词关联表
 * @TableName user_word
 */
@TableName(value ="user_word")
@Data
public class UserWord implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 单词id
     */
    private Long wordId;

    /**
     * 熟练度 0-9
     */
    private Integer proficiency;

    /**
     * 最后一次学习时间
     */
    private Date lastStudyTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 该单词的属性 1 - 属于用户单词表
     */
    private Integer attribute;

    /**
     * 单词拼写
     */
    private String spelling;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}