# lesson-2 Spring Boot 整合 JSP

* 1.搭建简单 maven 工程
![image](8E3AB46A0EE143BEBE3B0DAFA6988A7B)

* 2.配置 pom.xml 相关依赖
```xml
  <!-- 继承父包 -->
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.4.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
  </parent>

  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>

    <!-- web启动jar -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 整合 JSP -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-tomcat</artifactId>
    </dependency>
    <dependency>
      <groupId>org.apache.tomcat.embed</groupId>
      <artifactId>tomcat-embed-jasper</artifactId>
    </dependency>

    <!-- JSTL -->
    <dependency>
      <groupId>jstl</groupId>
      <artifactId>jstl</artifactId>
      <version>1.2</version>
    </dependency>
    
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.18.10</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>
```
* 3.创建配置文件 application.yml
```yml
server:
  port: 8080
spring:
  mvc:
    view:
      prefix: /
      suffix: .jsp
```
* 4.创建控制类 HelloHandler 验证框架结构
```java
package xyz.fusheng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloHandler {
    @GetMapping("/index")
    public String index(){
        System.out.println("index...");
        return "index";
    }
}
```
+ 5.创建 Student 实体类
```java
package xyz.fusheng.entity;

import lombok.Data;

@Data
public class Student {
    private long id;
    private String name;
    private int age;
}
```
+ 6.创建 StudentRepository[student] 接口。
```java
package xyz.fusheng.repository;

import xyz.fusheng.entity.Student;
import java.util.List;

public interface StudentRepository {
    public List<Student> findAll();
    public Student findById(long id);
    public void saveOrUpdate(Student student);
    public void deletebyId(long id);
}
```
+ 7.创建 StudentRepositoryImpl 接口实现类。
```java
package xyz.fusheng.repository.impl;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.fusheng.entity.Student;
import xyz.fusheng.repository.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class StudentRepositoryImpl implements StudentRepository {
    // 定义静态 Map 代替数据库
    private static Map<Long,Student> studentMap;

    static {
        studentMap = new HashMap<>();
        // 下面必须配置Lombok插件，否则自行添加构造器
        studentMap.put(1L, new Student( 1L,"zhanghao", 21));
        studentMap.put(2L, new Student( 2L,"gonglin", 20));
        studentMap.put(3L, new Student( 3L,"fusheng", 22));
    }

    @Override
    public Collection<Student> findAll() {
        return studentMap.values();
    }

    @Override
    public Student findById(long id) {
        return studentMap.get(id);
    }

    @Override
    public void saveOrUpdate(Student student) {
        studentMap.put(student.getId(),student);
    }

    @Override
    public void deletebyId(long id) {
        studentMap.remove(id);
    }
}
```

* 9.重写 HelloHandler 前端控制器类
```java
package xyz.fusheng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import xyz.fusheng.entity.Student;
import xyz.fusheng.repository.StudentRepository;

@Controller
@RequestMapping("/hello")
public class HelloHandler {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/index")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("list",studentRepository.findAll());
        return modelAndView;
    }

    @GetMapping("/deleteById/{id}")
    public String deleteById(@PathVariable("id") long id){
        studentRepository.deletebyId(id);
        return "redirect:/hello/index";
    }

    @PostMapping("/save")
    public String save(Student student){
        studentRepository.saveOrUpdate(student);
        return "redirect:/hello/index";
    }

    @PostMapping("/update")
    public String update(Student student){
        studentRepository.saveOrUpdate(student);
        return "redirect:/hello/index";
    }

    @GetMapping("/findById/{id}")
    public ModelAndView findById(@PathVariable("id") long id){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("update");
        modelAndView.addObject("student",studentRepository.findById(id);
        return modelAndView;
    }
}
```
* 10.创建 index.jsp save.jsp update.jsp 前端页面

***index.jsp***
```html
<%--
  Created by IntelliJ IDEA.
  User: 25610
  Date: 2020/2/1
  Time: 12:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--页面解析 EL 表达式--%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>学生信息</h1>
    <table>
        <tr>
            <th>学生编号</th>
            <th>学生姓名</th>
            <th>学生年龄</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${list}" var="student">
            <tr>
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td>${student.age}</td>
                <td>
                    <a href="/hello/findById/${student.id}">修改</a>
                    <a href="/hello/deleteById/${student.id}">删除</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <a href="/save.jsp">添加学生</a>
</body>
</html>
```
***save.jsp***
```html
<%--
  Created by IntelliJ IDEA.
  User: 25610
  Date: 2020/2/1
  Time: 12:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="/hello/save" method="post">
        ID:<input type="text" name="id"/><br/>
        name:<input type="text" name="name"/><br/>
        age:<input type="text" name="age"/><br/>
        <input type="submit" value="提交"/>
    </form>
</body>
</html>
```
***update.jsp***
```html
<%--
  Created by IntelliJ IDEA.
  User: 25610
  Date: 2020/2/1
  Time: 12:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/hello/update" method="post">
    ID:<input type="text" name="id" value="${student.id}" readonly/><br/>
    name:<input type="text" name="name" value="${student.name}"/><br/>
    age:<input type="text" name="age" value="${student.age}"/><br/>
    <input type="submit" value="提交"/>
</form>
</body>
</html>
```
***项目结构：***
![image](1DA4A425EC6843B6B5861333B5A852A0)
***项目结果：***
![image](F43DEAE5127F42B18272C115F96A2409)

