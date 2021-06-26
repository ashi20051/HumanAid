# HumanAid
As the crime against women is increasing tremendously. According to NCRB, the crime against women is committed every 3 minutes. Safety Apps are proven to be a life-saving piece of technology, any human can get hold of at this juncture of time. These Apps can provide an invisible protective shield to the user of the app.
Our App HumanAid can provide such an invisible protective shield to the user. It will help people to tackle emergencies and will monitor mainly women and child safety. The basic idea is to build a user-friendly and robust application that can help individuals by sending SOS messages along with the current location and the app can be triggered with minimal effort. 

# Workflow diagram of the App can be visualized as:
![workflow](https://user-images.githubusercontent.com/48834012/123510532-cc3fb680-d699-11eb-8123-a26380c8ef19.png)

# Features of HumanAid :
**1. Phone Verification:** Whenever the user first installs the app, there comes an OTP authentication page that asks for the phone number and then generates an OTP for verification.<br />
**2. Add Emergency Contacts:** The user can add his/her preferred contacts with the customized message. Contacts are added and displayed in the form of a list using RecyclerView.<br />
**3. Modify & Delete Contacts:** This feature enables the user to update and delete the saved contacts along with their details and customized messages, respectively. Details of a contact are stored in Room Database, which can be accessed, modified and deleted using DBMS queries.<br />
**4. Quick Helpline Dialer Support:** The user can make a call via dialer by clicking on the particular helpline numbers in the Emergency tab. Emergency helplines can be quick dialed on clicking, this is implemented using ACTION_DIAL intent.<br />
**5. 3 Time Shake + Power Press To alert:** In the App, we have the feature of having the 3 times shake along with the power button press to trigger the SMS with a customized message and the link of the user’s current location. <br />
**6. 3 times Power Press To alert:** The user can tap the power button 3 times or more consecutively, which will lead to the triggering of the SMS to the chosen emergency contacts. This feature is also achieved using broadcast receiver and appropriate intent filters.<br />
**7. Guardian Shield:** This feature will define the probable visiting area radius exiting, which will lead to triggering the SMS to the emergency contacts.<br />
**8. Duration:** This feature will define the preset duration of visit, exceeding which will lead to triggering the SMS to the user-selected contacts.<br />

