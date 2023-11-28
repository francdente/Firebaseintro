package fr.eurecom.firebaseintro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1000);
            return;
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://fir-intro-a5c54-default-rtdb.firebaseio.com/");
        final DatabaseReference myRef = database.getReference("Contacts");

        final Button syncButton = (Button) findViewById(R.id.goButton);

        syncButton.setOnClickListener( view -> {
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            myContact myContact = new myContact();
            String[] projection = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            Cursor names = getContentResolver().query(uri, projection, null,null,null);
            int indexName = names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int indexNumber = names.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);


            //this is used in order to not have duplicated contacts everytime we sync the contacts
            myRef.setValue(null);

            names.moveToFirst();

            do{
                myContact.setName(names.getString(indexName));
                myContact.setPhoneNumber(names.getString(indexNumber));
                String key = myRef.push().getKey();
                myRef.child(key).setValue(myContact);
            }
            while(names.moveToNext());

        });

        final Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(view -> {
            myRef.setValue(null);
        });


        final ArrayList<myContact> contactsArray = new ArrayList<>();
        final myContactAdapter adapter = new myContactAdapter(this, contactsArray, myRef);
        final ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactsArray.clear();
                adapter.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    myContact readContact = snapshot.getValue(myContact.class);
                    readContact.setKey(snapshot.getKey());
                    contactsArray.add(readContact);


                    //QUESTION 1 to sort contact
                    contactsArray.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

                    Log.e("MainActivity", "Prova");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}