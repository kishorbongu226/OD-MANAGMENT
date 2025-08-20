package org.studyeasy.SpringRestdemo.util.constants;

public enum Authority {
    READ,
    WRITE,
    UPDATE,
    ROLE_TEACHER,
    ROLE_USER, // Can update delete self object, read anything
    ROLE_ADMIN // Can read update delete any object
    
}
