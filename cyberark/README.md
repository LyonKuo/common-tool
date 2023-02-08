# Cyberark 通用工具模块


- cyberark-client：负责和cyberark通信，获取存储在cyberark的密码
- jasypt-spring-boot-starter-cyberark：springboot stater，负责从cyberark中获取密码，动态更新配置文件中的密文


## cyberark-client

### 原理

基于spring resttemplate和cyberark通信，获取密钥

### 使用

1. 创建CyberarkClient类
```java
    CyberarkClient client = new CyberarkClient(
                restTemplate,
                "https://",
                "App_XXX",
                "AIM_XXX",
                "root",
                "925736f60b29229d6123"
        );
```
2. 调用getPassword方法获取密码
```java
    String password = client.getPassword("postgresql-123456");
```


## jasypt-spring-boot-starter-cyberark

### 原理

基于开源组件jasypt-spring-boot-starter实现。利用cyberark-client，扩展jasypt定义的获取密钥的类（StringEncryptor），相关配置可以
参考jasypt github地址：https://github.com/ulisesbocchio/jasypt-spring-boot。

jasypt-spring-boot-starter-cyberark基于spring boot 2.5.x版本开发。

### 使用

1. POM文件引入依赖
```xml
        <dependency>
            <groupId>com</groupId>
            <artifactId>jasypt-spring-boot-starter-cyberark</artifactId>
            <version>1.0.0</version>
        </dependency>
```

2. 如果当前程序中spring容器中已经包含rest template，可以忽略这一步，否则引入http client依赖 (测试使用的是4.5.13，但4.3.x以上的版本应该都支持)
```xml
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>${httpclient.version}</version>
        </dependency>
```

3. yml或者properties文件中，配置stater
```yaml
jasypt:
  encryptor:
    password: true
    algorithm: cyberark
    cyberark:
      url: https://xxx/rest/pwd/getPassword
      app-id: App_XXX
      safe: AIM_xxx
      folder: root
      key: 4e0fc5cfe9d79251
      connection-max-total: 10
      connection-default-max-per-route: 5
      connection-request-timeout: 5000
      connection-connect-timeout: 1000
      connection-read-timeout: 20000
```
其中：
- url是cyerark的地址
- app-id，safe，folder，key 是cyerark中的配置
- connection* 开头的属性是远程调用使用http client pool的配置，如果使用自己的rest template可以不进行配置

4. 使用密码
比如配置redis，在输入密码的地方输入cyberark中的object name
```yaml
spring: 
  redis:
    client-type: lettuce
    host: 1.2.3.4
    port: 1234
    password: ENC(REDIS_DEV)
    database: 3
    lettuce:
      pool:
        min-idle: 2
        max-idle: 4
        max-active: 4
        max-wait: 1000
```