package pnpmsjm.com.ourmasjid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private Context context;
    private List<Members> membersList;

    public MembersAdapter(Context context, List<Members> memberList) {
        this.context = context;
        this.membersList = memberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_members, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Members member = membersList.get(position);
        holder.tvName.setText(member.getName());
        holder.tvMobile.setText(member.getMobile_No());
        holder.tvDesignation.setText(member.getDesignation());
        holder.imgPhoto.setVisibility(View.GONE);

        holder.tvName.setOnClickListener(v -> {
            if (holder.tvDesignation.getVisibility() == View.GONE) {
                holder.tvDesignation.setVisibility(View.VISIBLE);
            } else {
                holder.tvDesignation.setVisibility(View.GONE);
            }
        });

        if (member.getPurl() != null && !member.getPurl().isEmpty()) {
            Picasso.get().load(member.getPurl()).into(holder.imgPhoto);
            holder.imgPhoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesignation, tvMobile;
        ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }
}
