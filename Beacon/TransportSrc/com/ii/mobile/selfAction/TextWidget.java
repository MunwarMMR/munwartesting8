package com.ii.mobile.selfAction;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableRow;

import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;
import com.ii.mobile.instantMessage.TextValidator;

public class TextWidget extends BaseWidget {

	public Editable lastString;
	public String foobar = "no way!";
	public int numLines = 1;
	private EditText editText = null;
	private final boolean number;

	// public TextWidget(FragmentActivity fragmentActivity, Fields field) {
	// super(fragmentActivity, field);
	// }

	public TextWidget(FragmentActivity activity, Fields field, boolean number) {
		super(activity, field);
		this.number = number;
	}

	@Override
	public EditText createValueView() {
		editText = new EditText(activity);
		// L.out("editText: " + editText);
		editText.setLines(numLines);
		if (number)
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		TableRow.LayoutParams layoutParams = new
				TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT);

		// layoutParams.setMargins(0, 0, 10, 0);
		editText.setLayoutParams(layoutParams);
		// layoutParams = (LayoutParams) editText.getLayoutParams();
		// layoutParams.setMargins(20, 0, 40, 0);
		editText.setHint(field.name);
		editText.setTextSize(15);
		editText.setTextColor(Color.parseColor("#000000"));
		editText.setBackgroundResource(android.R.drawable.editbox_background);
		// editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		editText.setSingleLine(true);
		editText.setImeOptions(EditorInfo.IME_FLAG_NAVIGATE_NEXT);
		editText.addTextChangedListener(new TextValidator() {
		});
		// L.out("numLines: " + numLines);
		if (numLines > 1) {
			layoutParams.setMargins(0, 0, 10, 0);
			editText.setHint("Speak or Type Note");
			editText.setSingleLine(false);
			editText.setHorizontalScrollBarEnabled(false);
			editText.setVerticalScrollBarEnabled(true);
			editText.setHorizontallyScrolling(false);
			editText.setMinLines(numLines);
			editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			editText.setGravity(Gravity.TOP | Gravity.LEFT);

			// editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

			// editText.setInputType(InputType.TYPE_CLASS_TEXT |
			// InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			// editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		}

		onRestoreInstanceState();
		if (field.required)
			editText.setHintTextColor(Color.parseColor("#FFAAAA"));
		else
			editText.setHintTextColor(Color.parseColor("#AAAAAA"));
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// validate();
				String newText = editText.getText().toString();
				// L.out("lastString: " + lastString);
				// L.out("newText: " + newText);
				if (newText != null && newText.contains("\"")) {
					newText = newText.replace("\"", "");
					editText.setText(newText);
				}
				if (field.required) {
					if (!lastString.equals(newText))
						validateAll();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				lastString = editText.getText();
				// L.out("lastString: " + lastString);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// L.out("onTextChanged: ");
				// L.out("setNamedValue: " + this + " " + getValue());
				SelfActionFragment.getActionStatus.setNamedValue(field.control, getValue());
				setSideEffect(SelfActionFragment.getActionStatus);
			}

		});
		// L.out("edittext: " + editText + " " + field.toString());
		// L.out("getTaskValue(): " + getTaskValue());
		// editText.setText(getTaskValue());
		// validateAll();
		return editText;
	}

	@Override
	public boolean validate() {
		// L.out("validate foo: " + foobar + " edit: " + editText);
		if (editText == null) {
			// L.out("editText is null for " + field.toString());
			return true;
		}
		// setValue();
		Editable foo = editText.getText();
		String text = foo.toString();
		// L.out("validate textField: #" + text + "#" + text.length());
		if (titleView == null) {
			// L.out("titleView is null");
			return true;
		}
		if (field.required) {
			if (text != null && text.equals("")) {
				titleView.setTextColor(Color.parseColor(REQUIRED));
				return false;
			}
			else
				titleView.setTextColor(Color.parseColor(REQUIRED_PRESENT));

		} else
			titleView.setTextColor(Color.parseColor(OPTIONAL));

		return true;
	}

	@Override
	public ContentValues addValue(ContentValues contentValues) {
		String text = editText.getText().toString();
		if (text != null && !text.equals("")) {
			// L.out("addValue text: " + text + " fieldName: " +
			// field.fieldName);
			contentValues.put(field.fieldName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		if (field.required) {
			String text = editText.getText().toString();
			if (text == null || text.equals("")) {
				// L.out("text: " + text);
				contentValues.put(field.name, "Not Entered!");
			}
		}
		return contentValues;
	}

	@Override
	public String getValue() {
		String text = editText.getText().toString();
		if (numLines == 1)
			text = text.replace("\n", "");
		return text;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(field.control, getValue());
	}

	public String getTaskValue() {
		// L.out("field: " + field);
		// L.out("task: " + task);
		String text = SelfActionFragment.getActionStatus.getNamedValue(field.control);
		// L.out("getTaskValue: " + text + " " + field.control);
		return text;
	}

	@Override
	void onRestoreInstanceState() {
		String text = getTaskValue();
		// L.out("text: " + text);
		editText.setText(text);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		String text = outState.getString(field.control);
		// L.out("text: " + text);
		editText.setText(outState.getString(field.control));
	}

	public void setNumLines(int numLines) {
		this.numLines = numLines;

	}
}
