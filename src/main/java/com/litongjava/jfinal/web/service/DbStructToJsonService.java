package com.litongjava.jfinal.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbStructToJsonService {

  private String ds1 = "datasource1";

  private String[] datasource1 = {
      // URL
      "jdbc:mysql://192.168.3.9/cj_chaofu?useunicode=true&characterEncoding=utf8&useSSL=false",
      // user and password
      "root", "Zh)D^dlf" };

  /**
   * 分析mysql数据库的表表结构,导出Json格式的数组,数据格式如下
  { field: 'configId', title: '配置ID' },
  field是字段端转为驼峰格式,title是备注中的内容
   * @return
   */
  public String toJson(String tableName,String tableSchema) {
    // 链接数据库
    String sql = "SELECT COLUMN_NAME, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + tableName + "' AND TABLE_SCHEMA = '" + tableSchema + "'";
    log.info(sql);
    List<Record> findResult = Db.find(sql);
    if (findResult != null) {
      log.info(ds1 + "连接成功");
      log.info("size:{}",findResult.size());
    }
    String[] columns = {"field", "title"};
    List<Map<String, String>> rows = new ArrayList<>();
    for (Record record : findResult) {
      Map<String, String> row = new HashMap<>();
      String columnName = toCamelCase(record.getStr("COLUMN_NAME"));
      row.put(columns[0], "\""+ columnName + "\"");
      String columnComment = record.getStr("COLUMN_COMMENT");
      columnComment=columnComment.split(" ")[0];
      row.put(columns[1], "\""+ columnComment +"\"");
      rows.add(row);
    }

    //String jsonStr = JSONUtil.toJsonStr(rows);
    return rows.toString().replace("=", ":");

  }

  private static String toCamelCase(String s) {
    StringBuilder sb = new StringBuilder();
    boolean nextUpperCase = false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '_') {
        nextUpperCase = true;
      } else {
        if (nextUpperCase) {
          sb.append(Character.toUpperCase(c));
          nextUpperCase = false;
        } else {
          sb.append(Character.toLowerCase(c));
        }
      }
    }
    return sb.toString();
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
    DbStructToJsonService dbStructToJsonService = new DbStructToJsonService();
    dbStructToJsonService.start();
    String json = dbStructToJsonService.toJson("cf_alarm","cj_chaofu");
    System.out.println(json);
    //[{ field: 'configId', title: '配置ID' }, { field: 'shipName', title: '船舶名称' },{ field: 'mmsi', title: 'MMSI' }, { field: 'longitude', title: '经度' }]
     
  }

}
