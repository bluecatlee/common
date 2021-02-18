动态数据源demo

    orm框架都是包装了数据源，然后封装、扩展了各种操作而已。
    spring框架本身提供了AbstractRoutingDataSource机制，
    因此实现动态数据源的基本思路都是将orm框架中的DataSource设置成包装后的动态数据源，然后在使用前指定路由键。
        
    可以对每次切换路由键的重复代码进行二次封装。
        
    当然，不使用AbstractRoutingDataSource机制也可以实现，
    无非就是显式的操作特定的数据源，
    只不过这种方式代码耦合性较强，而且一般都会结合orm框架。
    
