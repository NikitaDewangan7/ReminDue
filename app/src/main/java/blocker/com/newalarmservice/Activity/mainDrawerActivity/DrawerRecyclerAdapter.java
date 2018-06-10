package blocker.com.newalarmservice.Activity.mainDrawerActivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import blocker.com.newalarmservice.R;


public class DrawerRecyclerAdapter extends RecyclerView.Adapter<DrawerRecyclerAdapter.MyViewHolder> {
    private Context context;
    private static final int HOME_ITEM = 1,NO_HEADER_ITEM =2, HEADER_SETTING = 3, SETTING_ITEM = 4;
    private int status = 1;
    private OnDrawerItemClickListener drawerItemClickListener;
    private HashMap<Integer, MenuDrawerItemPojo> drawerItems;

    public void setOnDrawerItemClickListener(OnDrawerItemClickListener listener) {
        drawerItemClickListener = listener;
    }

    public DrawerRecyclerAdapter(Context context, HashMap<Integer, MenuDrawerItemPojo> pojo) {
        this.drawerItems = pojo;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        MyViewHolder holder = null;
        switch (viewType) {
            case HOME_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.home_drawer_item, null);
                holder = new MyViewHolder(view, HOME_ITEM);
                break;
            case HEADER_SETTING:
                view = LayoutInflater.from(context).inflate(R.layout.drawer_header, null);
                holder = new MyViewHolder(view, HEADER_SETTING);
                break;
            case NO_HEADER_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.draweritem, null);
                holder = new MyViewHolder(view, NO_HEADER_ITEM);
                break;
            case SETTING_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.draweritem2, null);
                holder = new MyViewHolder(view, SETTING_ITEM);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (drawerItems.containsKey(position)) {
            if (drawerItems.get(position).isClickedstatus()) {
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.material_grey_300));
            } else
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.white));
            if (drawerItems.get(position).getPosition() == position) {
                if (holder.itemImg != null) {
                    holder.itemImg.setImageResource(drawerItems.get(position).getImgId());
                }
                if (holder.tvItemName != null) {
                    holder.tvItemName.setText(drawerItems.get(position).getName());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvColorChange, tvItemName, tvHeader;
        private ImageView itemImg;
        private View view;
        public MyViewHolder(View itemView, int type) {
            super(itemView);
            view = itemView;
            itemView.setOnClickListener(this);
            if (type == HEADER_SETTING) {
                tvHeader = (TextView) itemView.findViewById(R.id.tvHeader);
                tvHeader.setText("Settings");
            }
            if (type == SETTING_ITEM) {
                itemImg = (ImageView) itemView.findViewById(R.id.imgItem);
                tvItemName = (TextView) itemView.findViewById(R.id.tvHeaderItem);
            }
            if (type == NO_HEADER_ITEM) {
                tvItemName = (TextView) itemView.findViewById(R.id.tvHeaderItem);
                tvColorChange = (TextView) itemView.findViewById(R.id.tvColorChange);
                itemImg = (ImageView) itemView.findViewById(R.id.imgItem);
                switch (status) {
                    case 1:
                        tvColorChange.setBackgroundColor(context.getResources().getColor(R.color.themelightblue));
                        status++;
                        break;
                    case 2:
                        tvColorChange.setBackgroundColor(context.getResources().getColor(R.color.themepink));
                        status++;
                        break;
                    case 3:
                        tvColorChange.setBackgroundColor(context.getResources().getColor(R.color.themedarkgreen));
                        status++;
                        break;
                    case 4:
                        tvColorChange.setBackgroundColor(context.getResources().getColor(R.color.themeorange));
                        status++;
                        break;
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (drawerItemClickListener != null)
                drawerItemClickListener.onDrawerClick(v, getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position ==0)
          return HOME_ITEM;
        if (position>0&&position<5)
            return NO_HEADER_ITEM;
        if (position==5)
            return HEADER_SETTING;
        if (position>5)
            return SETTING_ITEM;
        return -1;
    }

    public interface OnDrawerItemClickListener {
        public void onDrawerClick(View view, int position);
    }
}
