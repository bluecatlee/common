package com.github.bluecatlee.common.plugin;

import java.util.List;

/**
 * 插件
 *      将一些可复用逻辑抽象成插件，同时避免将所有业务直接耦合在相关方法之中
 *          比如订单创建完成之后扣除库存 发送微信模板消息
 *          或者计算运费、计算分仓价格，拼团秒杀积分优惠券等其他营销类业务的处理
 *          或者对于不同的业务重新赋值返回的情景
 *
 *      插件按照不同流程的前置后置关系划分，抽象出不同流程层次的插件，每个插件有各自具体业务的多种实现。
 *
 *      插件的实现是可配置的，可配置多实现及每个实现之间的顺序。考虑以List的方式存储插件集。
 *      插件集的元素就是该插件的所有启用的实现的bean的名称，可以通过bean的名称获取对应的service实例
 *
 *      配置方式
 *          配置文件
 *              可以是spring xml配置文件注入list的方式 或者application.yml中配置列表数据 或者是properties文件
 *          数据库(关系型/非关系型)
 *
 *      通过是否每次都需要重新加载配置可以实现动态配置且不重启
 */
public class Plugin {

    /**
     * 订单创建插件集
     *      比如订单创建插件提供afterCreated()方法 对应有扣除库存的实现，有清除购物车对应商品的实现...
     */
    private List<String> orderCreatePlugins;

    /**
     * 订单确认插件集
     *      比如订单确认页的各种数据的校验(去订单确认页和提交订单的过程中实际有很多重复的校验逻辑)，
     *      优惠信息、积分、运费的计算业务...
     */
    private List<String> orderConfirmPlugins;

    // ...

}
