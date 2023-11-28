package fr.eurecom.firebaseintro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

        txtName.setText(contactPerson.getName());
        txtPhone.setText(contactPerson.getPhoneNumber());

        return convertView;
    }
}
