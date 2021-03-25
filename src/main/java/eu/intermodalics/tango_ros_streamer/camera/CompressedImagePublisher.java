package eu.intermodalics.tango_ros_streamer.camera;

import com.google.common.base.Preconditions;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera.Size;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.ros.internal.message.MessageBuffers;
import org.ros.message.Time;
import org.ros.namespace.NameResolver;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 * Publishes preview frames.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
public class CompressedImagePublisher implements RawImageListener {

    private final ConnectedNode connectedNode;
    private final Publisher<sensor_msgs.CompressedImage> imagePublisher;
    private final Publisher<sensor_msgs.CameraInfo> cameraInfoPublisher;

    private String robotName;
    private int camera_id;

    private byte[] rawImageBuffer;
    private Size rawImageSize;
    private YuvImage yuvImage;
    private Rect rect;
    private ChannelBufferOutputStream stream;

    public CompressedImagePublisher(ConnectedNode connectedNode, String robotName, int camera_id) {
        this.connectedNode = connectedNode;
        this.robotName = robotName;
        this.camera_id = camera_id;
        imagePublisher = connectedNode.newPublisher("/android/"+robotName+"/camera_"+camera_id+"/image/compressed", sensor_msgs.CompressedImage._TYPE);
        cameraInfoPublisher = connectedNode.newPublisher("/android/"+robotName+"/camera_"+camera_id+"/camera_info", sensor_msgs.CameraInfo._TYPE);
        stream = new ChannelBufferOutputStream(MessageBuffers.dynamicBuffer());
    }

    @Override
    public void onNewRawImage(byte[] data, Size size) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(size);
        if (data != rawImageBuffer || !size.equals(rawImageSize)) {
            rawImageBuffer = data;
            rawImageSize = size;
            yuvImage = new YuvImage(rawImageBuffer, ImageFormat.NV21, size.width, size.height, null);
            rect = new Rect(0, 0, size.width, size.height);
        }

        Time currentTime = connectedNode.getCurrentTime();
        String frameId = "/android/camera_"+camera_id;
        sensor_msgs.CompressedImage image = imagePublisher.newMessage();
        image.setFormat("jpeg");
        image.getHeader().setStamp(currentTime);
        image.getHeader().setFrameId(frameId);

        Preconditions.checkState(yuvImage.compressToJpeg(rect, 20, stream));
        image.setData(stream.buffer().copy());
        stream.buffer().clear();

        imagePublisher.publish(image);

        sensor_msgs.CameraInfo cameraInfo = cameraInfoPublisher.newMessage();
        cameraInfo.getHeader().setStamp(currentTime);
        cameraInfo.getHeader().setFrameId(frameId);

        cameraInfo.setWidth(size.width);
        cameraInfo.setHeight(size.height);
        cameraInfoPublisher.publish(cameraInfo);
    }
}