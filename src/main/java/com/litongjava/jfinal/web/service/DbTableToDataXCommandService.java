package com.litongjava.jfinal.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.litongjava.jfinal.web.model.ElInput;
import com.litongjava.jfinal.web.utils.EngineUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author litongjava@qq.com on 2023/3/27 11:56
 * 查询出mysql中的所有表,拼接成datax同步命令
 */
@Slf4j
public class DbTableToDataXCommandService {

  private String ds1 = "datasource1";

  private String[] datasource1 = {
    // URL
    "jdbc:mysql://192.168.3.9/jgwnew?useunicode=true&characterEncoding=utf8&useSSL=false",
    // user and password
    "root", "Zh)D^dlf" };

  private String commandTemplate="python2 bin/datax.py job/ztbjgw_oracle_to_mysql_v1.0.json --jvm=\"-Xms8G -Xmx8G\" -p \"-DreadTb=%s -DwriteTb=%s\"";

  private String sqlTemplate="select count(1) from %s;";

  /**
   * 分析mysql数据库的表表结构,导出Json格式的数组,数据格式如下
   { field: 'configId', title: '配置ID' },
   field是字段端转为驼峰格式,title是备注中的内容
   * @return
   */
  public List<String> toCommand() {
    // 链接数据库
    String sql = "show tables;";
    log.info(sql);
    List<Record> findResult = Db.find(sql);
    if (findResult != null) {
      log.info(ds1 + "连接成功");
      log.info("size:{}", findResult.size());
    }

    List<String> retval = new ArrayList<>();

    for (Record record : findResult) {
      String tableName = record.getStr("Tables_in_jgwnew");
      retval.add(String.format(commandTemplate,tableName,tableName));
    }

    for (Record record : findResult) {
      String tableName = record.getStr("Tables_in_jgwnew");
      retval.add(String.format(sqlTemplate,tableName));
    }


    return retval;

  }


  /**
   * 启动数据源
   */
  private void start() {
    DruidPlugin plugin1 = new DruidPlugin(datasource1[0], datasource1[1], datasource1[2]);
    ActiveRecordPlugin arp1 = new ActiveRecordPlugin(ds1, plugin1);
    plugin1.start();
    arp1.start();

  }

  public static void main(String[] args) {
    DbTableToDataXCommandService service = new DbTableToDataXCommandService();
    service.start();
    List<String> strings = service.toCommand();
    for (String string : strings) {
      System.out.println(string);
    }
    // [{ field: 'configId', title: '配置ID' }, { field: 'shipName', title: '船舶名称' },{ field: 'mmsi', title: 'MMSI' }, { field: 'longitude', title: '经度' }]

  }

}
