package pnpmsjm.com.bd.ourmasjid_1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Comittee extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private List<Member> memberList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comittee); // activity_main.xml ফাইলে recyclerView থাকবে

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        memberList = new ArrayList<>();
        adapter = new Adapter(memberList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("comiti")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("Firestore", "Listen failed.", error);
                            return;
                        }

                        memberList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Member member = doc.toObject(Member.class);
                            memberList.add(member);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}