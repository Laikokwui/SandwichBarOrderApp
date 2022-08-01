package com.example.fragment_mysandwichbar;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FragmentTwo extends Fragment {
    // TODO: Rename and change types of parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private HashMap<String, Double> selections = new HashMap<>();
    private HashMap<String, Double> addon_selection = new HashMap<>();
    private ArrayList<RadioButton> radioButtons = new ArrayList<>();
    private TextView textView_display_price;
    private ImageButton ImageButton_place_order, ImageButton_reset;
    private double total_price, discount;
    private onFragmentInteractionListener onFragmentListener2;

    // TODO: Rename and change types of parameters
    private int mParam1;
    private Double mParam2;
    private String mParam3;

    public FragmentTwo() {}

    public interface onFragmentInteractionListener {
        void PassData(String command);
    }

    public static FragmentTwo newInstance(int filling_count, double previous_price, String order_message) {
        FragmentTwo fragment = new FragmentTwo();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, filling_count);
        args.putDouble(ARG_PARAM2, previous_price);
        args.putString(ARG_PARAM3, order_message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getDouble(ARG_PARAM2);
            mParam3 = Objects.requireNonNull(getArguments().getString(ARG_PARAM3));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_two, container, false);
        initializeUI(view);

        for (int i = 0; i < radioButtons.size(); i++){
            radioButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRadioButtonClicked();
                }
            });
        }

        ImageButton_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayOrderAlertBox();
            }
        });

        ImageButton_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentListener2.PassData("reset");
            }
        });

        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                onFragmentListener2.PassData("");
            }
        });

        CalculateTotalPrice();
        DisplayPrice(total_price);

        return view;
    }

    public void initializeUI(View view) {
        radioButtons.add((RadioButton) view.findViewById(R.id.radiobutton_tea));
        radioButtons.add((RadioButton) view.findViewById(R.id.radiobutton_coffee));
        radioButtons.add((RadioButton) view.findViewById(R.id.radiobutton_french_fries));

        textView_display_price = view.findViewById(R.id.textView_display_price);

        ImageButton_place_order = view.findViewById(R.id.imageButton_placeorder);
        ImageButton_reset = view.findViewById(R.id.imageButton_reset);

        selections.put(getString(R.string.tea), 3.50);
        selections.put(getString(R.string.coffee), 4.50);
        selections.put(getString(R.string.french_fries), 5.00);
    }

    public void onRadioButtonClicked() {
        CalculateTotalPrice();
        DisplayPrice(total_price);
    }

    private String GetAddonName(int i) {
        String name = "";
        if (i == 0) name = getString(R.string.tea);
        if (i == 1) name = getString(R.string.coffee);
        if (i == 2) name = getString(R.string.french_fries);
        return name;
    }

    private double GetAddonPrice(int i) {
        double price = 0.00;
        if (i == 0) price = selections.get(getString(R.string.tea));
        if (i == 1) price = selections.get(getString(R.string.coffee));
        if (i == 2) price = selections.get(getString(R.string.french_fries));
        return price;
    }

    private void GetSelection() {
        for (int i = 0; i < radioButtons.size(); i++) {
            if (radioButtons.get(i).isChecked()) addon_selection.put(GetAddonName(i), GetAddonPrice(i));
        }
    }

    private void CalculateTotalPrice() {
        total_price = Double.valueOf(mParam2);
        GetSelection();
        if (!addon_selection.isEmpty()) {
            for (double value: addon_selection.values()) {
                if (mParam1 == 3) {
                    discount = value * 0.2;
                    total_price += value - discount;
                }
                else {
                    total_price += value;
                }
            }
            addon_selection.clear();
        }
    }

    private String OrderMessage() {
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        String menu = mParam3;
        GetSelection();
        if (!addon_selection.isEmpty()){
            for (String key : addon_selection.keySet()) {
                menu += key + ": " + String.format("RM %s", df.format(addon_selection.get(key)));
            }
            if (mParam1 == 3) {
                menu += " (-" + String.format("RM %s", df.format(discount)) + ")";
            }
            addon_selection.clear();
            menu += "\n";
        }
        menu += "======================\n" + "Total: " + String.format("RM %s", df.format(total_price)) + "\n======================";
        return menu;
    }

    private void DisplayOrderAlertBox() {
        new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog)
            .setTitle("Your Order")
            .setMessage(OrderMessage())
            .setPositiveButton("YES", null)
            .show();
    }

    private void DisplayPrice(double price) {
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        textView_display_price.setText(String.format("RM %s", df.format(price)));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onFragmentListener2 = (FragmentTwo.onFragmentInteractionListener)context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement onFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentListener2 = null;
    }
}