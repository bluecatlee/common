package com.github.bluecatlee.common.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.MultipartConfigElement;
import java.util.HashMap;

/**
 * 初始化配置
 *  从外部数据源中获取配置信息 来完成spring的启动过程
 */
// @Configuration
public class OuterPropConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OuterPropConfiguration.class);

    private static final String TAG = "Load Config Properties >>>>> ";

    private HashMap<String, String> map = new HashMap<>();

    /**
     * 加载配置数据 实现可以是从数据库中获取
     */
    // @Autowired
    // private SystemPropertyService systemPropertyService;

    /**
     * 测试bean 测试bean初始化的依赖顺序
     * 可以实现为内存缓存
     */
    public class PropertyCache {}

    /**
     * 从数据库中读取配置数据 初始化propertyCache @Bean的初始化顺序比@Component晚
     */
    @Bean(name = "propertyCache")
    public PropertyCache getPropertyCache() {
        logger.info(TAG + "Read Data from Database···");
        // 加载方法未实现
        // List<SystemProperty> properties = systemPropertyService.getProperties();
        // map = (HashMap) properties.stream().collect(Collectors.toMap(SystemProperty::getPropKey, SystemProperty::getPropVal));
        PropertyCache cache = new PropertyCache();
        logger.info(TAG + "Init Config Properties Success.");
        return cache;
    }

    /**
     * 初始化redisCache 指定在propertyCache之后初始化
     * 使用这种方式 RedisCache类上不要加@Service或类似注解
     */
    // @Bean(name = "redisCache")
    // @DependsOn("propertyCache")
    // public RedisSingleCache getRedisCache() {
    //     Map<String, String> config = new HashMap<>();
    //     config.put("host", map.get("spring.redis.host"));
    //     config.put("port", map.get("spring.redis.port"));
    //     config.put("password", map.get("spring.redis.password"));
    //     config.put("database", map.get("spring.redis.database"));
    //     RedisSingleCache cache = new RedisSingleCache();
    //     cache.init(config);
    //     logger.info(TAG + "Init Redis Connection Success.");
    //     return cache;
    // }

    /**
     * 文件上传大小限制的配置不使用配置文件 从数据库读取
     */
    @Bean
    @DependsOn("propertyCache")
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(map.get("servlet.multipart.max-file-size"));       // 设置文件大小限制
        factory.setMaxRequestSize(map.get("servlet.multipart.max-request-size")); // 设置请求大小限制
        logger.info(TAG + "Init MultipartFile Restrict Success.");
        return factory.createMultipartConfig();
    }

    // @Bean(name = "multipartResolver")
    // @DependsOn("propertyCache")
    // public CommonsMultipartResolver getCommonsMultipartResolver() {
    //     CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    //     String s = map.get("servlet.multipart.max-file-size");
    //
    //     if (s.endsWith("MB")) {
    //         s = s.substring(0, s.indexOf("MB"));
    //     }
    //     multipartResolver.setMaxUploadSize(Long.valueOf(s).longValue() * 1024 * 1024);
    //     // multipartResolver.setMaxInMemorySize(Integer.valueOf(s).intValue() * 1024 * 1024);
    //     return multipartResolver;
    // }

    // 连接池模式下使用的不是这个工厂实现
    // @Bean
    // @DependsOn("propertyCache")
    // public ActiveMQConnectionFactory activeMQConnectionFactory() {
    //     ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
    //     activeMQConnectionFactory.setBrokerURL(map.get("spring.activemq.broker-url"));
    //     activeMQConnectionFactory.setUserName(map.get("spring.activemq.user"));
    //     activeMQConnectionFactory.setPassword(map.get("spring.activemq.password"));
    //     logger.info(TAG + "Init ActiveMQ ConnectionFactory Success.");
    //     return activeMQConnectionFactory;
    // }



    // ----------------------------------------------------------------------------------
    // SpringBoot2.1及以后版本的ActiveMQ配置方式不一样 ActiveMQProperties中没有Pool内部类
    // ----------------------------------------------------------------------------------
    // /**
    //  * 初始化activemq连接池
    //  */
    // @Bean
    // @DependsOn("propertyCache")
    // @Primary  // 必须申明该注解 否则会与默认的bean[activeMQProperties]冲突
    // public ActiveMQProperties activeMQProperties() {
    //     ActiveMQProperties activeMQProperties = new ActiveMQProperties();
    //     activeMQProperties.setBrokerUrl(map.get("spring.activemq.broker-url"));
    //     activeMQProperties.setUser(map.get("spring.activemq.user"));
    //     activeMQProperties.setPassword(map.get("spring.activemq.password"));
    //     // 如果不显式指定broker-url 则会自动创建broker
    //     activeMQProperties.setInMemory(Boolean.valueOf(map.get("spring.activemq.in-memory")));
    //
    //     boolean enablePool = Boolean.valueOf(map.get("spring.activemq.pool.enabled"));
    //     ActiveMQProperties.Pool pool = new ActiveMQProperties.Pool();
    //     pool.setEnabled(enablePool);
    //     if (enablePool) { // 若允许创建连接池
    //         pool.setExpiryTimeout(Duration.ofMillis(Long.parseLong(map.get("spring.activemq.poolExpiryTimeout"))));
    //         pool.setIdleTimeout(Duration.ofMillis(Long.parseLong(map.get("spring.activemq.poolIdleTimeout"))));
    //         pool.setMaxConnections(Integer.parseInt(map.get("spring.activemq.poolMaxConnections")));
    //     }
    //     activeMQProperties.setPool(pool);
    //     logger.info(TAG + "Init ActiveMQ Connection Config Success.");
    //     return activeMQProperties;
    // }
}
