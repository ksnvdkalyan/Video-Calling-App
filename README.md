# Connect
The connect is a video calling app made on jitsi sdk. Users will sign up for their account using some basic details like the first name, last name, email, and password. To store all of the user data we will be using the cloud fire store database, which is a flexible and scalable database for mobile, web, and server development from Firebase and Google Cloud Platform.

After sign up, the user will sign in to their account using email and password. We will store logged user information into shared preferences to handle auto-sign in so users don't need to enter email and password every time.

After sign in, we will display a list of other users that are signed up in our application except for the currently logged user because nobody is going to start a video meeting with himself.

In a video meeting, the user will initiate the video meeting by sending a meeting invitation to another user. To send a meeting invitation we will use firebase cloud messaging which is a cross-platform messaging solution that lets you reliably send messages at no cost. Using FCM, you can notify a client app that a new email or other data is available to sync.

Once the meeting invitation sent, the receiver has two options, accept or reject the invitation. On acceptance or rejection, the related response message will be sent to the meeting initiator or sender. If the user wants to cancel the meeting invitation then it can be done by the hang-up process, in which another remote message will be sent to the receiver to cancel the current meeting invitation. This is the complete meeting invitation process.

On acceptance of the meeting invitation, the video meeting will start using the Jitsi Meet. To implement video meetings or conferences lots of groundwork is needed, to simplify this process we will use a pre-built Jitsi Meet client which is a free, open-source project that provides web browsers and mobile applications with real-time communication (RTC) via simple application programming interfaces (APIs). It allows audio and video communication to work inside applications and web pages by allowing direct peer-to-peer communication, eliminating the need to install plugins or download native apps. This Jitsi Meet client provides the facilities like, switch sound devices, invite more people to the meeting, audio-only mode, toggle camera, tile view in case of more people. The best part is you can also start chat conversion during video meetings.

The app has additional features like adding new meetings and get notified at the time of meeting, get to know locations of the users who are using the app.

These are some images of the app.

![splashScreen](https://user-images.githubusercontent.com/66007960/115340098-bc03ea80-a1c3-11eb-859d-e6793ea9d384.jpeg)
![intro1](https://user-images.githubusercontent.com/66007960/115340143-cf16ba80-a1c3-11eb-9536-68260f0ca72f.jpeg)
![intro2](https://user-images.githubusercontent.com/66007960/115340170-d938b900-a1c3-11eb-81a9-b7d803e7e4f8.jpeg)
![intro3](https://user-images.githubusercontent.com/66007960/115340200-e3f34e00-a1c3-11eb-8e5a-13ab9e49ff91.jpeg)
![login](https://user-images.githubusercontent.com/66007960/115340237-ef467980-a1c3-11eb-879b-35f9fc0de2d4.jpeg)
![register](https://user-images.githubusercontent.com/66007960/115340279-fec5c280-a1c3-11eb-89d8-ef06e182716d.jpeg)
![Home](https://user-images.githubusercontent.com/66007960/115340333-1604b000-a1c4-11eb-8394-6d688f138fa0.jpeg)
![Meetings](https://user-images.githubusercontent.com/66007960/115340357-2026ae80-a1c4-11eb-8a53-81c1ce254577.jpeg)
![Locations](https://user-images.githubusercontent.com/66007960/115340369-287ee980-a1c4-11eb-808f-22f927ac9398.jpeg)
![info](https://user-images.githubusercontent.com/66007960/115340399-39c7f600-a1c4-11eb-8312-9ce5dfe31c40.jpeg)
