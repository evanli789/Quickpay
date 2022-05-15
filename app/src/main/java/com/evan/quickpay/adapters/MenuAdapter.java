package com.evan.quickpay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.evan.quickpay.data.MenuItem;
import com.evan.quickpay.R;
import com.evan.quickpay.util.SpacingUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private int CARD_PREVIEW_CORNERS_DP;

    public interface MenuAdapterCallback {
        void onMenuItemClick(int position);
        void onMenuItemLongClick(int position);
    }

    private final List<MenuItem> list;
    private final MenuAdapterCallback callback;

    public MenuAdapter(List<MenuItem> list, MenuAdapterCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        CARD_PREVIEW_CORNERS_DP = SpacingUtils.convertIntToDP(recyclerView.getContext(), 16);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_menu_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.tvName.setText(list.get(i).getName());
        holder.tvDescription.setText(list.get(i).getDescription());
        holder.tvPrice.setText(list.get(i).getFormattedPrice());

        Glide.with(holder.itemView.getContext())
                .load(list.get(i).getPicturePath())
                .transform(new CenterCrop(),
                        new GranularRoundedCorners(
                                CARD_PREVIEW_CORNERS_DP,
                                CARD_PREVIEW_CORNERS_DP,
                                0,
                                0))
                .into(holder.ivItem);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.iv_item_pic)
        ImageView ivItem;

        @BindView(R.id.tv_item_name)
        TextView tvName;

        @BindView(R.id.tv_item_description)
        TextView tvDescription;

        @BindView(R.id.tv_price)
        TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.onMenuItemClick(getAbsoluteAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            callback.onMenuItemLongClick(getAbsoluteAdapterPosition());
            return true;
        }
    }
}
