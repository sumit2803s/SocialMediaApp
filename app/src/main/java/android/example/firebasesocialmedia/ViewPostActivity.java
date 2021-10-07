package android.example.firebasesocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewPostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView postListView;
    private ArrayList<String> usernames;
    private ArrayAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private ImageView sentpostsImageView;
    private TextView txtDescription;
    private ArrayList<DataSnapshot> dataSnapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        sentpostsImageView=findViewById(R.id.sentpostimageview);
        txtDescription=findViewById(R.id.edtdescription);

        firebaseAuth=FirebaseAuth.getInstance();

        postListView=findViewById(R.id.postslistview);
        usernames=new ArrayList<>();
        adapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,usernames);
        postListView.setAdapter(adapter);
        dataSnapshots=new ArrayList<>();
        postListView.setOnItemClickListener(this);
        postListView.setOnItemLongClickListener(this);

        FirebaseDatabase.getInstance().getReference().child("my_users").child(firebaseAuth.getCurrentUser().getUid()).child("received_posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                dataSnapshots.add(snapshot);
                String fromWhomUsername=(String) snapshot.child("fromWhom").getValue();
                usernames.add(fromWhomUsername);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                int i=0;
                for(DataSnapshot snapshot1:dataSnapshots) {
            if(snapshot1.getKey().equals(snapshot.getKey())) {
                dataSnapshots.remove(i);
                usernames.remove(i);
            }
            i++;
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       DataSnapshot myDataSnapshot=dataSnapshots.get(position);
       String downLoadLink=(String) myDataSnapshot.child("imageLink").getValue();
        Picasso.get().load(downLoadLink).into(sentpostsImageView);
        txtDescription.setText((String) myDataSnapshot.child("des").getValue());

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            builder=new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        } else {
            builder=new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete entry").setMessage("Are Your Sure You Want to delete this Entry").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseStorage.getInstance().getReference().child("my_users").child((String) dataSnapshots.get(position).child("imageIdentifier").getValue()).delete();

                FirebaseDatabase.getInstance().getReference()
                        .child("my_users").child(firebaseAuth.getCurrentUser()
                .getUid()).child("received_posts")
                        .child(dataSnapshots.get(position).getKey()).removeValue();


            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        return false;
    }
}