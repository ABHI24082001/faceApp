import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, TouchableOpacity} from 'react-native';
import {NativeEventEmitter, NativeModules} from 'react-native';
import {Camera} from 'react-native-camera-kit';

const {FaceDetectionModule} = NativeModules;

export default function App() {
  const [faces, setFaces] = useState([]);
  const [isFrontCamera, setIsFrontCamera] = useState(true);
  const eventEmitter = new NativeEventEmitter(FaceDetectionModule);

  useEffect(() => {
    const listener = eventEmitter.addListener('onFaceDetected', data => {
      setFaces([data]); // Update detected faces
    });

    FaceDetectionModule.startCamera().catch(console.error);

    return () => listener.remove();
  }, []);

  const toggleCamera = () => {
    const newCameraState = !isFrontCamera;
    setIsFrontCamera(newCameraState);

    // If the native module does not accept arguments, restart camera after state update
    FaceDetectionModule.startCamera().catch(console.error);
  };
  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>Real-Time Face Detection</Text>
      </View>

      {/* Camera Preview */}
      <View style={styles.cameraContainer}>
        <Camera
          style={styles.camera}
          cameraType={isFrontCamera ? 'front' : 'back'}
        />

        {/* Bounding Boxes */}
        {faces.map((face, index) => (
          <BoundingBox key={index} face={face} />
        ))}
      </View>

      {/* Toggle Camera Button */}
      <TouchableOpacity style={styles.button} onPress={toggleCamera}>
        <Text style={styles.buttonText}>
          {isFrontCamera ? 'Switch to Back Camera' : 'Switch to Front Camera'}
        </Text>
      </TouchableOpacity>
    </View>
  );
}

/** 🟢 Bounding Box Component */
const BoundingBox = ({face}) => {
  return 
    return (
    <View
      style={[
        styles.boundingBox,
        {
          left: x * scaleX,
          top: y * scaleY,
          width: width * scaleX,
          height: height * scaleY,
        },
      ]}
    />
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, backgroundColor: '#000'},
  header: {padding: 15, backgroundColor: '#1e1e1e', alignItems: 'center'},
  title: {color: '#fff', fontSize: 18, fontWeight: 'bold'},
  cameraContainer: {flex: 1, position: 'relative'},
  camera: {flex: 1},
  boundingBox: {
    position: 'absolute',
    borderWidth: 2,
    borderColor: 'red',
    borderRadius: 5,
  },
  button: {
    padding: 15,
    backgroundColor: '#007AFF',
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonText: {color: '#fff', fontSize: 16, fontWeight: 'bold'},
});
