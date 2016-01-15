package skyrealm.ioweyou;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Cat on 1/1/2016.
 */
public class SettingsListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<String> settings;
    public SettingsListAdapter(Context context)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settings = new ArrayList<String>();
        settings.add("Reset Password");
        settings.add("Reset Pin");
    }
    @Override
    public int getCount() {
        return settings.size();
    }

    @Override
    public Object getItem(int position) {
        return settings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder
    {
        TextView settingTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.setting_list_item, null);
        Holder holder = new Holder();
        holder.settingTextView = (TextView) rowView.findViewById(R.id.settingTextView);
        holder.settingTextView.setText(settings.get(position));

        return rowView;
    }
}
