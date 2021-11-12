# **HotMic Android SDK Sample**

This sample app demonstrates how to integrate HotMic Android SDK to your Android application.

### **1. Pre-requisite**

You will need an API key that has been generated specifically for your application. If you do not have one, please contact the HotMic team.

### **2. Configure Android Application**

Configure your Android application with following setting in the build.gradle (app level).

+ Minimum SDK version: 21
+ Compile SDK version: 30
+ Target SDK version: 30

### **3. Setup HotMic SDK**

1. Add the Maven repository in the build.gradle (project level).
```
maven {
   url "https://hotmic-android-803561570732.d.codeartifact.us-west-2.amazonaws.com/maven/HotMicAndroidSDK/"
   credentials {
       username "aws"
       password '<access-token>'
   }
}
```
This access-tocken is dynamic with an expiry of 12 hours. Everytime the SDK version changes, a new access-tocket will be generated. Instructions for generating the tocken will be shared by the HotMic team.

2. Add the HotMic SDK dependency in the build.gradle (app level).
```
implementation 'io.hotmic.player:hotmic-android-sdk:<version>'
```

### **4. Integrate HotMic SDK**

This section introduces the APIs for HotMic functional features.

The player screen in the SDK is implemented as a **fragment**.

+ If you have a **single activity**, then the player fragment can be launched in the desired (ViewGroup) layout container. 
+ If you have **separate acitvities**, then load the player fragmet in the desired activity itself.

#### **Fetch Streams**

To fetch all streams, use following code snippet with unique API Key:

```
HotMicPlayer.getStreams(context, API_KEY)?.let { api ->
    api
	.subscribeOn(Schedulers.io())
	.subscribe({ sList ->
		Log.d(“List_Streams”, "Streams fetched: ${sList.size}")
		//Update UI

        },{ e ->
		Log.e("Error in fetching streams", e)
        })
}
```

#### **Start the Player**

To start the player, use the following code snippet with passing the specific stream data:

```
HotMicPlayer.Builder(this)
	.setStreamId(streamId)
	.setUICallback(playerUiHandler)
	.credential(API_KEY)
	.setAnalyticHandler(analytics)
	.show(R.id.player_fragment_container)
```


For more details, please see the HotMic Player SDK Guide.