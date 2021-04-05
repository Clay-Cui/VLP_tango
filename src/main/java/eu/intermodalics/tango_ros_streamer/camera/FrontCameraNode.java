package eu.intermodalics.tango_ros_streamer.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.app.Activity;

import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.ros.internal.message.MessageBuffers;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import eu.intermodalics.tango_ros_common.ConnectedNodeLogger;
import sensor_msgs.Image;

public class FrontCameraNode  extends AbstractNodeMain implements NodeMain, SensorEventListener {
    private static final String TAG = FrontCameraNode.class.getSimpleName();
    private static final String NODE_NAME = "android";

    private ConnectedNode mConnectedNode;
    private Publisher<sensor_msgs.CompressedImage> imagePublisher;
    private Publisher<sensor_msgs.CameraInfo> cameraInfoPublisher;

    private Camera mCameraMessage;
    private ConnectedNodeLogger mLog;

    public FrontCameraNode(Activity activity){
//        activity.getSystemService(Context.CAMERA_SERVICE);

    }

    public void onStart(ConnectedNode connectedNode){
        mConnectedNode = connectedNode;
        mLog = new ConnectedNodeLogger(connectedNode);
        imagePublisher = connectedNode.newPublisher("android/frontCamera", Image._TYPE);
        mCameraMessage = mConnectedNode.getTopicMessageFactory().newFromType("sensor_msgs/front_camera");
        cameraInfoPublisher = connectedNode.newPublisher("android/frontCamera", sensor_msgs.CameraInfo._TYPE);

    }


    public void publishImage(Bitmap bitmap){
        ChannelBufferOutputStream stream;
        stream = new ChannelBufferOutputStream(MessageBuffers.dynamicBuffer());
        sensor_msgs.CompressedImage image=imagePublisher.newMessage();

        Time currentTime = mConnectedNode.getCurrentTime();
        String frameId = "android/frontCamera";
        image.setFormat("jpeg");
        image.getHeader().setStamp(currentTime);
        image.getHeader().setFrameId(frameId);

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        image.setData(stream.buffer().copy());
        stream.buffer().clear();
        imagePublisher.publish(image);

        sensor_msgs.CameraInfo cameraInfo = cameraInfoPublisher.newMessage();
        cameraInfo.getHeader().setStamp(currentTime);
        cameraInfo.getHeader().setFrameId(frameId);
        //todo:Add more attribute here, like imu or depth
        cameraInfoPublisher.publish(cameraInfo);
    }










    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(NODE_NAME);
    }
}
