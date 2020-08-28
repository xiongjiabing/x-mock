##使用指南
###模式
* x-mock主要有2种模式:server、client(默认client)
* 可以在项目的资源文件bootstrap.properties 添加 xmock.model=server
* 或者加在vm参数中-javaagent:/Users/target/k-mock.jar=xmock.model=server

###说明
* 1、server模式下主要针对和前端连调、测试环境数据的mock。需要在项目资源文件中创建appserver.mock文件
* 2、client模式下针对junit test增强。当创建一个test case类时，需要在test/resources文件下创建与该test case类同路径同名的.mock文件

####重要:所有的测试类都必须以Test结尾，mock文件同理。(仅仅针对client模式)
####重要:所有的mock文件都必须遵循yaml风格
####提示:具体请详见x-mock-demo

###yaml mock文件 配置使用说明
* 一、基础格式:
   * \- { for-return: org.xiong.xmock.demo.DemoMain.getMgs,  test: dealIdl, return: {retCode: 0, retMsg: abc} } 
     
   * \- { for-service: facadeServiceFirst  }
   *  for-return: 代表需要mock的目标方法，当方法出现重载时，需要指定方法签名，可以单独使用也可以与for-field搭配
   *  test: 当前mock的作用域，多个用 ,号分割。允许为空,则代表为当前Test类全部范围
   *  return: 方法返回值可为 []、{}、某个值。仅仅和for-return搭配
   *  for-service: 当for-return的mock对象注入到指定的服务时，需要指定目标服务的名称,多个_下划线分割或者另起一行。
     可选的，为空则根据类型匹配进行注入。建议相同的接口类型写在一起。
   *  sleep: 模拟接口处理时间,默认单位为 ms，可选的,仅仅和for-return搭配
   *  throw: 异常模拟,支持2种可选值:runtime、timeout
 
* 二、参数内置函数
   *  $time 当前时间默认格式: yyyy-MM-dd HH:mm:ss
   *  $date 当前时间默认格式: yyyy-MM-dd
   *  $ts 当前时间时间戳
   *  $uuid 唯一id
   
###内置注解支持
* @XMock 作用域在test类
* @AutoInject 作用域在test类属性
####提示:集成spring后,内置注解均可不用引入

###关于spring的集成
* 当springboot已经成为主流,x-mock在设计之初并没有考虑对原生spring直接支持。与spring的生态体系结合仅仅建立在springboot之上。
* x-mock内置x-mock-spring插件。默认会启动，可以做到动态加载与替换。具体扩展详见`设计原则`部分
* yaml mock文件编写将变得更加简洁。只需要for-return到具体类型,x-mock会自动注入到指定类型，完全兼容@Autowired、@Qualifier
同时支持jdk内置@Resource注解

###关于jacoco的集成
* 选择内置jacoco,因为其社区活跃。而且本身也是java语言编写。集成更加方便。
* x-mock内置x-mock-jacoco插件。默认会启动，可以做到动态加载与替换。具体扩展详见`设计原则`部分
* 作为单测mock框架，代码覆盖率统计是必要的，没有指标就失去了意义。用户在运行完单测后，请到target/coverage-report下查看统计报表。
