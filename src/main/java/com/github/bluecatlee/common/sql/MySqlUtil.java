package com.github.bluecatlee.common.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.druid.sql.SQLUtils.toMySqlExpr;
import static com.alibaba.druid.sql.SQLUtils.toMySqlString;
import static com.alibaba.druid.util.JdbcConstants.MYSQL;

/**
 * sql工具类 todo
 * @see com.alibaba.druid.sql.SQLUtils 对SQLUtils进行了二次封装
 */
@Slf4j
public class MySqlUtil {
    final static SQLBinaryOpExpr DEFAULT_PROP_AND = new SQLBinaryOpExpr(SQLUtils.toMySqlExpr("1"), SQLBinaryOperator.Equality, SQLUtils.toMySqlExpr("1"));
    final static SQLBinaryOpExpr DEFAULT_PROP_OR = new SQLBinaryOpExpr(SQLUtils.toMySqlExpr("1"), SQLBinaryOperator.Equality, SQLUtils.toMySqlExpr("2"));
    final static SQLInListExpr DEFAULT_IN_AND = new SQLInListExpr(toMySqlExpr("1"));
    final static SQLInListExpr DEFAULT_IN_OR = new SQLInListExpr(toMySqlExpr("1"));
    final static SQLBetweenExpr DEFAULT_BETWEEN_AND = new SQLBetweenExpr(toMySqlExpr("2"), toMySqlExpr("1"), toMySqlExpr("3"));
    final static SQLBetweenExpr DEFAULT_BETWEEN_OR = new SQLBetweenExpr(toMySqlExpr("1"), toMySqlExpr("2"), toMySqlExpr("3"));

    final static SQLSelectItem DEFAULT_SELECT_COUNT = new SQLSelectItem();

    static {
        DEFAULT_SELECT_COUNT.setExpr(SQLUtils.toMySqlExpr("count(1)"));
        DEFAULT_SELECT_COUNT.setAlias("total");
        DEFAULT_IN_OR.setTargetList(Arrays.asList(toMySqlExpr("2")));
        DEFAULT_IN_AND.setTargetList(Arrays.asList(toMySqlExpr("1")));
    }


