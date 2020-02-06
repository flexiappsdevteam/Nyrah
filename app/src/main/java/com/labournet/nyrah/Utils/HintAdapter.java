package com.labournet.nyrah.Utils;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.labournet.nyrah.account.model.BusinessType;

import java.util.ArrayList;

public class HintAdapter extends ArrayAdapter<BusinessType> {

    public HintAdapter(Context context, int resource, ArrayList<BusinessType> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
