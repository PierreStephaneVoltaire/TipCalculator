package com.example.steph.tipcalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher, AdapterView.OnItemSelectedListener {
    //declare variables
    Spinner percentageSpinner;
    Spinner numberOfPeopleSpinner;
    CheckBox addHst;
    EditText amount;
    EditText otherPercent;
    TextView Total;
    Button clearBtn, CalculateBtn;
    TextView totalTip, totalPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize widgets
        initializeWidgets();
        // make the adapters for the spinners
        AddAdaptersToSpinners(percentageSpinner, numberOfPeopleSpinner);
        //clearBtn button function: clears all fields hides the Other % for Tip field and resets the # people spinner to 1 person
        clearBtn.setOnClickListener(this);
        //CalculateBtn Function
        CalculateBtn.setOnClickListener(this);
        //reset everything when the percentageSpinner spinner changes
        percentageSpinner.setOnItemSelectedListener(this);
        //reset everything when the numberOfPeopleSpinner Spinner changes
        numberOfPeopleSpinner.setOnItemSelectedListener(this);
        //reset everything when the amount Textfields changes
        amount.addTextChangedListener(this);
        // changes everything when the checkBox changes
        addHst.setOnCheckedChangeListener(this);
        //changes everything when the when the otherPercent Textfeild changes
        otherPercent.addTextChangedListener(this);
    }

    public void initializeWidgets() {
        percentageSpinner = (Spinner) findViewById(R.id.percentage);
        numberOfPeopleSpinner = (Spinner) findViewById(R.id.numberOfPeople);
        addHst = (CheckBox) findViewById(R.id.hst);
        amount = (EditText) findViewById(R.id.amount);
        otherPercent = (EditText) findViewById(R.id.otherPercentage);
        Total = (TextView) findViewById(R.id.total);
        CalculateBtn = (Button) findViewById(R.id.calculate);
        clearBtn = (Button) findViewById(R.id.clear);
        totalTip = (TextView) findViewById(R.id.totalTip);
        totalPeople = (TextView) findViewById(R.id.totalPeople);
    }

    //clear all
    public void ClearAll(TextView Total, TextView totalTip, TextView totalPeople) {
        //clear all 3 TextViews
        Total.setText("");
        totalTip.setText("");
        totalPeople.setText("");
    }

    // DoCalculations(percentageSpinner, amount, otherPercent, addHst, numberOfPeopleSpinner);
    /*display results*/
    public void DoCalculations(Spinner percentage, EditText amount, EditText otherPercent, CheckBox addHst, Spinner numberOfPeople) {
        double totalTip, subTotal;
        //check if the percentageSpinner spinner is set to spinner and change to formula accordingly
        if (percentage.getSelectedItem().toString().matches("Other")) {
            //if it's checked , the CalculateTip and subTotal methods will use the other percent textView
            totalTip = CalculateTip(Double.parseDouble(amount.getText().toString()), Double.parseDouble(otherPercent.getText().toString()), addHst.isChecked());

            subTotal = CalculateSubTotal(Double.parseDouble(amount.getText().toString())
                    , addHst.isChecked());
        } else {
            //else use the value that is selected in the percentageSpinner spinner
            totalTip = CalculateTip(Double.parseDouble(amount.getText().toString()), Double.parseDouble(percentage.getSelectedItem().toString()), addHst.isChecked());

            subTotal = CalculateSubTotal(Double.parseDouble(amount.getText().toString()), addHst.isChecked());
        }
        //get the Hst amount *0.13
        double tax = (Math.round((Double.parseDouble(amount.getText().toString()) * 0.13) * 100) / 100.00);
        //divide subtotal by number of ppl
        double tipPerPerson = CalculatePricePerPerson(Double.parseDouble(numberOfPeople.getSelectedItem().toString()), subTotal, totalTip);
        GetResultAndSetTextFields(subTotal, tax, addHst, totalTip, tipPerPerson, numberOfPeople);
    }

    //toggle percentageSpinner editText visibility
    public void ResetOnUserInputChange(TextView Total, EditText otherPercent, Spinner percentage, TextView totalPeople, TextView totalTip) {
        //reset all texviews
        Total.setText("");
        totalPeople.setText("");
        totalTip.setText("");
        //hide other textview
        String percent = percentage.getSelectedItem().toString();
        if (percent.matches("Other")) {
            otherPercent.setVisibility(View.VISIBLE);
        } else {
            otherPercent.setVisibility(View.INVISIBLE);
        }
    }

    //print calculations
    public void GetResultAndSetTextFields(double subtotal, double hst, CheckBox addHst, double tip, double tipPerPerson, Spinner numberOfPeople) {
        //  if hst is checked add it to the total textview
        if (addHst.isChecked()) {
            Total.setText("Total is: $" + (subtotal + tip) + "(" + hst + "hst)");
            totalTip.setText("Tip: $" + tip);
        }// else print just the subtotal
        else {
            Total.setText("Total is:$ " + (subtotal + tip));
            totalTip.setText("Tip: $" + tip);
        }
        //if the number of people is 1 don't print in the per people textview
        if (Integer.parseInt(numberOfPeople.getSelectedItem().toString()) != 1) {
            totalPeople.setText("per person: $" + tipPerPerson);
        }

    }

    //make adapters for spinners
    public void AddAdaptersToSpinners(Spinner percentageSpinner, Spinner numberOfPeopleSpinner) {
        // get the array of percentages
        String[] listOfPercentages = getResources().getStringArray(R.array.percentages);
        // get the array of people
        String[] listOfPeople = getResources().getStringArray(R.array.people);
        //create an instance of the percent adapter
        ArrayAdapter<String> percentAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfPercentages);
        //create an instance of the people adapter
        ArrayAdapter<String> peopleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfPeople);
        //set views
        percentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        peopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //assign adapters to spinners
        percentageSpinner.setAdapter(percentAdapter);
        numberOfPeopleSpinner.setAdapter(peopleAdapter);
    }

    //clear text
    public void ClearUsingBtn(EditText amount, CheckBox AddHst, EditText otherPercent, Spinner numberOfPeople, Spinner percentage) {
        amount.setText("");
        otherPercent.setText("");
        //reset spinners
        percentage.setSelection(0, true);
        numberOfPeople.setSelection(0, true);
        //uncheck checkbox
        if (AddHst.isChecked()) {
            AddHst.toggle();
        }
    }

    //calculate per person
    public double CalculatePricePerPerson(double people, double fullsum, double tip) {
        return Math.round(((fullsum + tip) / people) * 100) / 100.00;
    }

    //calculate tip
    public double CalculateTip(double amount, double tip, boolean Hst) {
        if (Hst) {
            return (Math.round(((amount * (tip / 100)) * 1.13 * 100)) / 100.00);
        } else {
            return (Math.round(((amount * (tip / 100)) * 100)) / 100.00);
        }

    }

    //return subtotal
    public double CalculateSubTotal(double amount, boolean Hst) {
        if (Hst) {
            return (Math.round(((amount)) * 1.13 * 100) / 100.00);
        } else {
            return (Math.round((amount) * 100.00) / 100);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == CalculateBtn) {
            try {
                DoCalculations(percentageSpinner, amount, otherPercent, addHst, numberOfPeopleSpinner);
            } catch (NumberFormatException E) {
                String percent = percentageSpinner.getSelectedItem().toString();
                if (percent.matches("Other")) {
                    if (otherPercent.getText().toString().isEmpty()) {
                        otherPercent.setError("Please Enter a Valid Number");
                    }
                }
                if (amount.getText().toString().isEmpty()) {
                    amount.setError("Please Enter a valid number");
                }
            }
        } else if (v == clearBtn) {
            ClearUsingBtn(amount, addHst, otherPercent, numberOfPeopleSpinner, percentageSpinner);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ClearAll(Total, totalTip, totalPeople);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ClearAll(Total, totalTip, totalPeople);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ResetOnUserInputChange(Total, otherPercent, percentageSpinner, totalPeople, totalTip);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
