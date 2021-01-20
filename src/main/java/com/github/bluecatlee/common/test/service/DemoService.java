package com.github.bluecatlee.common.test.service;

import com.github.bluecatlee.common.mybatis.tk.BaseService;
import com.github.bluecatlee.common.test.entity.TPosDevice;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class DemoService extends BaseService {

    public CommonResp<List<TPosDevice>> query(CommonResp.Meta meta) {
        Example example = new Example(TPosDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", 1);

        CommonResp commonResp = this.queryList(example, meta, TPosDevice.class);
        return commonResp;
    }

}
