package com.example.user.edeasy;

/**
 * Created by ASUS on 5/31/2017.
 */

class UserSnap {

    //having the same values as the user initially has in the database so that Frebase can match and
    //deserialize
    String email;
    String password;

    public UserSnap(){
        // empty default constructor, necessary for Firebase to be able to deserialize users
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
