React Native Face Detection App

This is a React Native project that implements real-time face detection using react-native-camera-kit and a custom native module for face tracking.

Features

📷 Real-time face detection

🟦 Bounding boxes around detected faces

🔄 Toggle between front and back cameras

🚀 Optimized for smooth performance

Getting Started

Note: Make sure you have completed the Set Up Your Environment guide before proceeding.

Step 1: Install Dependencies

Clone this repository and install the dependencies:

# Clone the project
git clone <repo_url>
cd <project_folder>

# Install dependencies
npm install  # or yarn install

For iOS:

cd ios
pod install
cd ..

Step 2: Start Metro

Metro is the JavaScript bundler for React Native. Start the Metro server with:

npm start  # or yarn start

Step 3: Build and Run the App

Android

npm run android  # or yarn android

iOS

npm run ios  # or yarn ios

Usage

Open the app.

Allow camera permissions.

The camera will detect faces and draw bounding boxes.

Click the toggle button to switch cameras.

Troubleshooting

If you get a FaceDetectionModule error, make sure the native module is correctly linked.

For iOS, ensure that you have run pod install inside the ios/ directory.

If the camera doesn't open, check if the app has camera permissions in the device settings.

Use Docs 
React Native Docs
React Native Camera Kit