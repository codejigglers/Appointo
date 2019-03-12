package examples.sdk.android.clover.com.appointo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ConfirmFragment extends DialogFragment {

  DialogFragmentListener dialogFragmentListener;
  Button yes;
  Button no;

  public ConfirmFragment() {
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
    View v = inflater.inflate(R.layout.fragment_my_custom_dialog, container, false);
    yes = v.findViewById(R.id.yesButton);
    no = v.findViewById(R.id.noButton);

    yes.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialogFragmentListener.onYesClick();
      }
    });

    no.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    return v;
  }
}
