package pnpmsjm.com.bd.ourmasjid_1;

public class Firestore {

}
FirebaseFirestore db = FirebaseFirestore.getInstance();
List<Chapter> chapterList = new ArrayList<>();
ChapterAdapter adapter = new ChapterAdapter(chapterList);

RecyclerView recyclerView = findViewById(R.id.recyclerView);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

db.collection("chapters")
    .addSnapshotListener(new EventListener<QuerySnapshot>() {
    @Override
    public void onEvent(@Nullable QuerySnapshot value,
            @Nullable FirebaseFirestoreException error) {
        if (error != null) {
            Log.w("Firestore", "Listen failed.", error);
            return;
        }

        chapterList.clear();
        for (QueryDocumentSnapshot doc : value) {
            Chapter chapter = doc.toObject(Chapter.class);
            chapterList.add(chapter);
        }
        adapter.notifyDataSetChanged();
    }
});