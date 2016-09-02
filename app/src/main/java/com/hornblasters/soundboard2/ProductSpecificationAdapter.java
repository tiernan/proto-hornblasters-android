package com.hornblasters.soundboard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class ProductSpecificationAdapter extends ArrayAdapter<ProductSpecificationsParser.Specification> {
    private final LayoutInflater vi;
    private final int[] positionType;

    public ProductSpecificationAdapter(Context context, List<ProductSpecificationsParser.Specification> items) {
        super(context, R.layout.view_list_specification, items);
        vi = LayoutInflater.from(getContext());

        positionType = new int[items.size()];
        int i = 0;
        for (ProductSpecificationsParser.Specification spec : items) {
            if (spec.definition == null) {
                positionType[i] = 0;
            } else {
                positionType[i] = 1;
            }
            i++;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return positionType[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int p, View v, ViewGroup vg) {
        if (getItemViewType(p) == 0) {
            if (v == null) {
                v = vi.inflate(R.layout.view_list_specification_header, vg, false);
            }

            ProductSpecificationsParser.Specification i = getItem(p);
            TextView heading = (TextView) v.findViewById(R.id.heading);
            heading.setText(i.term);

        } else {
            if (v == null) {
                v = vi.inflate(R.layout.view_list_specification, vg, false);
            }

            ProductSpecificationsParser.Specification i = getItem(p);

            TextView term = (TextView) v.findViewById(R.id.term);
            term.setText(i.term);

            TextView definition = (TextView) v.findViewById(R.id.definition);
            definition.setText(i.definition);
        }

        return v;
    }
}