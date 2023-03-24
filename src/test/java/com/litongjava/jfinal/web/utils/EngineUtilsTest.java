package com.litongjava.jfinal.web.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.litongjava.jfinal.web.model.ElInput;

/**
 * @author litongjava@qq.com on 2023/3/24 17:36
 */
public class EngineUtilsTest {

  Engine engine = EngineUtils.getEngine();

  @Test
  public void getEngine() {

    Template template = engine.getTemplate("/index.html");
    String string = template.renderToString();
    System.out.println(string);
  }

  @Test
  public void tableFormElInput() {
    ElInput name = new ElInput("通知人员", "name", "temp.name");
    ElInput phone = new ElInput("手机号", "phone", "temp.phone");
    List<ElInput> list = Arrays.asList(name, phone);
    Kv kv = Kv.by("elList", list);

    Template template = engine.getTemplate("/table-form-el-input.html");
    String string = template.renderToString(kv);
    System.out.println(string);

  }
}