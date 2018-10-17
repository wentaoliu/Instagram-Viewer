package comshaoqingliu.httpsgithub.Instagram;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import comshaoqingliu.httpsgithub.helloworld.R;

public class ProfileGridViewAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private String[] imageUrls;

    //constructor
    public ProfileGridViewAdapter(Context context, String[] imageUrls){
        super(context, R.layout.profile_grid_item, imageUrls);

        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.imageUrls = imageUrls;
    }


    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        public ImageView imageView;
//        public TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.profile_grid_item,null);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.iv_grid);
//            holder.textView = convertView.findViewById(R.id.iv_title);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        /*assign value for each grid item!*/
//        holder.textView.setText("watermelon");
        Glide.with(context).load(imageUrls[position]).into(holder.imageView);
//        Log.d("getView", "position: "+position);
        return convertView;
    }
}
