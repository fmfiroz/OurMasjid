package pnpmsjm.com.bd.ourmasjid_1;

public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.ChapterViewHolder> {
    private List<Chapter> chapterList;

    public RecyclerView_Adapter(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.tvTitle.setText(chapter.getTitle());
        holder.tvDescription.setText(chapter.getDescription());
        holder.tvDescription.setVisibility(View.GONE);

        holder.tvTitle.setOnClickListener(v -> {
            if (holder.tvDescription.getVisibility() == View.GONE) {
                holder.tvDescription.setVisibility(View.VISIBLE);
            } else {
                holder.tvDescription.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}

