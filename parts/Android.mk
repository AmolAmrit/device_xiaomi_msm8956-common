#
# Copyright (C) 2017-2018 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.preference_preference

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := XiaomiParts
LOCAL_CERTIFICATE := platform
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_PRIVILEGED_MODULE := true

LOCAL_USE_AAPT2 := true

LOCAL_STATIC_ANDROID_LIBRARIES := \
    android-support-v4 \
    android-support-v13 \
    android-support-v7-recyclerview \
    android-support-v7-preference \
    android-support-v7-appcompat \
    android-support-v14-preference \

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res \
    $(package_resource_overlays) $(LOCAL_RESOURCE_DIR) \
    $(TOP)/packages/resources/devicesettings/res

package_resource_overlays := $(strip \
    $(wildcard $(foreach dir, $(PRODUCT_PACKAGE_OVERLAYS), \
      $(addprefix $(dir)/, packages/apps/XiaomiParts/res))) \
    $(wildcard $(foreach dir, $(DEVICE_PACKAGE_OVERLAYS), \
      $(addprefix $(dir)/, packages/apps/XiaomiParts/res))))

LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_DEX_PREOPT := false

include frameworks/base/packages/SettingsLib/common.mk

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
