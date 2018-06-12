# MandolApp
An android application created under the framework of the Mandola EU project by the University of Cyprus. Allows the reporting of social media posts containing hate speech to relevant authorities.

**Instructions to use**

1. Download zip project file and extract to desired location or clone github project
1. Launch Android Studio, click **File->Open** and choose parent application directory
1. Download Tooleap SDK jar from (https://developer.tooleap.com/download/)
1. Create directory called libs under MandolApp/app and place downloaded jar in MandolApp/app/libs directory
1. Open AndroidManifest.xml and edit the following code: 
            `<meta-data`
            `android:name="com.tooleap.sdk.apiKey"`
            `android:value="API_KEY" />` 
   adding the API_KEY given by the Tooleap Early Access Program.

