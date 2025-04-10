const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// Function đơn giản
exports.helloWorld = functions.https.onRequest((req, res) => {
  res.send("Hello from Firebase!");
});

// Cú pháp mới cho Firebase Functions v6.0.1
exports.sendNotificationOnCreate = functions.firestore
  .onDocumentCreated({
    document:'notifications/{notificationId}',
    enforceAppCheck: false,
    ingressSettings: 'ALLOW_ALL'}, async (event) => {
    const snapshot = event.data;
    if (!snapshot) {
      console.log('No data associated with the event');
      return null;
    }
    
    const notification = snapshot.data();
    const userId = notification.userId;
    
    console.log(`Processing notification for user: ${userId}`);
    
    try {
      const userDoc = await admin.firestore().collection('users').doc(userId).get();
      
      if (!userDoc.exists) {
        console.log(`User document ${userId} doesn't exist`);
        return null;
      }
      
      const userData = userDoc.data();
      const fcmToken = userData.fcmTokens;
      
      if (!fcmToken) {
        console.log(`User ${userId} has no FCM token`);
        return null;
      }
      
      console.log(`Sending notification to token: ${fcmToken}`);
      
      const message = {
        notification: {
          title: notification.title,
          body: notification.body
        },
        token: fcmToken
      };
      
      const response = await admin.messaging().send(message);
      console.log('Successfully sent message:', response);
      return response;
    } catch (error) {
      console.error('Error sending notification:', error);
      return null;
    }
  });