    ///只对简单select语句有效果
    public static SQLSelectQueryBlock parseQueryBlock(String sql) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, MYSQL);
        assert sqlStatements.size() == 1;
        SQLStatement sqlStatement = sqlStatements.get(0);
        assert sqlStatement != null;
        assert sqlStatement instanceof SQLSelectStatement;
        if (!(sqlStatement instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("只支持SELECT 语句");
        }
        SQLSelect select = ((SQLSelectStatement) sqlStatement).getSelect();
        SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
        return queryBlock;
    }

    public static List<QueryDetail> parseDetail(SQLSelectQueryBlock queryBlock) {
        List<SQLSelectItem> selectList = queryBlock.getSelectList();
        List<QueryDetail> propNameList = selectList.stream().map(x -> {
            String alias = x.getAlias();
            String prop = null;
            if (prop == null) {
                String s = x.getExpr() + "";
                if (s.contains(".")) {  //点式子, 去掉.前面的
                    prop = s.substring(s.indexOf(".") + 1);
                } else if (s.contains("(")) {
                    prop = s.replace("(", "").replace(")", "");
                    //函数去掉括号, 如果参数有问题, 那管不了了... 自己写alias吧
                } else {
                    prop = s;
                }
            }
            return QueryDetail.builder().prop(prop).label(alias).build();
        }).collect(Collectors.toList());
        // 如今列表是有重复的, select  a,a 执行后的字段名是 a, a1 所以要循环加标号前面出现过的加 1, 先用map写吧
        Map<String, Integer> existMap = new HashMap<>(propNameList.size());

        List<QueryDetail> renamedList = propNameList.stream().map(x -> {
            Integer times = existMap.get(x.getProp());
            if (times != null) {
                existMap.put(x.getProp(), times + 1);
                x.setProp(x.getProp() + times);
                return x;
            }
            existMap.put(x.getProp(), 1);
            return x;
        }).collect(Collectors.toList());


        return renamedList;
    }

    //递归where, 获取字段名和类型
    public static void parseCondition(SQLBinaryOpExpr expr, List<QueryCondition> map) {
        SQLExpr left = expr.getLeft();
        SQLExpr right = expr.getRight();
        if (left instanceof SQLBinaryOpExpr) {
            parseCondition((SQLBinaryOpExpr) left, map);
        } else {
            String prop = "";
            String type = "text";
            if (left instanceof SQLBetweenExpr) {
                genBetween(map, left);
            } else if (left instanceof SQLInListExpr) {
                genInCondition(map, left);
            } else {
                type = "text";
                prop = toMySqlString(left);
                map.add(QueryCondition.builder()
                        .prop(prop)
                        .type(type)
                        .val(toMySqlString(right))
                        .build());
                return;
            }
        }
        if (right instanceof SQLBinaryOpExpr) {
            parseCondition((SQLBinaryOpExpr) right, map);
//            log.info("exprright:{}, {}",toMySqlString(right),right.getClass());
        } else {
            if (right instanceof SQLBetweenExpr) {
                genBetween(map, right);
            } else if (right instanceof SQLInListExpr) {
                genInCondition(map, right);
            }

            return;
        }
    }

    private static void genBetween(List<QueryCondition> map, SQLExpr left) {
        String prop;
        String type;
        String val;
        log.info("between la {}", toMySqlString(left));
        SQLBetweenExpr betweenExpr = (SQLBetweenExpr) left;
        prop = toMySqlString(betweenExpr.getTestExpr());
        type = "range";
        SQLExpr beginExpr = betweenExpr.getBeginExpr();
        SQLExpr endExpr = betweenExpr.getEndExpr();
        val = toMySqlString(beginExpr) + "," + toMySqlString(endExpr);
        Class<? extends SQLExpr> aClass = beginExpr.getClass();
        if (aClass == SQLMethodInvokeExpr.class) {
            SQLMethodInvokeExpr beginExpr1 = (SQLMethodInvokeExpr) beginExpr;
            String name = beginExpr1.getMethodName().toUpperCase();
            if (name.equals("DATE")) {
                type = "daterange";
                val = toMySqlString(beginExpr1.getParameters().get(0)) + "," + toMySqlString(endExpr);
            } else {
                // 这时候, 进到这个逻辑说明虽然有between, 但是可变量可能只有1,0个 换成 date类型足矣 todo
                type = "date";
            }
        }


        map.add(QueryCondition.builder()
                .prop(prop)
                .type(type)
                .val(val)
                .build());
    }

    private static void genInCondition(List<QueryCondition> map, SQLExpr left) {
        String prop;
        String type;
        SQLInListExpr inListExpr = (SQLInListExpr) left;
        prop = toMySqlString(inListExpr.getExpr());
        log.info("in la:{}", toMySqlString(left));
        type = "in";
        List<SQLExpr> targetList = inListExpr.getTargetList();
        if (targetList.size() > 1) {
            type = "inm";
        }
        Object attribute = inListExpr.getExpr().getAttribute("format.before_comment");
        log.info("{}", attribute);
        String url = null;
        if (attribute != null) {
            List<String> attributeList = (List<String>) attribute;
            if (!attributeList.isEmpty()) {
                String s = attributeList.get(0);
                log.info(s);
                String prefix = "/*###";
                String suffix = "*/";
                if (s.startsWith(prefix) && s.endsWith(suffix)) {
                    url = s.substring(prefix.length(), s.length() - suffix.length()).trim();
                    log.info(url);
                }
            }
        }

        map.add(QueryCondition.builder()
                .prop(prop)
                .type(type)
                .url(url)
                .val("[" + targetList.stream().map(x -> toMySqlString(x)).collect(Collectors.joining(",")) + "]")
                .build());
    }

    /**
     * 生成sql
     * @param sql
     * @param map
     * @return
     */
    public static List<String> generateSelectAndCount(String sql, JSONObject map) {
        SQLSelectQueryBlock queryBlock = parseQueryBlock(sql);
        // 获取查询项 即查询语句的fields
        List<SQLSelectItem> selectList = queryBlock.getSelectList();
        //修改select, 因默认的 count(1) 作为属性名, js会报错, 没有加alias的 函数要加上别名这里是直接去掉括号
        for (int i = 0; i < selectList.size(); i++) {
            SQLSelectItem sqlSelectItem = selectList.get(i);
            String s = sqlSelectItem.getExpr() + "";
            if (sqlSelectItem.getAlias() == null && s.contains("(")) {
                sqlSelectItem.setAlias(s.replace("(", "").replace(")", ""));
            }
        }
        SQLExpr where = queryBlock.getWhere();
        SQLExpr where1 = modifySQLBinaryExpr((SQLBinaryOpExpr) where, map);
        log.info("new.where{}", where1);
        queryBlock.setWhere(where1);
        //修改limit
        SQLLimit limit = new SQLLimit();
        limit.setOffset(toMySqlExpr(":offset"));
        limit.setRowCount(toMySqlExpr(":limit"));

        queryBlock.setLimit(limit);
        String selectSql = toMySqlString(queryBlock);
        //处理count, limit offset 要改成0
        selectList.removeIf(x -> true);
        selectList.add(DEFAULT_SELECT_COUNT);
        limit.setOffset(0);
        limit.setRowCount(9999999);
        String countSql = toMySqlString(queryBlock);
        if (queryBlock.getGroupBy() != null) {
            map.put("___groupmark___", true);
        }
        return Arrays.asList(selectSql, countSql);
    }

    public static SQLExpr modifyWhere(SQLSelectQueryBlock queryBlock, JSONObject json) {
        SQLExpr where = queryBlock.getWhere();
        assert where != null;
        return where;
    }

    public static SQLExpr modifySQLBinaryExpr(SQLBinaryOpExpr expr, JSONObject jsonObject) {
        SQLExpr sqlExpr = modifySQLBinaryExpr(SQLBinaryOperator.BooleanAnd, expr, jsonObject);
        if (sqlExpr == null) {
//            return DEFAULT_PROP_AND;
        }
        return sqlExpr;
    }

    public static SQLExpr modifySQLBinaryExpr(SQLBinaryOperator opOuter, SQLBinaryOpExpr expr, JSONObject jsonObject) {
        SQLExpr left = expr.getLeft();
        SQLExpr right = expr.getRight();


        SQLBinaryOperator op = expr.getOperator();
        boolean keyOnLeft = left instanceof SQLIdentifierExpr || left instanceof SQLPropertyExpr;
        boolean keyOnRight = right instanceof SQLIdentifierExpr || right instanceof SQLPropertyExpr;
        if (keyOnLeft || keyOnRight) {
            Object param = getParam(keyOnLeft ? left : right, jsonObject);
            //有参数时候, 补上参数, 没有参数时候, 切换成 1=1 1 in(1) 等
            if (param != null) {// 没有参数 //todo 这里换成完整的判断空,null,empty
                String paramHolder = left + "";
                String rhvHolder = ":" + paramHolder;
                String rhv = param + "";
                if (keyOnLeft) {
                    rhv = buildLinkeExpr(right, op, rhv);
                    jsonObject.put(paramHolder, rhv);
                    return new SQLBinaryOpExpr(left, op, SQLUtils.toMySqlExpr(rhvHolder));
                } else {
                    rhv = buildLinkeExpr(left, op, rhv);
                    return new SQLBinaryOpExpr(SQLUtils.toMySqlExpr(rhv), op, right);
                }
            }
            if (opOuter == SQLBinaryOperator.BooleanAnd) {
                return null;
            } else if (opOuter == SQLBinaryOperator.BooleanOr) {
                return null;
            } else {
                log.info("op outer:{}", opOuter);
                throw new IllegalArgumentException("支持不了的操作符");
            }
        }

        left = getSqlExpr(jsonObject, left, op);
        right = getSqlExpr(jsonObject, right, op);
        if (left != null && right != null) {
            return new SQLBinaryOpExpr(left, op, right);
        }
        if (left != null) {
            return left;
        }
        if (right != null) {
            return right;
        }
        return null;
    }

    private static String buildLinkeExpr(SQLExpr left, SQLBinaryOperator op, String rhv) {
        if (op == SQLBinaryOperator.Like) {
            String s = left.toString();
            if (s.startsWith("'%")) {
                rhv = "%" + rhv + "";
            }
            if (s.endsWith("%'")) {
                rhv = "" + rhv + "%";
            }
        }
        return rhv;
    }

    private static SQLExpr getSqlExpr(JSONObject jsonObject, SQLExpr right, SQLBinaryOperator op) {
        if (right instanceof SQLBinaryOpExpr) {
            right = modifySQLBinaryExpr(op, (SQLBinaryOpExpr) right, jsonObject);
        }
        if (right instanceof SQLInListExpr) {
            right = useInListExpr(op, (SQLInListExpr) right, jsonObject);
        }

        if (right instanceof SQLBetweenExpr) {
            right = useBetweenExpr(op, (SQLBetweenExpr) right, jsonObject);
        }
        return right;
    }

    private static SQLInListExpr useInListExpr(SQLBinaryOperator op, SQLInListExpr right, JSONObject jsonObject) {
        System.out.println("op = [" + op + "], right = [" + right + "], jsonObject = [" + jsonObject + "]");
        Object param = getParam(right.getExpr(), jsonObject);
        if (param != null) {
            right.setTargetList(Arrays.asList(toMySqlExpr(":" + right.getExpr())));
            jsonObject.put(right.getExpr() + "", getList(param));
            return right;
        }
        if (op.equals(SQLBinaryOperator.BooleanAnd)) {

            return null;
        }
        if (op.equals(SQLBinaryOperator.BooleanOr)) {
            return null;
        }
        log.info("不支持的外部操作符:{}", op);
        throw new IllegalArgumentException("不支持的操作符" + op);

    }

    private static SQLBetweenExpr useBetweenExpr(SQLBinaryOperator op, SQLBetweenExpr right, JSONObject jsonObject) {
        System.out.println("op = [" + op + "], right = [" + right + "], jsonObject = [" + jsonObject + "]");
        SQLExpr testExpr = right.getTestExpr();
        Object o = getParam(testExpr, jsonObject);
        if (o == null) {
//            return op == SQLBinaryOperator.BooleanAnd ? DEFAULT_BETWEEN_AND : DEFAULT_BETWEEN_OR;
            return null;
        }
        List arr = getList(o);
        if (arr.isEmpty()) {
//            return op == SQLBinaryOperator.BooleanAnd ? DEFAULT_BETWEEN_AND : DEFAULT_BETWEEN_OR;
            return null;
        }
        Object o1 = arr.get(0);
        if (o1 != null) {
            right.setBeginExpr(toMySqlExpr(":" + testExpr + "_begin"));
            String key = testExpr + "_begin";
            jsonObject.put(key, o1);
        }
        if (arr.size() > 1 && arr.get(1) != null) {
            String sql = testExpr + "_end";
            jsonObject.put(sql, arr.get(1));
            right.setEndExpr(toMySqlExpr(":" + sql));
        }
        return right;
    }

    private static List getList(Object o) {
        if (o instanceof Object[]) {
            return Arrays.asList((Object[]) o);
        }
        if (o instanceof List) {
            return (List) o;
        }
        if (o instanceof JSONArray) {
            return (JSONArray) o;
        }
        return Arrays.asList(o);
    }


    /**
     * @param testExpr
     * @param jsonObject
     * @return
     */
    private static Object getParam(SQLPropertyExpr testExpr, JSONObject jsonObject) {
        log.info("testExpr = [" + testExpr + "], jsonObject = [" + jsonObject + "]");
        String key = testExpr + "";
        Object oo = jsonObject.get(key);
        if (oo != null) {
            return oo;
        }

        if (key.contains(".")) {
            String key1 = key.split("\\.")[1];
            oo = jsonObject.get(key1);
            if (oo != null) {
                return oo;
            }
            String key2 = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key1);
            return jsonObject.get(key2);
        }


        String keyUnderScore = key.replace(".", "___");
        oo = jsonObject.getJSONObject(keyUnderScore);
        if (oo != null) {
            return oo;
        }
        return null;
    }

    private static Object getParam(SQLExpr testExpr, JSONObject jsonObject) {
        if (testExpr instanceof SQLPropertyExpr) {
            return getParam((SQLPropertyExpr) testExpr, jsonObject);
        }
        String expr = testExpr.toString();
        log.info("expr = {}", expr);
        String key = expr;
        return jsonObject.get(key);
    }

    public static List<QueryDetail> parseDetailList(SQLSelectQueryBlock queryBlock) {
        return parseDetailList(queryBlock);
    }

    public static List<QueryCondition> parseCondition(SQLExpr where) {
        List<QueryCondition> list = new LinkedList<>();
        parseCondition((SQLBinaryOpExpr) where, list);
        return list;
    }

    public static Pair<List<QueryCondition>, List<QueryDetail>> parseSql(String sql) {
        SQLSelectQueryBlock queryBlock = MySqlUtil.parseQueryBlock(sql);
        List<QueryCondition> queryConditions = MySqlUtil.parseCondition(queryBlock.getWhere());
        List<QueryDetail> queryDetails = MySqlUtil.parseDetail(queryBlock);
        return Pair.of(queryConditions, queryDetails);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class QueryCondition {
        String type;
        String label;
        String prop;
        String val;
        String url;   //当这一个有动态自动完成的字段时候,会有这个属性.  通过comment来判定和传入
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryDetail {
        String prop;
        String label;
        Integer width = 12;
    }
}

