package com.googlemap;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by R-Tem on 07.09.2015.
 */
public class LocationDialog extends DialogFragment implements View.OnClickListener {

    private static final String LOG_TAG = "LocDial";

    private String mLocInfo;

    /**
     * Create a new instance of LocationDialog, providing "location info"
     * as an argument.
     */
    static LocationDialog newInstance(String _locInfo) {
        LocationDialog locDial = new LocationDialog();

        // Supply locInfo input as an argument.
        Bundle args = new Bundle();
        args.putString("locationInfo", _locInfo);
        locDial.setArguments(args);

        return locDial;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocInfo = getArguments().getString("locationInfo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("My location info:");
        View v = inflater.inflate(R.layout.dialog_loc_info, null);
        TextView tv = (TextView) v.findViewById(R.id.txtLocInfo_DFLI);
        tv.setText(mLocInfo);
        v.findViewById(R.id.btnOk_DFLI).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "LocDialog#onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "LocDialog#onCancel");
    }
}
