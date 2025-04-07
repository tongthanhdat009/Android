package com.project.myapplication.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;

import java.util.Objects;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private final ChatBox chatBox;
    private final String userName;
    private final String userID;

    public BottomSheetFragment(ChatBox chatBox, String userName, String userID) {
        this.chatBox = chatBox;
        this.userName = userName;
        this.userID = userID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetStyle);
        View view = inflater.inflate(R.layout.bottomsheet, container, false);
        TextView textView = view.findViewById(R.id.name);
        textView.setText(userName);

        view.findViewById(R.id.deleteButton).setOnClickListener(v -> {
            ChatBoxModel chatBoxModel = new ChatBoxModel();
            // Kiểm tra nếu là ChatBox AI thì xóa luôn
            if (chatBoxModel.isAI(chatBox)) {
                chatBoxModel.deleteChatBox(chatBox.getId());
                Toast.makeText(getContext(), "Đã xóa "+ userName, Toast.LENGTH_SHORT).show();
            } else {
                // Nếu không phải ChatBox AI, ẩn nó cho người dùng hiện tại
                chatBoxModel.hideChatBox(chatBox.getId(), userID);
                Toast.makeText(getContext(), "Đã xóa hộp thoại " + userName, Toast.LENGTH_SHORT).show();
            }
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
