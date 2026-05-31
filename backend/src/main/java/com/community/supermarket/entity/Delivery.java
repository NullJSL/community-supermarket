package com.community.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_delivery")
public class Delivery {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String status;
    private LocalDateTime estimatedTime;
    private LocalDateTime actualTime;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
