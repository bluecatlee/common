package com.github.bluecatlee.common.negative.examples.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.bluecatlee.common.sql.MySqlUtil;
import com.github.bluecatlee.common.negative.examples.bean.CommonResp;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 通用查询接口
 *      直接解析参数拼接成sql，然后使用jdbc查询
 */
@Controller
public interface BasicQueryController {

    @RequestMapping(value = "/page.json", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    default String page(HttpServletRequest request, HttpServletResponse response, CommonResp.Meta meta) {
        String sql = getSql();
        Pair<List<MySqlUtil.QueryCondition>, List<MySqlUtil.QueryDetail>> pair = MySqlUtil.parseSql(sql);
        JSONObject map = new JSONObject();
        map.put("conditions", pair.getLeft());
        map.put("details", pair.getRight());
        return renderJs("index", map);
    }

    default String renderJs(String index, JSONObject map) {
        return JSON.toJSONString(map);
    }

    /**
     * 通用的列表查询
     * @param request
     * @param response
     * @param meta
     * @return
     */
    @GetMapping("/query_list")
    @ResponseBody
    default CommonResp query(HttpServletRequest request, HttpServletResponse response, CommonResp.Meta meta) {
        String sql = getSql();
        JSONObject params = parseParams(request, meta);
        List<String> sqls = MySqlUtil.generateSelectAndCount(sql, params);
        List<Map<String, Object>> data = getJdbcTemplate().queryForList(sqls.get(0), params);
        CommonResp commonResp = new CommonResp();
        commonResp.setData(data);
        meta.setTotal(getJdbcTemplate().queryForObject(sqls.get(1), params, Long.class));
        commonResp.setMeta(meta);
        return commonResp;
    }

    /**
     * 参数解析
     * @param request
     * @param meta
     * @return
     */
    default JSONObject parseParams(HttpServletRequest request, CommonResp.Meta meta) {
        JSONObject paramMap = new JSONObject();
        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((x, y) -> {
            Object v = null;
            x = x.replace("__", ". ");  // 如果有别名 由于'.'是特殊字符 前端传参要用'__'替换'.'， 因此这里需要替换回来
            String xUnderscore = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, x); // 参数名转下划线
            if (y == null || y.length == 0 || StringUtils.isBlank(y[0])) {
                return;
            }
            if (y.length == 0) {
                return;
            } else if (y.length == 1) {
                v = y[0];
                if(((String) v).contains(",")){
                    v = ((String) v).split(",");
                }
            } else {
                v = y;
            }
            //为了避免 a.id 参数  a 的丑陋, 去掉 a  再额外传递一个参数.. 若多表内有意义不同的参数同名传入, 仍需使用前缀
            if (xUnderscore.contains(".")) {
                String[] split = xUnderscore.split("\\.");
                paramMap.put(split[split.length - 1], v);
            }
            paramMap.put(x, v);
            paramMap.put(xUnderscore, v);
        });

        paramMap.put("offset", (meta.getPage() - 1) * meta.getPageSize());
        paramMap.put("limit", meta.getPageSize());

        return paramMap;
    }

    String getSql();

    NamedParameterJdbcTemplate getJdbcTemplate();

}

