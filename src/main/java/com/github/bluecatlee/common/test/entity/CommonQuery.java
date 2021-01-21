package com.github.bluecatlee.common.test.entity;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Table(name = "common_query")
public class CommonQuery {
    /**
     * 行号
     */
    @Column(name = "`series`")
    private Long series;

    /**
     * sql执行描述
     */
    @Column(name = "`sql_name`")
    private String sqlName;

    /**
     * sql编号
     */
    @Column(name = "`sql_id`")
    private String sqlId;

    /**
     * jdbc连接名字
     */
    @Column(name = "`jdbc_name`")
    private String jdbcName;

    /**
     * 创建时间
     */
    @Column(name = "`create_dtme`")
    private Date createDtme;

    /**
     * 最后更新时间
     */
    @Column(name = "`last_updtme`")
    private Date lastUpdtme;

    /**
     * 用户
     */
    @Column(name = "`create_user_id`")
    private Long createUserId;

    /**
     * 更新用户
     */
    @Column(name = "`last_update_user_id`")
    private Long lastUpdateUserId;

    /**
     * n为可使用,y为不使用
     */
    @Column(name = "`cancel_sign`")
    private String cancelSign;

    /**
     * 租户id
     */
    @Column(name = "`tenant_num_id`")
    private Integer tenantNumId;

    /**
     * 0是正式,1是测试
     */
    @Column(name = "`data_sign`")
    private Byte dataSign;

    /**
     * 数据库类型
     */
    @Column(name = "`db_type`")
    private String dbType;

    @Column(name = "`annotate_prefix`")
    private String annotatePrefix;

    @Column(name = "`sub_sql_id`")
    private String subSqlId;

    /**
     * 无数据是否报错，Y是，N不报错
     */
    @Column(name = "`no_data_exception`")
    private String noDataException;

    /**
     * 缓存使用标识0:不使用1：直接缓存，2缓存服务
     */
    @Column(name = "`cache_sign`")
    private Integer cacheSign;

    /**
     * 方法名称
     */
    @Column(name = "`method_name`")
    private String methodName;

    /**
     * 缓存存活时间
     */
    @Column(name = "`cache_live_time`")
    private Integer cacheLiveTime;

    /**
     * field for checking consistency
     */
    @Column(name = "`_dble_op_time`")
    private Long dbleOpTime;

    /**
     * n为可使用,y为不使用
     */
    @Column(name = "`cancelsign`")
    private String cancelsign;

    /**
     * sql内容
     */
    @Column(name = "`sql_content`")
    private String sqlContent;

    /**
     * sql参数
     */
    @Column(name = "`param_content`")
    private String paramContent;

    /**
     * 返回参数 处理
     */
    @Column(name = "`return_handle_content`")
    private String returnHandleContent;

    public static final String SERIES = "series";

    public static final String DB_SERIES = "series";

    public static final String SQL_NAME = "sqlName";

    public static final String DB_SQL_NAME = "sql_name";

    public static final String SQL_ID = "sqlId";

    public static final String DB_SQL_ID = "sql_id";

    public static final String JDBC_NAME = "jdbcName";

    public static final String DB_JDBC_NAME = "jdbc_name";

    public static final String CREATE_DTME = "createDtme";

    public static final String DB_CREATE_DTME = "create_dtme";

    public static final String LAST_UPDTME = "lastUpdtme";

    public static final String DB_LAST_UPDTME = "last_updtme";

    public static final String CREATE_USER_ID = "createUserId";

    public static final String DB_CREATE_USER_ID = "create_user_id";

    public static final String LAST_UPDATE_USER_ID = "lastUpdateUserId";

    public static final String DB_LAST_UPDATE_USER_ID = "last_update_user_id";

    public static final String CANCEL_SIGN = "cancelSign";

    public static final String DB_CANCEL_SIGN = "cancel_sign";

    public static final String TENANT_NUM_ID = "tenantNumId";

    public static final String DB_TENANT_NUM_ID = "tenant_num_id";

    public static final String DATA_SIGN = "dataSign";

    public static final String DB_DATA_SIGN = "data_sign";

    public static final String DB_TYPE = "dbType";

    public static final String DB_DB_TYPE = "db_type";

    public static final String ANNOTATE_PREFIX = "annotatePrefix";

    public static final String DB_ANNOTATE_PREFIX = "annotate_prefix";

    public static final String SUB_SQL_ID = "subSqlId";

    public static final String DB_SUB_SQL_ID = "sub_sql_id";

    public static final String NO_DATA_EXCEPTION = "noDataException";

    public static final String DB_NO_DATA_EXCEPTION = "no_data_exception";

    public static final String CACHE_SIGN = "cacheSign";

    public static final String DB_CACHE_SIGN = "cache_sign";

    public static final String METHOD_NAME = "methodName";

    public static final String DB_METHOD_NAME = "method_name";

    public static final String CACHE_LIVE_TIME = "cacheLiveTime";

    public static final String DB_CACHE_LIVE_TIME = "cache_live_time";

    public static final String _DBLE_OP_TIME = "dbleOpTime";

    public static final String DB__DBLE_OP_TIME = "_dble_op_time";

    public static final String CANCELSIGN = "cancelsign";

    public static final String DB_CANCELSIGN = "cancelsign";

    public static final String SQL_CONTENT = "sqlContent";

    public static final String DB_SQL_CONTENT = "sql_content";

    public static final String PARAM_CONTENT = "paramContent";

    public static final String DB_PARAM_CONTENT = "param_content";

    public static final String RETURN_HANDLE_CONTENT = "returnHandleContent";

    public static final String DB_RETURN_HANDLE_CONTENT = "return_handle_content";
}