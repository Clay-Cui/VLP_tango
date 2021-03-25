package eu.intermodalics.tango_ros_streamer.camera;

import android.hardware.Camera.Size;

public interface RawImageListener {

    void onNewRawImage(byte[] data, Size size);

}