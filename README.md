# 简介
Collapse-Executor 是一个高性能、低延迟的批量执行器，可以有效支持高并发的热点请求，通过批量请求减少I/O次数的方式提升服务调用性能。

# 工程结构
## collapse-executor-core 
折叠执行器的核心抽象，包含输入拆分、输出映射、批量采集等核心逻辑抽象
## collapse-executor-integration 
折叠执行器与主流框架的集成模块，如RestTemplate、Servlet Filter等
## collapse-executor-samples 
折叠执行器的使用例子

# Servlet合并测试
```` text
服务参数
server.tomcat.threads.max=200

服务地址(位于 collapse-executor-samples 中的 StressTestController, 后端均延迟[100ms]后响应，逻辑一致无差异)
http://localhost:8080/test/collapse100
http://localhost:8080/test/noop100

测试参数
400用户线程数，持续压测5分钟
````

## 开启折叠
```` text
http://localhost:8080/test/collapse100 启用请求折叠测试结果
TPS   3785/s
RT99  115ms
````
![with-collapse](./docs/images/with-collapse.png)

## 关闭折叠
```` text
http://localhost:8080/test/noop100 关闭请求折叠测试结果
TPS   1951/s
RT99  211ms
````
![without-collapse](./docs/images/without-collapse.png)

# 工作流程
![处理流程](./docs/images/collapse-executor.png)