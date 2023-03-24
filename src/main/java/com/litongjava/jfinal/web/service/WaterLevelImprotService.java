package com.litongjava.jfinal.web.service;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
 
/**
 * @author litong
 * @date 2020年9月22日_下午10:38:45 
 * @version 1.0 
 * @desc 从水位app上查询出需要的
 */
@Slf4j
public class WaterLevelImprotService {
 
  private String ds1 = "datasource1";
  private String ds2 = "datasource2";
 
  private String[] datasource1 = {
      "jdbc:mysql://xxx/yangtze_river_app?useunicode=true&characterEncoding=utf8&useSSL=false",
      "yangtze_river_ap", "" };
 
  private String[] datasource2 = { "jdbc:mysql://127.0.0.1:3306/cjwb?useunicode=true&characterEncoding=utf8&serverTimezone=UTC",
      "cjwb", "xxx" };
 
  /**
   * 启动数据源
   */
  private void start() {
    DruidPlugin plugin1 = new DruidPlugin(datasource1[0], datasource1[1], datasource1[2]);
    ActiveRecordPlugin arp1 = new ActiveRecordPlugin(ds1, plugin1);
    plugin1.start();
    arp1.start();
 
    DruidPlugin plugin2 = new DruidPlugin(datasource2[0], datasource2[1], datasource2[2]);
    ActiveRecordPlugin arp2 = new ActiveRecordPlugin(ds2, plugin2);
    plugin2.start();
    arp2.start();
  }
 
 
  /**
   * 判断数据源是否连接成功
   */
  public void selectFromAllDatasource() {
    List<Record> find = Db.use(ds1).find("select 1");
    if (find != null) {
      log.info(ds1 + "连接成功");
    }
 
    find = Db.use(ds2).find("select 1");
    if (find != null) {
      log.info(ds2 + "连接成功");
    }
  }
 
  /**
   * 从datasource1查询中water_level,处理后插入到river_level,名称设置为spider
   * datasource1格式
   * {site_name:宜宾, level:1, id:857fee196f53390db332060a90028cce, time:2017-01-01}
   * datasource2格式
   * {site_name:宜宾, level:1, id:857fee196f53390db332060a90028cce, time:2017-01-01 08:00}
   */
  public void fromDatasource1ToDatasource2() {
    String sqlString = "select * from water_level where time>2020-08-15";
    List<Record> find = Db.use(ds1).find(sqlString);
    log.info("水位总条数:{}", find.size());
    String sqlString2 = "select count(*) from river_level where site_name=? and time=?";
    List<Record> insertList = new ArrayList<Record>();
    for (Record r : find) {
      String timeString = r.getStr("time");
 
      String timeAMString = timeString + " 08:00:00";
 
      Record countRecord = Db.use(ds2).findFirst(sqlString2, r.getStr("site_name"), timeAMString);
 
      if (countRecord.getInt("count(*)") == 0) {
        String random =  UUID.fastUUID().toString();
        Record insertRecord = new Record();
        insertRecord.set("id", random);
        insertRecord.set("site_name", r.getStr("site_name"));
        insertRecord.set("level", r.getStr("level"));
        insertRecord.set("time", timeAMString);
        insertRecord.set("name", "spider");
        insertList.add(insertRecord);
      }
 
      String timePMString = timeString + " 17:00:00";
      countRecord = Db.use(ds2).findFirst(sqlString2, r.getStr("site_name"), timePMString);
 
      if (countRecord.getInt("count(*)") == 0) {
        String random =  UUID.fastUUID().toString();
        Record insertRecord = new Record();
        insertRecord.set("id", random);
        insertRecord.set("site_name", r.getStr("site_name"));
        insertRecord.set("level", r.getStr("level"));
        insertRecord.set("time", timePMString);
        insertRecord.set("name", "spider");
        insertList.add(insertRecord);
      }
 
    }
 
    log.info("插入的水位总条数:{}", insertList.size());
//    for (Record r : insertList) {
//      System.out.println(r);
//    }
    Db.use(ds2).batchSave("river_level", insertList, insertList.size());
 
  }
 
  public void getDataSourceCountResult() {
//    String sql = "select count(*) from river_level where site_name='黄冈' and time='2020-01-14 08:00:00';";
    String sql = "select count(*) from river_level where site_name='宜宾' and time='2020-08-13 08:00:00';";
    Record findFirst = Db.use(ds1).findFirst(sql);
    System.out.println(findFirst);
    /**
     * 存放返回 {count(*):1}
     * 不存在 返回 {count(*):0}
     */
  }
 
  public static void main(String[] args) {
    WaterLevelImprotService waterLevelImprotService = new WaterLevelImprotService();
    waterLevelImprotService.start();
    waterLevelImprotService.fromDatasource1ToDatasource2();
//     waterLevelImprotService.selectFromAllDatasource();
//    waterLevelImprotService.getDataSourceCountResult();
  }
}