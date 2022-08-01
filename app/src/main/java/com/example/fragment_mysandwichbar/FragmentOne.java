package com.example.fragment_mysandwichbar;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FragmentOne extends Fragment {

    private HashMap<String, Double> selections = new HashMap<>();
    private HashMap<String, Double> fill_selection = new HashMap<>();
    private HashMap<String, Double> side_selection = new HashMap<>();
    private HashMap<String, Double> size_selection = new HashMap<>();
    private ArrayList<String> latest_fill = new ArrayList<>();
    private ArrayList<String> latest_side = new ArrayList<>();
    private ArrayList<String> latest_size = new ArrayList<>();
    private ArrayList<CheckBox> checkboxes = new ArrayList<>();
    private ArrayList<RadioButton> radiobutton = new ArrayList<>();
    private TextView textView_selected_fill, textView_selected_side;
    private double total_price;
    private String order_message;
    private onFragmentInteractionListener onFragmentListener;

    public FragmentOne() {}

    public interface onFragmentInteractionListener {
        void PassData(int filling_count,
                      double total_price,
                      String order_message);
    }

    public static FragmentOne newInstance() {
        FragmentOne fragment = new FragmentOne();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_one, container, false);
        initializeUI(view);

        for (int i = 0; i < checkboxes.size(); i++){
            checkboxes.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckboxClicked(v);
                }
            });
        }

        for (int i = 0; i < radiobutton.size(); i++){
            radiobutton.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioButtonClicked(v);
                }
            });
        }

        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeLeft() {
                if (getFilling_count() > 0) {
                    onFragmentListener.PassData(getFilling_count(), total_price, order_message);
                }
                else {
                    Toast.makeText(getActivity(), "Please select at least one filling!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void initializeUI(View view) {
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_ham));
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_chicken));
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_beef));
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_salmon));
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_kebab));
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_tomato));
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_lettuce));
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_onion));
        checkboxes.add((CheckBox) view.findViewById(R.id.checkbox_cheese));

        radiobutton.add((RadioButton) view.findViewById(R.id.radiobutton_6inch));
        radiobutton.add((RadioButton) view.findViewById(R.id.radiobutton_9inch));
        radiobutton.add((RadioButton) view.findViewById(R.id.radiobutton_12inch));

        textView_selected_fill = view.findViewById(R.id.textView_selected_fill);
        textView_selected_side = view.findViewById(R.id.textView_selected_side);

        selections.put(getString(R.string.ham), 2.50);
        selections.put(getString(R.string.roasted_chicken), 2.00);
        selections.put(getString(R.string.beef_steak), 4.50);
        selections.put(getString(R.string.grilled_salmon), 3.70);
        selections.put(getString(R.string.kebab), 4.00);
        selections.put(getString(R.string.tomato), 1.00);
        selections.put(getString(R.string.lettuce), 1.20);
        selections.put(getString(R.string.onion), 0.50);
        selections.put(getString(R.string.cheese), 1.50);
        selections.put(getString(R.string.six_inch), 7.00);
        selections.put(getString(R.string.nine_inch), 9.50);
        selections.put(getString(R.string.twelve_inch), 13.00);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.radiobutton_6inch:
                if (checked) latest_size.add(GetSizeName(0));
                else latest_size.remove(GetSizeName(0));
            case R.id.radiobutton_9inch:
                if (checked) latest_size.add(GetSizeName(1));
                else latest_size.remove(GetSizeName(1));
            case R.id.radiobutton_12inch:
                if (checked) latest_size.add(GetSizeName(2));
                else latest_size.remove(GetSizeName(2));
        }
        CalculateTotalPrice();
        OrderMessage();
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox_ham:
                if (checked) DisplayLatestFillSelection(0);
                else DisplayPreviousFillSelection(0);
                break;
            case R.id.checkbox_chicken:
                if (checked) DisplayLatestFillSelection(1);
                else DisplayPreviousFillSelection(1);
                break;
            case R.id.checkbox_beef:
                if (checked) DisplayLatestFillSelection(2);
                else DisplayPreviousFillSelection(2);
                break;
            case R.id.checkbox_salmon:
                if (checked) DisplayLatestFillSelection(3);
                else DisplayPreviousFillSelection(3);
                break;
            case R.id.checkbox_kebab:
                if (checked) DisplayLatestFillSelection(4);
                else DisplayPreviousFillSelection(4);
                break;
            case R.id.checkbox_tomato:
                if (checked) DisplayLatestSideSelection(5);
                else DisplayPreviousSideSelection(5);
                break;
            case R.id.checkbox_lettuce:
                if (checked) DisplayLatestSideSelection(6);
                else DisplayPreviousSideSelection(6);
                break;
            case R.id.checkbox_onion:
                if (checked) DisplayLatestSideSelection(7);
                else DisplayPreviousSideSelection(7);
                break;
            case R.id.checkbox_cheese:
                if (checked) DisplayLatestSideSelection(8);
                else DisplayPreviousSideSelection(8);
                break;
        }
        CountFillingCheckBox();
        CalculateTotalPrice();
        OrderMessage();
    }

    private void DisableAllFillingCheckBoxes() {
        for (int i = 0; i < 5; i++) {
            if (!checkboxes.get(i).isChecked()) checkboxes.get(i).setEnabled(false);
        }
    }

    private String GetFillingName(int i) {
        String name = "";
        if (i == 0) { name = getString(R.string.ham); }
        if (i == 1) { name = getString(R.string.roasted_chicken); }
        if (i == 2) { name = getString(R.string.beef_steak); }
        if (i == 3) { name = getString(R.string.grilled_salmon); }
        if (i == 4) { name = getString(R.string.kebab); }
        return name;
    }

    private String GetSideName(int i) {
        String name = "";
        if (i == 5) { name = getString(R.string.tomato); }
        if (i == 6) { name = getString(R.string.lettuce); }
        if (i == 7) { name = getString(R.string.onion); }
        if (i == 8) { name = getString(R.string.cheese);  }
        return name;
    }

    private String GetSizeName(int i) {
        String name = "";
        if (i == 0) { name = getString(R.string.six_inch); }
        if (i == 1) { name = getString(R.string.nine_inch); }
        if (i == 2) { name = getString(R.string.twelve_inch); }
        return name;
    }

    private double GetFillingPrice(int i) {
        double price = 0.00;
        if (i == 0) { price = selections.get(getString(R.string.ham)); }
        if (i == 1) { price = selections.get(getString(R.string.roasted_chicken)); }
        if (i == 2) { price = selections.get(getString(R.string.beef_steak)); }
        if (i == 3) { price = selections.get(getString(R.string.grilled_salmon)); }
        if (i == 4) { price = selections.get(getString(R.string.kebab)); }
        return price;
    }

    private double GetSidePrice(int i) {
        double price = 0.00;
        if (i == 5) { price = selections.get(getString(R.string.tomato)); }
        if (i == 6) { price = selections.get(getString(R.string.lettuce)); }
        if (i == 7) { price = selections.get(getString(R.string.onion)); }
        if (i == 8) { price = selections.get(getString(R.string.cheese)); }
        return price;
    }

    private double GetSizePrice(int i) {
        double price = 0.00;
        if (i == 0) { price = selections.get(getString(R.string.six_inch)); }
        if (i == 1) { price = selections.get(getString(R.string.nine_inch)); }
        if (i == 2) { price = selections.get(getString(R.string.twelve_inch)); }
        return price;
    }

    private void DisplayLatestFillSelection(int i) {
        textView_selected_fill.setText(GetFillingName(i));
        latest_fill.add(GetFillingName(i));
    }

    private void DisplayLatestSideSelection(int i) {
        textView_selected_side.setText(GetSideName(i));
        latest_side.add(GetSideName(i));
    }

    private void DisplayPreviousSideSelection(int i) {
        latest_side.remove(GetSideName(i));
        if (!latest_side.isEmpty()) textView_selected_side.setText(latest_side.get(latest_side.size()-1));
        else textView_selected_side.setText("");
    }

    private void DisplayPreviousFillSelection(int i) {
        latest_fill.remove(GetFillingName(i));
        if (!latest_fill.isEmpty()) textView_selected_fill.setText(latest_fill.get(latest_fill.size() - 1));
        else textView_selected_fill.setText("");
    }

    public void CountFillingCheckBox() {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            checkboxes.get(i).setEnabled(true);
            if (checkboxes.get(i).isChecked()) { count++; }
        }
        if (count >= 3) { DisableAllFillingCheckBoxes(); }
    }

    private int getFilling_count() {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (checkboxes.get(i).isChecked()) { count++; }
        }
        return count;
    }

    private void CountSelections() {
        for (int i = 0; i < checkboxes.size(); i++) {
            if (checkboxes.get(i).isChecked()) {
                if (i < 5) fill_selection.put(GetFillingName(i), GetFillingPrice(i));
                if (i > 4) side_selection.put(GetSideName(i), GetSidePrice(i));
            }
            for (int j = 0; j < radiobutton.size(); j++) {
                if (radiobutton.get(j).isChecked()) size_selection.put(GetSizeName(j), GetSizePrice(j));
            }
        }
    }

    private void CalculateTotalPrice() {
        total_price = 0.00;
        CountSelections();
        if (!fill_selection.isEmpty()) {
            for (double value: fill_selection.values()) {
                total_price += value;
            }
            total_price -= Collections.min(fill_selection.values());
            fill_selection.clear();
        }
        if (!side_selection.isEmpty()) {
            for (double value: side_selection.values()) {
                total_price += value;
            }
            total_price -= Collections.min(side_selection.values());
            side_selection.clear();
        }
        if (!size_selection.isEmpty()) {
            for (double value: size_selection.values()) {
                total_price += value;
            }
            size_selection.clear();
        }
    }

    private String OrderMessage() {
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        order_message = "";
        CountSelections();
        if (!size_selection.isEmpty()) {
            for (String key : size_selection.keySet()) {
                order_message += "Size: " + key + " " + String.format("RM %s", df.format(size_selection.get(key))) + "\n";
            }
            size_selection.clear();
        }
        if (!fill_selection.isEmpty()) {
            for (String key : fill_selection.keySet()) {
                if (Collections.min(fill_selection.values()).equals(fill_selection.get(key))) {
                    fill_selection.replace(key, 0.0);
                }
                order_message += key + ": " + String.format("RM %s", df.format(fill_selection.get(key))) + "\n";
            }
            fill_selection.clear();
        }
        if (!side_selection.isEmpty()) {
            for (String key : side_selection.keySet()) {
                if (Collections.min(side_selection.values()).equals(side_selection.get(key))) {
                    side_selection.replace(key, 0.0);
                }
                order_message += key + ": " + String.format("RM %s", df.format(side_selection.get(key))) + "\n";
            }
            side_selection.clear();
        }
        return order_message;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onFragmentListener = (onFragmentInteractionListener)context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement onFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentListener = null;
    }
}