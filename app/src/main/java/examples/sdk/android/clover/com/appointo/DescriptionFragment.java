package examples.sdk.android.clover.com.appointo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DescriptionFragment extends DialogFragment {

  DialogFragmentListener dialogFragmentListener;
  Button yes;
  TextView description;
  String des;

  public DescriptionFragment() {
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.dialogFragmentListener = (DialogFragmentListener) context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }


  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog != null) {
      dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_description, container, false);
    description = v.findViewById(R.id.descTitle);
    yes = v.findViewById(R.id.closeButton);

    des = getArguments().getString("num");
    description.setText(des);

    yes.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    return v;
  }
}

