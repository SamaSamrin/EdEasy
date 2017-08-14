package com.example.user.edeasy;

import com.example.user.edeasy.fragments.course_materials.Course;

/**
 * Created by ASUS on 5/29/2017.
 */

public class User {

   // private Course[] courses_assigned;
    private String department;
    private String email;
    private String full_name;
//    private String firstName;
//    private String lastName;
    private String password;
    private String student_id;
    private String role;
    //private String key;
    // private String semester;

    public User(){

    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public User(Course[] courses_assigned, String department, String email, String full_name,
         String password, String semester, String student_id){
        this.department = department;
        this.email = email;
        this.full_name = full_name;
        this.password = password;
        //this.semester = semester;
        this.student_id = student_id;
    }

    public String getEmail(){
        return email;
    }

    void setFull_name(String name){ full_name = name; }

    String getFull_name(){
        return full_name;
    }

    public String getPassword(){
        return password;
    }

    String getRole(){
        return role;
    }

    void setRole(String role){
        this.role = role;
    }

//    String getKey(){ return  key;}
//
//    void setKey(String key){this.key = key;}
}
