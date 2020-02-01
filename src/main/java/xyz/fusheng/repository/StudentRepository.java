package xyz.fusheng.repository;

import xyz.fusheng.entity.Student;

import java.util.Collection;

public interface StudentRepository {
    public Collection<Student> findAll();
    public Student findById(long id);
    public void saveOrUpdate(Student student);
    public void deletebyId(long id);
}
