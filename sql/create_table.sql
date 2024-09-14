# 创建库
drop database if exists word_ease;
create database if not exists word_ease character set = utf8mb4;
use word_ease;
# 用户表
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    user_account  varchar(256)                       null comment '账号',
    avatar_url    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    user_password varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_delete     tinyint  default 0                 not null comment '是否删除'
)
    comment '用户';

# 单词表
create table word
(
    id               bigint unsigned auto_increment comment '主键'
        primary key,
    spelling         varchar(255)                        not null comment '单词拼写',
    phonetic         varchar(255)                        not null comment '单词音标',
    definition       varchar(2048)                       not null comment '单词释义',
    example_sentence varchar(2048)                       null comment '单词例句',
    word_type        int                                 null comment '单词类型：1-四级，2-六级，3-其他',
    create_time      timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '常见单词词库';
# 用户单词关联表
create table user_word
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    user_id         bigint                              not null comment '用户id',
    word_id         bigint                              not null comment '单词id',
    study_status    int                                 not null comment '熟练度 0-9',
    last_study_time timestamp default CURRENT_TIMESTAMP not null comment '最后一次学习时间',
    create_time     timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '用户单词关联表';
