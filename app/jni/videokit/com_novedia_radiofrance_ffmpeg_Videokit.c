#include <android/log.h>
#include "logjam.h"
#include "com_novedia_radiofrance_ffmpeg_Videokit.h"

#include <stdlib.h>
#include <stdbool.h>

int main(int argc, char **argv);

JavaVM *sVm = NULL;

#define LOG_ERROR(message) __android_log_write(ANDROID_LOG_ERROR, "com.novedia.radiofrance.ffmpeg.Videokit<native>", message)
#define LOG_INFO(message) __android_log_write(ANDROID_LOG_INFO, "com.novedia.radiofrance.ffmpeg.Videokit<native>", message)

jint JNI_OnLoad( JavaVM* vm, void* reserved )
{
	LOG_INFO("Loading native library compiled at " __TIME__ " " __DATE__);
	sVm = vm;
	return JNI_VERSION_1_6;
}

JNIEXPORT jboolean JNICALL Java_com_novedia_radiofrance_ffmpeg_Videokit_run(JNIEnv *env, jobject obj, jobjectArray args)
{
	int i = 0;
	int argc = 0;
	char **argv = NULL;
	jstring *strr = NULL;

	if (args != NULL) {
		argc = (*env)->GetArrayLength(env, args);
		argv = (char **) malloc(sizeof(char *) * argc + 1);
		strr = (jstring *) malloc(sizeof(jstring) * argc);

        strr[0] = (jstring)(*env)->GetObjectArrayElement(env, args, 0);
		argv[0] = (char *)(*env)->GetStringUTFChars(env, strr[0], 0);
		LOG_INFO(argv[0]);
		argv[1] = "-y";
		LOG_INFO(argv[1]);
		for(i=1;i<argc;i++)
		{
			strr[i] = (jstring)(*env)->GetObjectArrayElement(env, args, i);
			argv[i+1] = (char *)(*env)->GetStringUTFChars(env, strr[i], 0);
			LOG_INFO(argv[i+1]);
		}
	}	

    LOG_INFO("Running main");
	int result = main(argc + 1, argv);
	LOG_INFO("Main ended");

	(*env)->ReleaseStringUTFChars(env, strr[0], argv[0]);
	for(i=1;i<argc;i++)
	{
		(*env)->ReleaseStringUTFChars(env, strr[i], argv[i+1]);
	}
	free(argv);
	free(strr);

	return result == 0;
}
