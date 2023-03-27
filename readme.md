## 使用Java开发的一些业务工具,用于辅助业务开发

#### 统计表数据记录数
```
java -cp java-db-tools-1.0.jar com.litongjava.db.tools.service.GetOracleEmptyTableNameService jdbcUrl jdbcUser jdbcPassowrd tablespaceName
```
eg
````
java -cp java-db-tools-1.0.jar com.litongjava.db.tools.service.GetOracleEmptyTableNameService jdbc:oracle:thin:@192.168.3.9:1521:xe JGWNEW JGWNEW JGWNEW
````