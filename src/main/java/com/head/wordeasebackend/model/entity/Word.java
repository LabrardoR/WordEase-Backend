package com.head.wordeasebackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 常见单词词库
 * @TableName word
 */
@TableName(value ="word")
@Data
public class Word implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 单词拼写
     */
    private String spelling;

    /**
     * 单词音标
     */
    private String phonetic;

    /**
     * 单词释义
     */
    private String definition;

    /**
     * 单词例句
     */
    private String exampleSentence;

    /**
     * 单词类型：1-四级，2-六级，3-其他
     */
    private Integer wordType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}