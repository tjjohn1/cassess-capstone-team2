package edu.asu.cassess.service.rest;

import edu.asu.cassess.model.Taiga.CourseList;
import org.json.JSONObject;

import edu.asu.cassess.persist.entity.rest.Course;

import java.util.List;


public interface ICourseService {

    <T> Object create(Course course);

    <T> Object update(Course course);

    <T> Object read(String course);

    <T> Object delete(Course course);

    <T> List<Course> listRead();

    List<CourseList> listGetCourses();

    JSONObject listCreate(List<Course> courses);

    JSONObject listUpdate(List<Course> courses);
}
