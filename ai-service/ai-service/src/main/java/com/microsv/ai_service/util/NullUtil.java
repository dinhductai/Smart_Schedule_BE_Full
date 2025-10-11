package com.microsv.ai_service.util;

public class NullUtil {
    public static void checkUserNullByUserId(Long userId){
        if(userId == null) {
            throw new IllegalArgumentException("User not authenticated, cannot save chat memory");
        }
    }
}
