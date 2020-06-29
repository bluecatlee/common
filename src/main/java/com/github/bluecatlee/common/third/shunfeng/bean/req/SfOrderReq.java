package com.github.bluecatlee.common.third.shunfeng.bean.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.bluecatlee.common.third.shunfeng.bean.SfCommonReqParams;
import com.github.bluecatlee.common.third.shunfeng.bean.SfOrder;
import lombok.Data;

@Data
public class SfOrderReq extends SfCommonReqParams {

    @JsonProperty("order")
    private SfOrder sfOrder;

}
