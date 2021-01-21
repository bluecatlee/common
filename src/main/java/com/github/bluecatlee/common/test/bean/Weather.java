package com.github.bluecatlee.common.test.bean;

import lombok.Data;

@Data
public class Weather extends MessagePack {
    private String fullInfo;
}