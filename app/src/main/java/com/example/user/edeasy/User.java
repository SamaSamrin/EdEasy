package com.example.user.edeasy;

/**
 * Created by ASUS on 5/29/2017.
 */

class User {

    private String username;
    private String firstName;
    private String lastName;
    public String email;
    public String password;
    private long id;
    private String department;
    private String role;
    private Course[] assignedCourses;
    private String semester;

    User(String email, String password){
        this.email = email;
        this.password = password;
    }
}
