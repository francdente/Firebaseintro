package fr.eurecom.firebaseintro;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class myContactAdapter extends ArrayAdapter<myContact> {

    private DatabaseReference myRef;

    public myContactAdapter(@NonNull Context context, ArrayList<myContact> contacts, DatabaseReference myRef) {
        super(context,0, contacts);
        this.myRef = myRef;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        myContact contactPerson = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_content, parent, false);
        }

        TextView txtName = convertView.findViewById(R.id.name);
        TextView txtPhone = convertView.findViewById(R.id.phone);

        final Button delete = (Button) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(view -> {
            myRef.child(contactPerson.getKey()).removeValue();
        });

        final Button call = (Button) convertView.findViewById(R.id.call);
        call.setOnClickListener(view -> {

            if (ActivityCompat.checkSelfPermission(parent.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) parent.getContext(), new String[]{Manifest.permission.CALL_PHONE}, 1000);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactPerson.getPhoneNumber()));
            parent.getContext().startActivity(intent);
        });

        txtName.setText(contactPerson.getName());
        txtPhone.setText(contactPerson.getPhoneNumber());

        return convertView;
    }
}
