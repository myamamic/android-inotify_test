LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := mounttest
LOCAL_SRC_FILES := mounttest.c

LOCAL_LDLIBS :=  -ldl -llog

include $(BUILD_SHARED_LIBRARY)
