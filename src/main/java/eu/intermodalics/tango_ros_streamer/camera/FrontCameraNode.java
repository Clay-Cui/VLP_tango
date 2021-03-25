package eu.intermodalics.tango_ros_streamer.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.app.Activity;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import eu.intermodalics.tango_ros_common.ConnectedNodeLogger;

public class FrontCameraNode  extends AbstractNodeMain implements NodeMain, SensorEventListener {
    private static final String TAG = FrontCameraNode.class.getSimpleName();
    private static final String NODE_NAME = "android";

    private ConnectedNode mConnectedNode;
    private Publisher<Camera> mCameraPublisher;
    private Camera mCameraMessage;
    private ConnectedNodeLogger mLog;

    private FrontCameraNode(Activity activity){
        activity.getSystemService(Context.CAMERA_SERVICE);
    }

    public void onStart(ConnectedNode connectedNode){
        mConnectedNode = connectedNode;
        mLog = new ConnectedNodeLogger(connectedNode);
        mCameraPublisher = connectedNode.newPublisher("android/frontCamera","sensor_msgs/front_camera");
        mCameraMessage = mConnectedNode.getTopicMessageFactory().newFromType("sensor_msgs/front_camera");
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
