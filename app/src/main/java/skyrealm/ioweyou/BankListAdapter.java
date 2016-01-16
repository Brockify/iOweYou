package skyrealm.ioweyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by RockyFish on 1/15/16.
 */
public class BankListAdapter extends BaseAdapter
{
    ArrayList<ArrayList<String>> userAccounts;
    LayoutInflater inflater;
    public BankListAdapter(Context context, ArrayList<ArrayList<String>> userAccounts)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userAccounts = new ArrayList<ArrayList<String>>();
        this.userAccounts = userAccounts;

    }
    @Override
    public int getCount() {
        return this.userAccounts.size();
    }

    @Override
    public Object getItem(int position) {
        return this.userAccounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView accountNameTextView, balance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.bank_account_list_item, null);
        Holder holder = new Holder();
        holder.accountNameTextView = (TextView) rowView.findViewById(R.id.nameTextView);
        holder.balance = (TextView) rowView.findViewById(R.id.balanceTextView);


        holder.balance.setText(userAccounts.get(position).get(0));
        holder.accountNameTextView.setText(userAccounts.get(position).get(1));


        return rowView;
    }
}
