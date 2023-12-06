# Firebase - Android Lab Session 4
## Question 1 - This while loop is missing the first contact, could you explain why? After fixing this, if you press again the button in your app, is something strange happening? If yes, explain how to fix it.
To solve this we had to slightly change the while loop, by converting it into a do-while loop. This way the first contact is also added to the list.
```java
//this is used in order to not have duplicated contacts everytime we sync the contacts
myRef.setValue(null);
names.moveToFirst();
    do{
        myContact.setName(names.getString(indexName));
        myContact.setPhoneNumber(names.getString(indexNumber));

        if (!names.isNull(indexEmail)) {
            myContact.setEmail(names.getString(indexEmail));
        } else {
            myContact.setEmail(""); // or handle null case as per your requirement
        }

        String key = myRef.push().getKey();
        myRef.child(key).setValue(myContact);
    }
    while(names.moveToNext());
```
## Question 2 - Make contacts alphabetically sorted in your ListView.
To do so, we need to add a sort function client side, in the listener that responds to changes in the firebase realtime database:
```java
final DatabaseReference myRef = database.getReference("Contacts");
//....
// Other code....
myRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        contactsArray.clear();
        adapter.clear();

        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            myContact readContact = snapshot.getValue(myContact.class);
            readContact.setKey(snapshot.getKey());
            contactsArray.add(readContact);

            //Sort contacts alphabetically
            contactsArray.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

            adapter.notifyDataSetChanged();
        }
    }
});
//Other code....
```
## Question 3 - Add intents to the contact list to make a call
To do so, we first need to declare the needed permission in the manifest file:
```xml
<uses-permission android:name="android.permission.CALL_PHONE"/>
```
Then we need to add a button to fire the intent in the list view item layout by using an onClickListenr() that we set inside the getView() function of our myContactAdapter class:
```java
public View getView(int position, View convertView, ViewGroup parent){
    //..Other code...
    final Button call = (Button) convertView.findViewById(R.id.call);
        call.setOnClickListener(view -> {

            //CALL_CONTACTS permission needs to be granted at runtime
            if (ActivityCompat.checkSelfPermission(parent.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) parent.getContext(), new String[]{Manifest.permission.CALL_PHONE}, 1000);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactPerson.getPhoneNumber()));
            parent.getContext().startActivity(intent);
        });
    //..Other code...
}
```

## Question 4 - Add a button to delete all contacts
To do so, we need to add a button in the main activity layout, and then add a listener in the onCreate() to it of our **MainActivity**  in order to delete all the contacts in the database:
```java
final DatabaseReference myRef = database.getReference("Contacts");
//..Other code...
final Button deleteButton = (Button) findViewById(R.id.deleteButton);
    deleteButton.setOnClickListener(view -> {
        myRef.setValue(null);
    });
```
## Question 4.1 - Add a delete button on each row of the listview that will allow you to delete only one contact
To do so, we need to add a button in the list view item layout, and then add a listener to it that deletes the contact in the database. This must be done inside our **myContactAdapter class**, that therefore will now need also to have a reference to the database:
```java
public View getView(int position, View convertView, ViewGroup parent){
    final DatabaseReference myRef = database.getReference("Contacts");
    //..Other code...
    final Button delete = (Button) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(view -> {
            myRef.child(contactPerson.getKey()).removeValue();
        });
    //..Other code...
}
```
## Question 5 - Design and code another way to add a new contact to Firebase from the app
We used a dialog to add a new contact, by adding a button in the main activity layout, and then adding a listener to it inside our **MainActivity** onCreate(), in order to show the dialog:

**dialog.xml**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/name"
        android:textColor="@color/black"
        android:hint="Name" >

    </EditText>

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/phone"
        android:textColor="@color/black"
        android:hint="Phone" >

    </EditText>

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/submit"
        android:text="Add contact">

    </Button>

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/close"
        android:text="Close">

    </Button>
