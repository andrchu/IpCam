/*
 * Copyright (C) 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chu.android.camera.open;


/**
 * Selects an appropriate implementation of {@link com.chu.android.camera.open.OpenCameraInterface} based on the device's
 * API level.
 */
public final class OpenCameraManager extends com.chu.android.common.PlatformSupportManager<com.chu.android.camera.open.OpenCameraInterface> {

  public OpenCameraManager() {
    super(com.chu.android.camera.open.OpenCameraInterface.class, new DefaultOpenCameraInterface());
    addImplementationClass(9, "com.google.zxing.client.android.camera.open.GingerbreadOpenCameraInterface");
  }

}
