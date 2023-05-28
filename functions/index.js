const functions = require("firebase-functions");
const admin = require('firebase-admin');
admin.initializeApp();

exports.deleteOldItems = functions.pubsub.schedule('every 5 minutes').onRun((context) => {
    const currentTime = admin.firestore.Timestamp.now();
    const tenSecondsAgo = admin.firestore.Timestamp.fromDate(new Date(Date.now() - 10 * 1000)); // 10 seconds ago
    const twentySecondsAgo = admin.firestore.Timestamp.fromDate(new Date(Date.now() - 20 * 1000)); // 20 seconds ago

    const videosRef = admin.firestore().collection('Videos');

    return videosRef.where('publish_type', '==', true).where('creationDate', '<=', tenSecondsAgo)
        .get()
        .then(querySnapshot => {
            querySnapshot.forEach(doc => {
                doc.ref.delete().then(() => {
                    console.log('Document deleted successfully');
                }).catch(error => {
                    console.error('Error deleting document: ', error);
                });
            });
        })
        .then(() => {
            return videosRef.where('publish_type', '==', false).where('creationDate', '<=', twentySecondsAgo)
                .get()
                .then(querySnapshot => {
                    querySnapshot.forEach(doc => {
                        doc.ref.delete().then(() => {
                            console.log('Document deleted successfully');
                        }).catch(error => {
                            console.error('Error deleting document: ', error);
                        });
                    });
                })
        })
        .catch(error => {
            console.log('Error getting documents: ', error);
        });
});
