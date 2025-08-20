package org.studyeasy.SpringRestdemo.util.constants;

public enum Authority {
    READ,
    WRITE,
    UPDATE,
    TEACHER,
    USER, // Can update delete self object, read anything
    ADMIN // Can read update delete any object
    
}
