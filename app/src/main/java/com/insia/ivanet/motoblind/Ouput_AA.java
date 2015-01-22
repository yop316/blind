package com.insia.ivanet.motoblind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * class to display the database in a object list
 * @author JJ Anaya
 *
 */
public class Ouput_AA extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> values;

    public Ouput_AA(Context context, ArrayList<String> values) {
        super(context, R.layout.message_adapter, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.message_adapter, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        // change the icon for Windows and iPhone
        String s = values.get(position);
        textView.setText(s);
        String[] ss = s.split("\n");
        if (ss.length>=1)
            if(ss[0].split(";").length==10)
                imageView.setImageResource(R.drawable.database);
        return rowView;
    }

    public void add_element (String message){
        values.add(message);
        notifyDataSetChanged();
    }

    public void remove_extra_elements(){
        int maxnumelements = 5;
        if(values.size()>maxnumelements){
            while (true){
                values.remove(0);
                if(values.isEmpty() || values.size()<=maxnumelements/2.0)
                    break;
            }
        }
        notifyDataSetChanged();
    }
}

