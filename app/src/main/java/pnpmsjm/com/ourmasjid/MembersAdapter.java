package pnpmsjm.com.ourmasjid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        // Name
        holder.tvName.setText(member.getName() != null ? member.getName() : "No Name");

        // Mobile
        holder.tvMobile.setText(member.getMobile_No() != null ? member.getMobile_No() : "No Number");

        // Designation
        holder.tvDesignation.setText(member.getDesignation() != null ? member.getDesignation() : "No Designation");

        if (member.getPurl() != null && !member.getPurl().isEmpty()) {
            Glide.with(holder.imgPhoto.getContext())
                    .load(member.getPurl())
                    .placeholder(R.drawable.default_img)
                    .error(R.drawable.error_img)
                    .into(holder.imgPhoto);
        } else {
            holder.imgPhoto.setImageResource(R.drawable.default_img);
        }

        // Call button
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = member.getMobile_No();
                if (phone != null && !phone.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesignation, tvMobile;
        ImageView imgPhoto;
        ImageButton btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}
