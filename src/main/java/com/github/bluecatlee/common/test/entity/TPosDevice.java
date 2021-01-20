package com.github.bluecatlee.common.test.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "t_pos_device")
public class TPosDevice {
    @Id
    private Long id;
    private String code;
    @Column(name = "deviceDesc")
    private String deviceDesc;
}
