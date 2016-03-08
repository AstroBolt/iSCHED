package mobcom.iacademy.thesis.utilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.NavItem;


public class NavListAdapter extends ArrayAdapter<NavItem> {
    Context context;
    int resLayout;
    List<NavItem> listNavItems;

    public NavListAdapter(Context context, int resLayout, List<NavItem> listNavItems) {
        super(context, resLayout, listNavItems);

        this.context = context;
        this.resLayout = resLayout;
        this.listNavItems = listNavItems;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, resLayout, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        ImageView navIcon = (ImageView) view.findViewById(R.id.nav_icon);

        NavItem navItem = listNavItems.get(position);
        tvTitle.setText(navItem.getTitle());
        navIcon.setImageResource(navItem.getResIcon());
        return view;
    }
}
