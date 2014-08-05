#LOCAL_PATH := $(call my-dir)
#include $(CLEAR_VARS)
#LOCAL_MODULE := mp3lame
#LOCAL_SRC_FILES := libmp3lame/bitstream.c libmp3lame/encoder.c libmp3lame/fft.c libmp3lame/gain_analysis.c libmp3lame/id3tag.c libmp3lame/lame.c libmp3lame/mpglib_interface.c libmp3lame/newmdct.c libmp3lame/presets.c libmp3lame/psymodel.c libmp3lame/quantize.c libmp3lame/quantize_pvt.c libmp3lame/reservoir.c libmp3lame/set_get.c libmp3lame/tables.c libmp3lame/takehiro.c libmp3lame/util.c libmp3lame/vbrquantize.c libmp3lame/VbrTag.c libmp3lame/version.c
#LOCAL_C_INCLUDES := jni/libmp3lame/lame
#include $(BUILD_STATIC_LIBRARY)

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_C_INCLUDES := /Applications/android-ndk-r9b/sources/ffmpeg-2.1.1
LOCAL_CFLAGS := -Wdeprecated-declarations
LOCAL_MODULE := videokit
ANDROID_LIB := -landroid
LOCAL_LDLIBS += -llog -ljnigraphics -lz obj/local/armeabi/libmp3lame.a
LOCAL_SRC_FILES := videokit/com_novedia_radiofrance_ffmpeg_Videokit.c videokit/ffmpeg.c videokit/cmdutils.c videokit/ffmpeg_opt.c videokit/ffmpeg_filter.c
LOCAL_STATIC_LIBRARIES := libavdevice libavformat libavfilter libavcodec libwscale libavutil libswresample libswscale libpostproc
include $(BUILD_SHARED_LIBRARY)
$(call import-module,ffmpeg-2.1.1/android/arm)
