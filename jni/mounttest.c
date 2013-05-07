#include <jni.h>
#include <string.h>
#include <sys/inotify.h>
#include <errno.h>

#include <android/log.h>
#define LOG_TAG "MYAMA-NATIVE"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

jstring
Java_yama_tp_os_mounttest_MainActivity_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env, "Hello from JNI !");
}

jint
Java_yama_tp_os_mounttest_MainActivity_watch( JNIEnv* env,
        									jobject thiz ) {
	int nfd;
	int res;
	char event_buf[512];

	int event_mask = IN_ALL_EVENTS;
	int event_count = 1;
	struct inotify_event *event;
	const char* file_name = "/mnt/ext_sdcard";

	nfd = inotify_init();
	if(nfd < 0) {
		LOGE("inotify_init failed, %s\n", strerror(errno));
		return 1;
	}

	res = inotify_add_watch(nfd, file_name, event_mask);
	if(res < 0) {
		LOGE("inotify_add_watch failed for %s, %s\n", file_name, strerror(errno));
		return 1;
	}

	while(1) {
		int event_pos = 0;
		LOGE("CALL read()");
	    res = read(nfd, event_buf, sizeof(event_buf));
		LOGE("RETURN read()");
		if(res < (int)sizeof(*event)) {
			if(errno == EINTR) {
				continue;
			}
			LOGE("could not get event, %s\n", strerror(errno));
			return 1;
		}

		while(res >= (int)sizeof(*event)) {
			int event_size;
			event = (struct inotify_event *)(event_buf + event_pos);
			LOGE("%s: %08x %08x \"%s\"\n", file_name, event->mask, event->cookie, event->len ? event->name : "");

			if(event_count && --event_count == 0) {
				return 0;
			}
			event_size = sizeof(*event) + event->len;
			res -= event_size;
			event_pos += event_size;
		}
	}

	return 0;
}
