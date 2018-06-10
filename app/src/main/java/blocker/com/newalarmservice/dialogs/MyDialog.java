package blocker.com.newalarmservice.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import blocker.com.newalarmservice.R;


public class MyDialog extends DialogFragment {
    private TextView btnOk;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_insert_dialog, null);
        Bundle bundle = getArguments();
        int status = bundle.getInt("status", -1);

        ImageView statusImg;

        TextView tvMsg;
        statusImg = (ImageView) view.findViewById(R.id.img_status);
        btnOk = (TextView) view.findViewById(R.id.imgPaid);
        tvMsg = (TextView) view.findViewById(R.id.tv_message);

        if (status != -1) {
            switch (status) {
                case 1:
                    statusImg.setImageResource(R.mipmap.success);
                    tvMsg.setText("Due added successfully");
                    btnOk.setBackgroundColor(getResources().getColor(R.color.succeesbg));
                    registerOnClick(1);
                    break;
                case 0:
                    statusImg.setImageResource(R.mipmap.fail);
                    tvMsg.setText("Problem in adding due");
                    btnOk.setBackgroundColor(getResources().getColor(R.color.errorbg));
                    registerOnClick(0);
                    break;

            }
        }
        return view;
    }

    private void registerOnClick(final int status) {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case 0:
                        dismiss();
                        break;
                    case 1:
                        dismiss();
                        getActivity().finish();
                        break;
                }
            }
        });
    }
}
