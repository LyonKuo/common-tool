This file contains all changes done in releases for isic-commons-utils.

## 1.0.10

### Improvement
    * 添加对来自Isec请求的验签方法
    * 增加对泛型序列化和反序列化的支持

## 1.0.9

### Improvement
    * JsonUtils增加对jdk8日期类型的反序列化
    * ExceptionConvertor优化，提供convert方法


## 1.0.8

### Improvement
    * 异常的code类型从String改为Integer

### New Feature
    * 新增 TransmissionException 异常处理方式


## 1.0.7

### Improvement

    * 修改 通用日志配置文件路径，从 依赖系统配置 调整为 依赖spring context里面的logging.file.path属性
    * 修改 IpFilter为deperated

### New Feature

    * 新增Spring MVC统一异常处理类
    * 新增统一业务异常类

### Dependency

    * 升级common-api从1.0.1到1.1.0版本
    * 添加spring-context和spring-message依赖
    * 添加validation依赖
    * 添加slf4j依赖