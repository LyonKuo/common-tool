# common class


- JSON serial 
- sign
  * add sign alg
  * server verify filiter
- common log


## common log config

log PATTERN，appender usually（console，应用日志，审计日志，SQL慢日志）

sample：
```xml
<include resource="com/common/utils/log/logback-common-sleuth3.xml" />
```

note：
the config from spring context to get attribute **logging.file.path**，get log output path
```xml
<springProperty scope="context" name="LOG_PATH" source="logging.file.path" />
```

# ROADMAP

1. 添加rest controller异常处理类
