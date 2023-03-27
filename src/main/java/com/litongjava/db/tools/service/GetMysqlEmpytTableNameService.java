package com.litongjava.db.tools.service;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.litongjava.db.tools.model.KvObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author litongjava@qq.com on 2023/3/27 12:46
 */
@Slf4j
public class GetMysqlEmpytTableNameService {

  private String sqlCountTableTemplate = "select count(1) from %s";
  DruidPlugin plugin1 = null;
  ActiveRecordPlugin arp1 = null;


  public List<KvObject<Integer>> index(String tablespaceName) {
    // 连接接数据库
    String sql = "show tables;";
    log.info(sql);
    List<Record> findResult = Db.find(sql);
    if (findResult != null) {
      log.info("连接成功");
      log.info("size:{}", findResult.size());
    }

    List<KvObject<Integer>> retval = new ArrayList<>();

    for (Record record : findResult) {
      String tableName = record.getStr("Tables_in_" + tablespaceName);
      String sqlCount = String.format(sqlCountTableTemplate, tableName);
      Integer integer = Db.queryInt(sqlCount);
      retval.add(new KvObject<>(tableName, integer));
    }


    return retval;

  }


  /**
   * 启动数据源
   */
  private void start(String jdbcUrl, String username, String password) {
    plugin1 = new DruidPlugin(jdbcUrl, username, password);
    arp1 = new ActiveRecordPlugin(plugin1);
    plugin1.start();
    arp1.start();
  }

  public void stop() {
    arp1.stop();
    plugin1.stop();
  }

  public static void main(String[] args) {

    String jdbcUrl = null;
    String username = null;
    String password = null;
    String tablespaceName = null;
    if (args.length != 4) {
      System.out.println("please use jdbcUrl jdbcUser jdbcPassowrd tablespaceName");
      return;
    } else {
      jdbcUrl = args[0];
      username = args[1];
      password = args[2];
      tablespaceName = args[3];
      log.info("jdbcUrl:{}", jdbcUrl);
      log.info("username:{},password:{},tablespaceName:{}", username, password, tablespaceName);
    }


    GetMysqlEmpytTableNameService service = new GetMysqlEmpytTableNameService();
    service.start(jdbcUrl, username, password);
    List<KvObject<Integer>> list = service.index(tablespaceName);
    System.out.println("tableName\tcount");
    for (KvObject<Integer> integerKvObject : list) {
      System.out.println(integerKvObject.getK() + "\t" + integerKvObject.getV());
    }
    service.stop();
  }

}
