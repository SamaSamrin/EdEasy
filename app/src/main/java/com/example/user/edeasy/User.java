package com.example.user.edeasy;

/**
 * Created by ASUS on 5/29/2017.
 */

class User {

    private Course[] courses_assigned;
    private String department;
    public String email;
    private String full_name;
//    private String firstName;
//    private String lastName;
    public String password;
    private String student_id;
    private String role;
    private String semester;

    public User(){

    }

    User(String email, String password){
        this.email = email;
        this.password = password;
    }

    User(Course[] courses_assigned, String department, String email, String full_name,
         String password, String semester, String student_id){
        this.department = department;
        this.email = email;
        this.full_name = full_name;
        this.password = password;
        this.semester = semester;
        this.student_id = student_id;
    }

    public String getEmail(){
        return email;
    }

    void setFull_name(String name){ full_name = name; }

    public String getFull_name(){
        return full_name;
    }

    String getPassword(){
        return password;
    }

    String getRole(){
        return role;
    }

    void setRole(String role){
        this.role = role;
    }
}
