package blocker.com.newalarmservice.Activity.mainDrawerActivity.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import blocker.com.newalarmservice.R;


public class AboutFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout rateApp, contactDeveloper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, null);
        rateApp = (RelativeLayout) view.findViewById(R.id.rateApp);
        contactDeveloper = (RelativeLayout) view.findViewById(R.id.contactDeveloper);
        rateApp.setOnClickListener(this);
        contactDeveloper.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rateApp:
                Log.e("package name", getContext().getPackageName());
                Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    getActivity().startActivity(goToMarket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.contactDeveloper:
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:abhishekdewangan.005@gmail.com")); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                getContext().startActivity(intent);
                break;
        }

    }
}
