const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.deleteSpecificVideo = functions.https.onRequest((request, response) => {
    const firestore = admin.firestore();
    const videoId = "videoId";  // replace this with the actual ID
    const docRef = firestore.collection('Videos').doc(videoId);

    docRef.delete()
        .then(() => {
            response.send(`Video with ID: ${videoId} has been deleted successfully.`);
        })
        .catch(error => {
            console.error("Error removing document: ", error);
            response.status(500).send(error);
        });
});
