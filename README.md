## Description
This project was carried out for messaging purposes using the mqtt protocol.

## Sample
![app11](https://user-images.githubusercontent.com/48598966/138068101-d02b43d3-9098-4bc3-852e-2c749584d34e.png)
You can connect to the broker with user and password authentication or anonymously. The image shows an example of connection with user and password authentication. Mosquitto broker is currently not working. If you use the mosquitto switch, you will not be able to connect. If you want, you can fix the code and add your own broker here.

![example](https://user-images.githubusercontent.com/48598966/138402340-f5c1effa-a56e-42df-aa68-7cef62cb9535.PNG)

Firebase anonymous authentication takes place when you successfully connect to your broker. Your unique authentication id and other information is saved in the Firestore database. If you do not log out, your information will be remembered. If you log out, your anonymous identity and connection information will be deleted.

![app55](https://user-images.githubusercontent.com/48598966/138079766-86108307-51e4-45b0-b74d-c3fc9b3c3f18.png)
Publish and subscribe example can be seen in the pictures. You can choose qos(Quality of Service) when publishing and subscribing. You can publish the message as retain (Remember, each topic can only have one retained message). 

If you want to delete your information and session, you can exit from the menu at the top or use the back button of your device.
## Sources

Eclipse Paho Android Service

Online Websocket Client http://www.hivemq.com/demos/websocket-client/

Firebase Anonymous Authentication https://firebase.google.com/docs/auth/android/anonymous-auth

Firabase Firestore https://firebase.google.com/docs/firestore

Icon made by Freepik from www.flaticon.com

## Contact
Mehmet Emre Kobak

Mail: kobakmehmetemre@gmail.com