</LinearLayout>
```
```java
final Button addButton = (Button) findViewById(R.id.addButton);
Dialog dialog = new Dialog(MainActivity.this);
addButton.setOnClickListener(view ->{
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button submit = dialog.findViewById(R.id.submit);
        Button close = dialog.findViewById(R.id.close);

        submit.setOnClickListener(v ->{
            String name = String.valueOf(((EditText) dialog.findViewById(R.id.name)).getText());
            String phone = String.valueOf(((EditText) dialog.findViewById(R.id.phone)).getText());
            myContact contact = new myContact();
            contact.setName(name);
            contact.setPhoneNumber(phone);
            //Push a new entry generated by the push() method by the db
            String key = myRef.push().getKey();
            //Set the value to the new contact, using the key generated by the push() method
            myRef.child(key).setValue(contact);
            dialog.dismiss();
        });

        close.setOnClickListener(v ->{
            dialog.dismiss();
            });
        dialog.show();

});
```
## Question 6 - What are the advantages of Firebase and what is the business model behind it? (POV of the user and the developer)
**Advantages for the user:**
- Real-time Updates: Firebase provides real-time synchronization across devices, allowing users to experience instant updates and changes without needing to refresh the application.
- Scalability: Scaling requirements of applications can be easily handled, as the user base grows
- Authentication: Users can sign in using various authentication providers like Google, Facebook, Twitter, and others, allowing the convenient to conveniently access the application without creating new credentials.
- Offline Support: Firebase includes offline data support, enabling users to access certain features of the application even when they are not connected to the internet.
- Push Notifications: Firebase Cloud Messaging (FCM) allows developers to send push notifications to users, keeping them engaged and informed about updates or relevant information.
  
**Advantages for the developer:**
- Easy Development: Firebase offers a set of tools and services that simplify the development process, like authentication, real-time database, cloud functions.
- Real-time Database: Firebase's NoSQL real-time database allows developers to build responsive applications by synchronizing data in real time across clients, without much effort.
- Serverless Functions: Firebase provides serverless functions (Firebase Cloud Functions) that allow developers to run custom backend code without having to manage servers
- Machine Learning: Firebase integrates with Google Cloud's machine learning services. Thise allows developers to add AI to their applications without being ML experts.
- Analytics and Performance Monitoring: Firebase provides tools for monitoring application usage, user engagement, and performance, helping developers make data-driven decisions for making their applications better.

**Firebase business model:**
Firebase has a freemium business model, it offers a free tier with limited resources and paid plans for higher usage. The pricing is based on usage of individual services, such as Realtime Database, Authentication, Cloud Functions, and Storage. The paid plans offer higher quotas, additional features, and priority support.

The business model is designed to cater to both small developers and large enterprises, allowing them to scale their usage and costs based on their application's growth.

## Question 7 - Why Firebase is a relevant for mobile apps? Name 5 top benefits of using Firebase compared to other approachres?
Firebase is a popular mobile and web application development platform provided by Google. It offers a wide range of services and features that make it relevant and beneficial for mobile app development. Here are five top benefits of using Firebase compared to other approaches:

- Real-time Database:
        Firebase provides database that allows developers to store and sync data in real-time across all connected clients. This is particularly useful for mobile apps that require live updates and collaborative features. The real-time database simplifies data synchronization and ensures that users see the most up-to-date information.

- Authentication and Authorization:
        Firebase offers robust authentication services, supporting various authentication providers like email/password, social media logins, and more. 

- Cloud Functions:
        Firebase Cloud Functions enable developers to run server-side code in response to events triggered by Firebase features or HTTPS requests. This serverless computing approach allows for easy scalability and the execution of backend logic without managing servers.

- Hosting and CDN Integration:
        Firebase Hosting provides a reliable and scalable solution for hosting web assets, including static content, dynamic content, and microservices.

- Cloud Firestore:
        Firebase offers Cloud Firestore, a flexible and scalable NoSQL document database that complements the real-time database. Cloud Firestore supports complex queries, offline data access, and automatic scaling, making it suitable for building feature-rich mobile apps. It allows developers to structure data in collections and documents, providing a powerful and user-friendly data model.

## Question 8 - Identify 5 applications that could benefit from Firebase in particular for multi-device users?
- Collaborative Productivity Apps:
    Applications that involve real-time collaboration, such as document editing, project management, or collaborative brainstorming tools, can benefit from Firebase. The real-time database and synchronization capabilities guarantess that changes made by one user are immediately reflected across all devices, this allow for smooth collaboration between users.

- Messaging and Chat Apps:
    Firebase's real-time features make it an excellent choice for messaging and chat applications. Users can send and receive messages in real-time, and the application can synchronize chat history across multiple devices. Additionally, Firebase Cloud Functions can be used to implement features like push notifications, ensuring that users are notified of new messages even when using a different device.

- E-commerce Platforms:
    For e-commerce apps, Firebase can enhance the user experience by providing real-time updates on inventory, order status, and pricing. Users browsing products on one device can see changes in stock levels or make purchases, and these updates are instantly reflected on other devices.

- Gaming Apps:
    Multiplayer gaming applications benefit from Firebase's real-time database and Cloud Functions. Game state changes, player actions, and updates can be synchronized across devices in real time. Cloud Firestore can be used to store and manage game data.

- Educational Apps:
    Educational applications, including e-learning platforms, language learning apps, or collaborative study tools, can exploit Firebase for enhanced multi-device user experiences. Real-time synchronization ensures that collaborative study sessions, shared notes, or progress tracking are smoothly updated across devices.

