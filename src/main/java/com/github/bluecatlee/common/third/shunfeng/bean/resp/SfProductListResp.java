package com.github.bluecatlee.common.third.shunfeng.bean.resp;

import com.github.bluecatlee.common.third.shunfeng.bean.SfProduct;
import lombok.Data;

import java.util.List;

@Data
public class SfProductListResp {

    /*分页数据*/
    private Integer offset;
    private Integer pageSize;
    private Integer totalCount;
    private List<SfProduct> pageData;

    // /**
    //  * 客户打折比率
    //  */
    // private BigDecimal custRank;

}
