package com.project.myapplication.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.myapplication.R;

import java.util.Objects;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SaveBottomSheetDialogStyle);
        View view = inflater.inflate(R.layout.bottomsheet, container, false);
        view.findViewById(R.id.deleteButton).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Button in BottomSheet clicked", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            Objects.requireNonNull(dialog.getWindow()).setDimAmount(0.5f);
        }
    }
}