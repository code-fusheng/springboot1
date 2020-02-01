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
