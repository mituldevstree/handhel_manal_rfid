package com.nada.tech.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewParent;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.textfield.TextInputLayout;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class EditText extends AppCompatEditText {
    public static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
    public static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
    public static final MediaType MEDIA_TYPE_VIDEO = MediaType.parse("video/*");
    public static final MediaType MEDIA_TYPE_PDF = MediaType.parse("application/pdf");

    public EditText(Context context) {
        super(context);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        try {
            TextInputLayout inputLayout = findTextInputLayoutParent(this.getParent(), 3);
            if (inputLayout != null) {
                (inputLayout).setError("");
                (inputLayout).setErrorEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static TextInputLayout findTextInputLayoutParent(ViewParent view, int maxDepth) {
        if (view == null) return null;
        ViewParent parent = view.getParent();
        if (parent == null) return null;
        if (parent instanceof TextInputLayout) {
            return (TextInputLayout) parent;
        } else if (maxDepth > 0) {
            return findTextInputLayoutParent(parent, maxDepth - 1);
        }
        return null;
    }

    public void clear() {
        setText("");
    }

    public String getString() {
        Editable editable = super.getText();
        if (editable == null) return "";
        return editable.toString();
    }

    public String trim() {
        Editable editable = super.getText();
        if (editable == null) return "";
        return editable.toString().trim();
    }

    public int length() {
        return getString().length();
    }

    public void setError(int res) {
        super.setError(getContext().getString(res));
    }

    public RequestBody getRequestBody() {
        return RequestBody.create(getString(), MEDIA_TYPE_TEXT);
    }

    public boolean isChanged(String name) {
        if (name == null) return false;
        return !trim().equals(name);
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(trim());
    }

//    public void setDrawableStart(String text) {
//        if (TextUtil.isNullOrEmpty(text)) {
//            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//        } else {
//            setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(getContext(), text), null, null, null);
//        }
//    }
}
